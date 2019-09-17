package zzw.imtest.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.DownloadCompletionCallback
import cn.jpush.im.android.api.content.*
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.enums.MessageDirect
import cn.jpush.im.android.api.enums.MessageStatus
import cn.jpush.im.android.api.event.MessageEvent
import cn.jpush.im.android.api.event.MessageRetractEvent
import cn.jpush.im.android.api.event.OfflineMessageEvent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.Message
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import io.github.rockerhieu.emojicon.EmojiconGridFragment
import io.github.rockerhieu.emojicon.EmojiconsFragment
import io.github.rockerhieu.emojicon.emoji.Emojicon
import kotlinx.android.synthetic.main.activity_chat.*
import me.leefeng.promptlibrary.PromptDialog
import zzw.imtest.R
import zzw.imtest.adapter.ChatAdapter
import zzw.imtest.base.BaseActivity
import zzw.imtest.bean.ChatBean
import zzw.imtest.constant.VariableName
import zzw.imtest.util.*
import zzw.imtest.view.SoundTextView
import java.io.File

class ChatActivity : BaseActivity()  , EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener{
    override fun onEmojiconBackspaceClicked(v: View?) {
        EmojiconsFragment.backspace(et)
    }

    override fun onEmojiconClicked(emojicon: Emojicon?) {
        EmojiconsFragment.input(et, emojicon)
    }

    var type=VariableName.SINGLE//0为单聊，1为群聊

    var userName=""
    var nickname=""

    var group_id:Long=0
    var group_name=""

    var list:MutableList<ChatBean> = mutableListOf()

    var adapter:ChatAdapter = ChatAdapter(list)

    var conversation: Conversation?=null

    var chatOptionFragment: ChatOptionFragment= ChatOptionFragment()
    var emotionFragment: EmojiconsFragment = EmojiconsFragment.newInstance(false)



    var dialog : PromptDialog?=null


    var handler: Handler = object : Handler(){
        override fun handleMessage(msg: android.os.Message?) {
            super.handleMessage(msg)

            when(msg?.what){
                VariableName.SCROLL_BOTTOM->{
                    recyc_chat.scrollToPosition(list.size-1)
                }

                VariableName.HIDEN_BOTTOM->{
                    var params=frame_layout.layoutParams
                    params.height= ViewUtil.Dp2px(this@ChatActivity,0f)
                    frame_layout.layoutParams=params
                }

                VariableName.SHOW_BOTTOM->{
                    var params=frame_layout.layoutParams
                    params.height=ViewUtil.Dp2px(this@ChatActivity,270f)
                    frame_layout.layoutParams=params
                }
            }
        }
    }


    override fun initData() {


        type = intent.getIntExtra(VariableName.TYPE,VariableName.SINGLE)

        chatOptionFragment.type = type

        if(type==VariableName.SINGLE){
            userName=intent.getStringExtra(VariableName.DATA)
            nickname=intent.getStringExtra(VariableName.DATA_TWO)
            title_layout.tv.text = nickname


            chatOptionFragment.userName = userName
        }
        if(type==VariableName.GROUP){
            group_id=intent.getLongExtra(VariableName.DATA,0)
            group_name=intent.getStringExtra(VariableName.DATA_TWO)

            title_layout.tv.text = group_name

            iv_phone.visibility = View.INVISIBLE

            chatOptionFragment.group_id = group_id

        }


        //进入会话
        if(type==VariableName.SINGLE){
            conversation= JMessageClient.getSingleConversation(userName)
            if(conversation==null){
                conversation= Conversation.createSingleConversation(userName)
            }
        }else if(type==VariableName.GROUP){
            conversation= JMessageClient.getGroupConversation(group_id)
            if(conversation==null){
                conversation= Conversation.createGroupConversation(group_id)
            }
        }

        //获取通话记录
        if(conversation?.allMessage!=null){
            for( bean in  conversation?.allMessage!!.toMutableList()){
                addMessage(bean)
            }
        }



    }

    override fun initView() {


        initTop()

        initList()
        initInput()
        initOptionEmotion()

        //语音设置
        initVoice()
    }

    fun initTop(){
        iv_message.setOnClickListener {
            var myIntent = Intent(this@ChatActivity,ChatDetailActivity::class.java)
            myIntent.putExtra(VariableName.TYPE,type)
            if(type == VariableName.SINGLE){
                myIntent.putExtra(VariableName.DATA,userName)
            }else{
                myIntent.putExtra(VariableName.DATA,group_id)
            }
            startActivityForResult(myIntent,VariableName.REQUEST_CODE_TWO)

        }

        iv_phone.setOnClickListener {

            var myIntent = Intent(this@ChatActivity,VideoPhoneActivity::class.java)
            myIntent.putExtra(VariableName.TYPE,0)
            myIntent.putExtra(VariableName.DATA,userName)
            startActivity(myIntent)
        }
    }

    var showSound=false
    var playVoiceUtil:PlayVoiceUtil ?= null
    //语音设置
    fun initVoice(){
        playVoiceUtil = PlayVoiceUtil(this,adapter)

        iv_sound.setOnClickListener {
            if(showSound){
                tv_sound.visibility=View.INVISIBLE
                et.visibility=View.VISIBLE
                iv_sound.setImageResource(R.mipmap.sound_record)
            }else{
                tv_sound.visibility=View.VISIBLE
                et.visibility=View.INVISIBLE
                iv_sound.setImageResource(R.mipmap.icon_softkeyboard)

            }
            showSound=!showSound
        }



        tv_sound.mConv=conversation

        tv_sound.onNewMessage= object : SoundTextView.OnNewMessage{
            override fun newMessage(message: Message?) {
                if(message==null){
                    return
                }
                addMessage(message!!)
                var now=list.size
                list.get(now-1).upload=false
                adapter?.notifyItemChanged(now-1)
                // adapter_chat?.notifyDataSetChanged()



                message?.setOnSendCompleteCallback(object :BasicCallback(){
                    override fun gotResult(p0: Int, p1: String?) {
                        if(p0!=0){
                            return
                        }
                        list.get(now-1).upload=true
                        adapter?.notifyItemChanged(now-1)
                        // adapter_chat?.notifyDataSetChanged()
                    }
                })


            }

        }


    }

    fun initList(){
        recyc_chat.adapter = adapter
        recyc_chat.layoutManager = LinearLayoutManager(this)


        adapter.setOnItemChildClickListener { adapter, view, position ->
            if(list.get(position).message.direct==MessageDirect.send){
                return@setOnItemChildClickListener
            }
            if(type==VariableName.SINGLE){
                var myIntent = Intent(this@ChatActivity,UserDetailActivity::class.java)
                myIntent.putExtra(VariableName.DATA,userName)
                startActivity(myIntent)
            }else if(type==VariableName.GROUP){
                var user = list.get(position).message.fromUser

                var myIntent = Intent(this@ChatActivity,UserDetailActivity::class.java)
                myIntent.putExtra(VariableName.DATA,user.userName)
                startActivity(myIntent)
            }

        }


        adapter.setOnItemClickListener { adapter, view, position ->
            //浏览图片
            if(list.get(position).itemType==ChatBean.IMG_RECEIVE||
                list.get(position).itemType==ChatBean.IMG_SEND ){
                var first=  (recyc_chat.layoutManager as LinearLayoutManager)?.findFirstVisibleItemPosition()!!
                var last=  (recyc_chat.layoutManager as LinearLayoutManager)?.findLastVisibleItemPosition()!!

                if(position<first||position>last){
                    return@setOnItemClickListener
                }
                var imageContract = list.get(position).message.content as ImageContent

                var path = ""
                if(!TextUtils.isEmpty(imageContract.localThumbnailPath)){
                    path = imageContract.localThumbnailPath
                }
                dialog?.showLoading("下载图片中")
                imageContract.downloadOriginImage(list.get(position).message,object :
                    DownloadCompletionCallback(){
                    override fun onComplete(p0: Int, p1: String?, p2: File) {
                        dialog?.dismiss()

                        var myIntent = Intent(this@ChatActivity,PreviewImageActivity::class.java)
                        if(p0==0){
                            myIntent.putExtra(VariableName.DATA,p2.path)
                        }else{
                            myIntent.putExtra(VariableName.DATA,path)
                        }
                        startActivity(myIntent)

                    }
                })
            }

            //视频
            if(list.get(position).itemType==ChatBean.VIDEO_RECEIVE||
                list.get(position).itemType==ChatBean.VIDEO_SEND ){
                var message:Message=list.get(position).message

                var videoContent= message.content as VideoContent


                dialog?.showLoading("下载视频中")
                videoContent.downloadVideoFile(message,
                    object :DownloadCompletionCallback(){
                        override fun onComplete(p0: Int, p1: String?, p2: File?) {
                            dialog?.dismiss()
                            if(p2==null||p0!=0){
                                L.t("图片下载失败")
                                return
                            }
                            var intent = Intent(this@ChatActivity,VideoPlayerDetailedActivity::class.java)
                            intent.putExtra("url",p2?.path)
                            startActivity(intent)
                        }
                    })


            }

            //打开文件
            if(list.get(position).itemType==ChatBean.FILE_SEND||list.get(position).itemType==ChatBean.FILE_RECEIVE){
                var message:Message=list.get(position).message

                var content= message.content as FileContent
                var fileName=content.fileName
                var extra=content.getStringExtra("video")
                if(extra!=null){
                    fileName= message.serverMessageId.toString()+"."+extra
                }
                var path=content.localPath
                if(path!=null&&File(path).exists()){
                    var newPath=VariableName.FILE_DIR+fileName
                    var file=File(newPath)
                    if(file.exists()&&file.isFile){
                        browseDocument(fileName,newPath)
                    }else{
                        dialog?.showLoading("")
                        FileHelper.getInstance().copyFile(fileName,path,this,
                            object : FileHelper.CopyFileCallback{
                                override fun copyCallback(uri: Uri?) {
                                    dialog?.dismiss()
                                    browseDocument(fileName,newPath)
                                }
                            })
                    }
                }else{
                    dialog?.showLoading("下载文件中")
                    content.downloadFile(message,object : DownloadCompletionCallback(){
                        override fun onComplete(p0: Int, p1: String?, p2: File?) {
                            dialog?.dismiss()
                            if(p0==0){
                                L.t("下载成功")
                            }
                        }
                    })
                }
            }


            //播放录音
            if(list.get(position).itemType==ChatBean.VOICE_SEND||list.get(position).itemType==ChatBean.VOICE_RECEIVE){
                playVoiceUtil?.playVoice(list,position)
            }

        }



    }

    fun initInput(){

        //发送文字或者表情包
        tv_send.setOnClickListener {
            sendTextMessage(et.text.toString())
            et.setText("")

        }

        //输入文字后，出现发送按钮，否则隐藏
        et.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(TextUtils.isEmpty(s.toString()) ){
                    iv_option.visibility= View.VISIBLE
                    tv_send.visibility= View.INVISIBLE
                }else{
                    iv_option.visibility= View.INVISIBLE
                    tv_send.visibility= View.VISIBLE
                }
            }

        })





    }


    var showOption=false
    var showEmoji=false
    var keyBoardShow = false

    fun initOptionEmotion(){



        supportFragmentManager
            .beginTransaction()
            .add(R.id.frame_layout, chatOptionFragment!!)
            .commit()

        supportFragmentManager
            .beginTransaction()
            .add(R.id.frame_layout, emotionFragment!!)
            .commit()

        supportFragmentManager
            .beginTransaction()
            .hide( chatOptionFragment!!)
            .commit()

        supportFragmentManager
            .beginTransaction()
            .hide( emotionFragment!!)
            .commit()


        handler.sendEmptyMessage(VariableName.HIDEN_BOTTOM)


        iv_emoji.setOnClickListener {
            //软键盘和表情版不能同时出现
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0); //强制隐藏键盘

            if(showOption){
                showOption=false
                hideOption()
            }

            showHideEmoji(!showEmoji)
            showEmoji=!showEmoji
        }



        //选项弹出
        iv_option.setOnClickListener {
            //软键盘和表情版不能同时出现
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0); //强制隐藏键盘


            if(showEmoji){
                showEmoji=false
                showHideEmoji(showEmoji)
            }
            if(showOption){
                showOption=false
                hideOption()
            }else{
                showOption=true
                showOption()

            }
        }

        SoftKeyBoardListener.setListener(this,object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener{
            override fun keyBoardHide(height: Int) {
                //  Toast.makeText(this@ChatActivity, "键盘隐藏 高度" + height, Toast.LENGTH_SHORT).show();
                keyBoardShow = false
            }

            override fun keyBoardShow(height: Int) {
                //  Toast.makeText(this@ChatActivity, "键盘显示 高度" + height, Toast.LENGTH_SHORT).show();
                keyBoardShow = true

                showEmoji = false
                showOption = false


                handler.sendEmptyMessage(VariableName.HIDEN_BOTTOM)


            }

        })




    }



    //控制表情版的出现和隐藏
    private fun showHideEmoji(showEmoji: Boolean) {
        if(showEmoji){

            handler.sendEmptyMessage(VariableName.SHOW_BOTTOM)

            supportFragmentManager
                .beginTransaction()
                .show(emotionFragment!!)
                .commit()

        }else{
            handler.sendEmptyMessage(VariableName.HIDEN_BOTTOM)

            supportFragmentManager
                .beginTransaction()
                .hide(emotionFragment!!)
                .commit()
        }

    }

    //显示选择
    fun showOption(){

        handler.sendEmptyMessage(VariableName.SHOW_BOTTOM)

        supportFragmentManager
            .beginTransaction()
            .show( chatOptionFragment!!)
            .commit()





    }
    //隐藏多种选择
    fun hideOption(){

        handler.sendEmptyMessage(VariableName.HIDEN_BOTTOM)
        supportFragmentManager
            .beginTransaction()
            .hide( chatOptionFragment!!)
            .commit()



    }

    override fun onResume() {
        super.onResume()
        JMessageClient.enterSingleConversation(userName)

        if(type == VariableName.SINGLE){
            JMessageClient.enterSingleConversation(userName)
        }else{
            JMessageClient.enterGroupConversation(group_id)
        }
    }

    override fun onPause() {
        super.onPause()
        JMessageClient.exitConversation()
    }


    //接受了在线信息

    public fun onEventMainThread(event: MessageEvent) {

        addMessage(event.message)
    }

    //离线消息
    public fun onEvent(event: OfflineMessageEvent) {

        for(bean in event.offlineMessageList){
            addMessage(bean)
        }

    }
    //消息被对方撤回通知事件
    public fun onEvent(event: MessageRetractEvent) {

        for(bean in list){
            if(event.retractedMessage.id == bean.message.id){
                bean.itemType = ChatBean.RETRACT
                adapter?.notifyItemChanged( list.indexOf(bean))
            }
        }

    }



    //消息加入和刷新界面
    fun addMessage(message: Message){

        if(message.status == MessageStatus.send_fail){
            return
        }


        if(message.getContentType() == ContentType.eventNotification){
            return
        }

        if(message.targetType == ConversationType.single){
            val userInfo = message.targetInfo as UserInfo
            val targetId = userInfo.userName
            if(!targetId.equals(userName)){
                return
            }
        }

        if(message.targetType == ConversationType.group){
            val groupInfo = message.targetInfo as GroupInfo
            val targetId = groupInfo.groupID
            if(group_id!=targetId){
                return
            }
        }

        if(message.content is PromptContent){
            list.add(ChatBean(message ,ChatBean.RETRACT))
            adapter?.notifyItemInserted(list.size-1)
            //  adapter_chat?.notifyDataSetChanged()
            handler.sendEmptyMessageDelayed(VariableName.SCROLL_BOTTOM,100)

            return
        }

        when(message.contentType){

            ContentType.text->{
                if(message.direct== MessageDirect.send){
                    list.add(ChatBean(message ,ChatBean.TEXT_SEND))
                }else{
                    list.add(ChatBean(message,ChatBean.TEXT_RECEIVE))
                }
            }
            ContentType.image->{

                if(message.direct== MessageDirect.send){
                    list.add(ChatBean(message,ChatBean.IMG_SEND))
                }else{
                    list.add(ChatBean(message,ChatBean.IMG_RECEIVE))
                }
            }
            ContentType.video->{

                if(message.direct== MessageDirect.send){
                    list.add(ChatBean(message,ChatBean.VIDEO_SEND))
                }else{
                    list.add(ChatBean(message,ChatBean.VIDEO_RECEIVE))
                }
            }

            ContentType.voice->{
                if(message.direct== MessageDirect.send){
                    list.add(ChatBean(message,ChatBean.VOICE_SEND))
                }else{
                    list.add(ChatBean(message,ChatBean.VOICE_RECEIVE))
                }
            }
            ContentType.file->{
                if(message.direct== MessageDirect.send){
                    list.add(ChatBean(message,ChatBean.FILE_SEND))
                }else{
                    list.add(ChatBean(message,ChatBean.FILE_RECEIVE))
                }
            }

            ContentType.location->{
                if(message.direct== MessageDirect.send){
                    list.add(ChatBean(message,ChatBean.ADDRESS_SEND))
                }else{
                    list.add(ChatBean(message,ChatBean.ADDRESS_RECEIVE))
                }
            }
            ContentType.custom->{

                var type = (message.content as CustomContent).getStringValue(VariableName.TYPE)
                if(TextUtils.isEmpty(type)){
                    return
                }

                if(type.equals(VariableName.RED_PACKEGE)){
                    if(message.direct== MessageDirect.send){
                        list.add(ChatBean(message,ChatBean.REDP_SEND))
                    }else{
                        list.add(ChatBean(message,ChatBean.REDP_RECEIVE))
                    }
                }else if(type.equals(VariableName.CARD)){
                    if(message.direct== MessageDirect.send){
                        list.add(ChatBean(message,ChatBean.CARD_SEND))
                    }else{
                        list.add(ChatBean(message,ChatBean.CARD_RECEIVE))
                    }
                }else if(type.equals(VariableName.INVITATION)){
                    if(message.direct== MessageDirect.send){
                        list.add(ChatBean(message,ChatBean.GROUP_INVITA_SEND))
                    }else{
                        list.add(ChatBean(message,ChatBean.GROUP_INVITA_RECEIVE))
                    }
                }else if(type.equals(VariableName.VIDEO_PHONE)){
                    if(message.direct== MessageDirect.send){
                        list.add(ChatBean(message,ChatBean.VIDEO_PHONE_SEND))
                    }else{
                        list.add(ChatBean(message,ChatBean.VIDEO_PHONE_RECEIVE))
                    }
                }


            }
        }

        adapter?.notifyItemInserted(list.size-1)
        //  adapter_chat?.notifyDataSetChanged()
        handler.sendEmptyMessageDelayed(VariableName.SCROLL_BOTTOM,100)
    }




    open fun sendTextMessage(text:String){
        if(!TextUtils.isEmpty(text) ){
            var content=text
            var textContent= TextContent(content)
            var m=conversation?.createSendMessage(textContent)
            var bean =ChatBean(m,ChatBean.TEXT_SEND)
            bean.upload=false
            list.add(bean)

            adapter?.notifyItemInserted(list.size-1)
            // adapter_chat?.notifyDataSetChanged()


            var now=list.size


            handler.sendEmptyMessageDelayed(VariableName.SCROLL_BOTTOM,100)

            m?.setOnSendCompleteCallback(object : BasicCallback(){
                override fun gotResult(p0: Int, p1: String?) {
                    if(p0!=0){
                        return
                    }
                    list.get(now-1).upload=true
                    adapter?.notifyItemChanged(list.size-1)
                    // adapter_chat?.notifyDataSetChanged()


                }

            })
            JMessageClient.sendMessage(m)

        }
    }

    open fun addMessageRefresh(chatbean: ChatBean) {
        list.add(chatbean)
        adapter?.notifyItemInserted(list.size-1)
        handler.sendEmptyMessageDelayed(VariableName.SCROLL_BOTTOM,100)
    }


    fun browseDocument(fileName: String, path: String) {
        try {
            val ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()
            val mimeTypeMap = MimeTypeMap.getSingleton()
            val mime = mimeTypeMap.getMimeTypeFromExtension(ext)
            val file = File(path)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                var uri= FileProvider.getUriForFile(this, "com.yzyt.zzw.im.fileprovider", file);
                intent.setDataAndType(uri, mime)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }else{
                intent.setDataAndType(Uri.fromFile(file), mime)
            }


            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()


        handler.removeCallbacksAndMessages(null)

        if(playVoiceUtil!=null)
            playVoiceUtil!!.mp.stop()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {

            VariableName.REQUEST_CODE_TWO->{
                //清空聊天记录的反馈
                list.clear()
                adapter?.notifyDataSetChanged()
            }
        }

    }


    override fun getLayoutId(): Int {
        return R.layout.activity_chat
    }

}
