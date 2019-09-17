package zzw.imtest.ui.chat

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.event.MessageEvent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import kotlinx.android.synthetic.main.fragment_chat.*
import zzw.imtest.R
import zzw.imtest.adapter.ConversationAdapter
import zzw.imtest.base.BaseFragment
import zzw.imtest.constant.VariableName
import zzw.imtest.ui.MainActivity
import zzw.imtest.ui.my.ScannerActivity
import zzw.imtest.util.L
import zzw.imtest.view.AddPop

class ChatFragment:BaseFragment(){

    var list:MutableList<Conversation> = mutableListOf()
    var adapter:ConversationAdapter = ConversationAdapter(R.layout.item_main_dialog,list)

    override fun initData() {

        //订阅接收消息,子类只要重写onEvent就能收到消息
        JMessageClient.registerEventReceiver(this)


    }

    override fun initView() {

        recyc.layoutManager= LinearLayoutManager(activity)
        recyc.adapter= adapter
        recyc.setHasFixedSize(true)

        adapter.setOnItemClickListener { adapter, view, position ->

            list.get(position).updateConversationExtra("")
            adapter?.notifyItemChanged(position)

            if(list.get(position).type == ConversationType.single){

                val userInfo = list.get(position).getTargetInfo() as UserInfo

                var myIntent = Intent(activity,ChatActivity::class.java)
                myIntent.putExtra(VariableName.TYPE, VariableName.SINGLE)
                myIntent.putExtra(VariableName.DATA,userInfo.userName)
                myIntent.putExtra(VariableName.DATA_TWO, L.getName(userInfo))
                startActivity(myIntent)
            }else{
                val groupInfo = list.get(position).getTargetInfo() as GroupInfo
                var myIntent = Intent(activity,ChatActivity::class.java)
                myIntent.putExtra(VariableName.TYPE, VariableName.GROUP)
                myIntent.putExtra(VariableName.DATA,groupInfo.groupID)
                myIntent.putExtra(VariableName.DATA_TWO,groupInfo.groupName)
                startActivity(myIntent)
            }
            checkNew()

        }



        iv_add.setOnClickListener {

            var addPop= AddPop(activity)
            addPop.show(iv_add)

            addPop.ll_add.setOnClickListener {

                var myIntent =Intent(activity,AddFriendActivity::class.java)
                startActivity(myIntent)

            }

            addPop.ll_gourp_chat.setOnClickListener {

                var myIntent =Intent(activity,NewGroupChatActivity::class.java)
                startActivityForResult(myIntent,VariableName.REQUEST_CODE_ONE)

            }
            addPop.ll_scanner.setOnClickListener {
                var myIntent =Intent(activity,ScannerActivity::class.java)
                startActivity(myIntent)
            }

        }

    }

    override fun onResume() {
        super.onResume()
        refresh()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when(requestCode){
            VariableName.REQUEST_CODE_ONE->{
                refresh()

            }

        }

    }

    fun refresh(){
        list.clear()
        list.addAll(JMessageClient.getConversationList())
        adapter.notifyDataSetChanged()
    }



    //接受了在线信息

    public fun onEventMainThread(event: MessageEvent) {




            var handlable = false
            var msg = event.message
            if (msg.getTargetType() == ConversationType.single) {
                var userInfo = msg.targetInfo as UserInfo


                for (bean in list) {
                    if (bean.type == ConversationType.single) {
                        var userI = bean.targetInfo as UserInfo

                        if (userI.userName.equals(userInfo.userName)) {

                            bean.updateConversationExtra(VariableName.NEW_MESSAGE)

                            handlable = true

                            adapter?.notifyItemChanged(list.indexOf(bean))

                        }
                    }


                }

                if(!handlable){
                    var conversation = JMessageClient.getSingleConversation(userInfo.userName)
                    if (conversation.targetInfo is UserInfo) {
                        var bean = conversation
                        bean.updateConversationExtra(VariableName.NEW_MESSAGE)
                        list.add(bean)

                    }
                    adapter?.notifyItemInserted(list.size - 1)


                }



            } else if (msg.getTargetType() == ConversationType.group) {


                var groupInfo = msg.targetInfo as GroupInfo
                for (bean in list) {
                    if (bean.type == ConversationType.group) {
                        var userI = bean.targetInfo as GroupInfo

                        if (userI.groupID == groupInfo.groupID) {
                            bean.updateConversationExtra(VariableName.NEW_MESSAGE)

                            handlable = true
                            adapter?.notifyItemChanged(list.indexOf(bean))

                        }

                    }

                }

                if(!handlable){
                    var conversation = JMessageClient.getGroupConversation(groupInfo.groupID)
                    var bean = conversation
                    bean.updateConversationExtra(VariableName.NEW_MESSAGE)

                    list.add(bean)
                    adapter?.notifyItemInserted(list.size - 1)


                }


            }

        checkNew()

    }


    fun checkNew() {

        if (activity == null) {
            return
        }
        var hasNew = false

        for (bean in list) {
            if (bean.getExtra().equals(VariableName.NEW_MESSAGE)) {
                setNew(true)
                hasNew = true
            }

        }


        if (!hasNew) {

            setNew(false)
        }

    }


        fun setNew(news:Boolean){
            if(activity==null){
                return
            }
            (activity as MainActivity).setNew(news)
        }

        override fun getLayoutId(): Int {
        return R.layout.fragment_chat
    }


}
