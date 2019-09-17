package zzw.imtest.ui.chat

import android.app.Activity
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.text.TextUtils
import androidx.recyclerview.widget.GridLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.content.FileContent
import cn.jpush.im.android.api.content.ImageContent
import cn.jpush.im.android.api.exceptions.JMFileSizeExceedException
import cn.jpush.im.android.api.model.Message
import cn.jpush.im.api.BasicCallback
import com.leon.lfilepickerlibrary.LFilePicker
import com.leon.lfilepickerlibrary.utils.Constant
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.fragment_chat_option.*
import zzw.imtest.R
import zzw.imtest.adapter.ChatOptionAdapter
import zzw.imtest.base.BaseFragment
import zzw.imtest.bean.ChatBean
import zzw.imtest.bean.ChatOptionBean
import zzw.imtest.constant.VariableName
import zzw.imtest.util.L
import java.io.File
import java.io.FileNotFoundException

class ChatOptionFragment: BaseFragment(){

    var type=VariableName.SINGLE
    var userName = ""
    var group_id:Long=0


    var list:MutableList<ChatOptionBean> = mutableListOf()
    var adapter_option: ChatOptionAdapter= ChatOptionAdapter(R.layout.item_chat_option,list)

    override fun initData() {
        list.clear()
        list.add(ChatOptionBean(R.mipmap.chat_option_photo))
        list.add(ChatOptionBean(R.mipmap.chat_option_file))
/*        list.add(ChatOptionBean(R.mipmap.chat_option_person))
        list.add(ChatOptionBean(R.mipmap.chat_option_address))
        list.add(ChatOptionBean(R.mipmap.chat_option_redpacket))*/
    }

    override fun initView() {
        recyc.adapter=adapter_option
        recyc.layoutManager= GridLayoutManager(activity,4)

        adapter_option.setOnItemClickListener { adapter, view, position ->

            when(position){
                0->{
                    //选图片
                    PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofAll())
                        .maxSelectNum(1)
                        .minSelectNum(1)
                        .selectionMode(PictureConfig.SINGLE)
                        .previewImage(true)
                        .compress(true)
                        .forResult(VariableName.REQUEST_CODE_ONE)
                }
                1->{

                    LFilePicker()
                        .withSupportFragment(this@ChatOptionFragment)
                        .withRequestCode(VariableName.REQUEST_CODE_TWO)
                        .withMaxNum(1)
                        .start()
                }

                else -> {
                    L.t("没做")
                }


            }
        }




    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {

            VariableName.REQUEST_CODE_ONE -> {
                val selectList = PictureSelector.obtainMultipleResult(data)
                if ( selectList != null && selectList.size == 1 && selectList[0] != null) {
                    //所有图片都在这里拿到

                    var messagetype = VariableName.IMG
                    if(PictureMimeType.isPictureType(selectList.get(0).pictureType) == PictureConfig.TYPE_IMAGE ){
                        // L.t("这是图片")
                        messagetype = VariableName.IMG


                        ImageContent.createImageContentAsync( File(selectList[0].path), object : ImageContent.CreateImageContentCallback() {
                            override fun gotResult(responseCode: Int, responseMessage: String, imageContent: ImageContent) {
                                if (responseCode == 0) {
                                    imageContent.setStringExtra(VariableName.TYPE,messagetype)
                                    val msg = (activity as ChatActivity).conversation?.createSendMessage(imageContent)

                                    sendMessage(msg!!,ChatBean.IMG_SEND)

                                }
                            }
                        })

                    }else if(PictureMimeType.isPictureType(selectList.get(0).pictureType) == PictureConfig.TYPE_VIDEO ){
                        // L.t("这是视频")
                        messagetype = VariableName.VIDEO

                        // sendFile(ChatBean.VIDEO_SEND,selectList[0].path)



                        val index:Int = selectList[0].path?.lastIndexOf('/')!!
                        var fileName: String = ""
                        if (index > 0) {
                            fileName = selectList[0].path.substring(index + 1)
                        }

                        var media = MediaMetadataRetriever()
                        media.setDataSource(selectList[0].path)
                        var bitmap = media.getFrameAtTime()

                        var message: Message?=null


                        if(type == VariableName.SINGLE){
                            message  =  JMessageClient.createSingleVideoMessage(userName,VariableName.JIGUANG_APP_KEY,
                                bitmap,"jpeg",
                                File(selectList[0].path),fileName,1000)
                        }else{
                            message  =  JMessageClient.createGroupVideoMessage(group_id,
                                bitmap,"jpeg", File(selectList[0].path),fileName,1000)
                        }

                        sendMessage(message!!,ChatBean.VIDEO_SEND)




                    }



                }
            }


            VariableName.REQUEST_CODE_TWO->{
                val list = data?.getStringArrayListExtra(Constant.RESULT_INFO)
                // Toast.makeText(getApplicationContext(), "选中了" + list?.size + "个文件", Toast.LENGTH_SHORT).show()

                if(list==null){
                    return
                }
                var path=list!!.get(0)
                if(TextUtils.isEmpty(path)){
                    return
                }

                sendFile(ChatBean.FILE_SEND,path)




            }


        }
    }


    fun sendMessage( msg:Message,type:Int){
        var bean = ChatBean(msg, type)
        bean.upload=false
        (activity as ChatActivity).addMessageRefresh(bean)

        var now=(activity as ChatActivity).list.size

        JMessageClient.sendMessage(msg)
        msg?.setOnSendCompleteCallback(object : BasicCallback(){
            override fun gotResult(p0: Int, p1: String?) {
                if(p0!=0){
                    return
                }
                (activity as ChatActivity).list.get(now-1).upload=true
                (activity as ChatActivity).adapter?.notifyDataSetChanged()


            }

        })
    }


    fun sendFile(type:Int,path:String){
        val file = File(path)
        val index:Int = path?.lastIndexOf('/')!!
        val fileName: String
        if (index > 0) {
            fileName = path.substring(index + 1)
            try {
                val substring = path.substring(path.lastIndexOf(".") + 1, path.length)
                val content = FileContent(file, fileName)
                content.setStringExtra("fileType", substring)
                //                                        content.setStringExtra("fileType", entry.getKey().toString());
                content.setNumberExtra("fileSize", file.length())
                val msg = (activity as ChatActivity).conversation?.createSendMessage(content)

                sendMessage(msg!!,type)


            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: JMFileSizeExceedException) {
                e.printStackTrace()
            }

        }
    }




    override fun getLayoutId(): Int {
        return R.layout.fragment_chat_option
    }

}
