package zzw.imtest.ui.chat

import android.graphics.Bitmap
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.SurfaceView
import android.view.View
import cn.jiguang.jmrtc.api.JMRtcClient
import cn.jiguang.jmrtc.api.JMRtcListener
import cn.jiguang.jmrtc.api.JMRtcSession
import cn.jiguang.jmrtc.api.JMSignalingMessage
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.callback.GetUserInfoCallback
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_video_phone.*
import zzw.imtest.App
import zzw.imtest.R
import zzw.imtest.base.BaseActivity
import zzw.imtest.constant.VariableName
import zzw.imtest.util.L


class VideoPhoneActivity : BaseActivity() {


    var userName = ""
    private var session: JMRtcSession? = null//通话数据元信息对象



    var time = 0

    var handler:Handler = object :Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if(msg == null){
                return
            }

            if(msg.what == VariableName.REQUEST_CODE_ONE){
                time = time +1
                tv_time.text = (time/60).toString()+"分"+(time%60).toString()+"秒"
                sendEmptyMessageDelayed(VariableName.REQUEST_CODE_ONE,1000)

            }
        }
    }




    override fun onDestroy() {
        super.onDestroy()
        JMRtcClient.getInstance().releaseEngine()

        App.reinitVideo()

        handler.removeCallbacksAndMessages(null)

    }

    var type = 0

    var isHandfsFree = false

    override fun initData() {



        JMRtcClient.getInstance().initEngine(myjmRtcListener)


        type = intent.getIntExtra(VariableName.TYPE,0)
        userName = intent.getStringExtra(VariableName.DATA)


        JMessageClient.getUserInfo(userName,object :GetUserInfoCallback(){
            override fun gotResult(p0: Int, p1: String?, p2: UserInfo?) {
                if(p0==0){
                    tv_name.text = L.getName(p2)
                    p2?.getAvatarBitmap(object :GetAvatarBitmapCallback(){
                        override fun gotResult(p0: Int, p1: String?, p2: Bitmap?) {
                            iv_head.setImageBitmap(p2)
                        }

                    })
                }
            }

        })



        if(type==0){





            ll_receive.visibility=View.GONE
            startCall(userName, JMSignalingMessage.MediaType.AUDIO)

            iv_cancel.setOnClickListener {
                JMRtcClient.getInstance().hangup(object : BasicCallback() {
                    override fun gotResult(responseCode: Int, responseMessage: String) {

                        if(type == 0){
                            sendVideoMessage(object :BasicCallback(){
                                override fun gotResult(p0: Int, p1: String?) {
                                    if(p0!=0){
                                        Log.v("ChatDetailActivity",p1)
                                    }
                                    if(responseCode == 0){
                                        L.t("挂断成功")
                                    }
                                    finish()
                                }

                            })
                        }else{
                            if(responseCode == 0){
                                L.t("挂断成功")
                            }
                            finish()
                        }

                    }
                })
            }

        }else{



            tv_des.text="对方正在拨打语音通话"
            iv_cancel.setOnClickListener {



                JMRtcClient.getInstance().refuse(object : BasicCallback() {
                    override fun gotResult(responseCode: Int, responseMessage: String) {


                            if(responseCode == 0){
                                L.t("拒听电话成功")
                                finish()
                            }else{
                                //  L.t("拒听失败")
                            }
                            finish()




                    }
                })
            }
            iv_receive.setOnClickListener {
                JMRtcClient.getInstance().accept(object : BasicCallback() {
                    override fun gotResult(responseCode: Int, responseMessage: String) {



                        if(responseCode == 0){
                            L.t("接听电话成功")
                            ll_receive.visibility=View.GONE
                            tv_des.text="语音通话中"

                        }else{
                            L.t("接听失败")
                        }
                    }
                })
            }

        }





    }

    override fun initView() {
        iv_handfs_free.setOnClickListener {

            isHandfsFree = !isHandfsFree
            JMRtcClient.getInstance().enableSpeakerphone(isHandfsFree)
            if(isHandfsFree){
                iv_handfs_free.setImageResource(R.mipmap.handfs_free)
            }else{
                iv_handfs_free.setImageResource(R.mipmap.stop_handfs_free)
            }

        }

    }




    private fun startCall(username: String, mediaType: JMSignalingMessage.MediaType) {
        JMessageClient.getUserInfo(username, object : GetUserInfoCallback() {
            override fun gotResult(responseCode: Int, responseMessage: String, info: UserInfo?) {
                if (null != info) {
                    JMRtcClient.getInstance()
                        .call(listOf(info), mediaType, object : BasicCallback() {
                            override fun gotResult(responseCode: Int, responseMessage: String) {

                                if(responseCode==0){
                                    L.t("发起通话成功,等待对方响应")
                                }else{
                                  //  L.t("发起失败"+ responseCode+"  " + responseMessage)

                                    if(responseCode == 7100002){
                                        L.t("对方不在线")
                                    }
                                }

                            }
                        })
                } else {
                    L.t("发起失败")
                }
            }
        })
    }



    internal var myjmRtcListener: JMRtcListener = object : JMRtcListener() {
        override fun onEngineInitComplete(errCode: Int, errDesc: String?) {
            super.onEngineInitComplete(errCode, errDesc)

            Log.v("zzw","onEngineInitComplete")

        }

        override fun onCallOutgoing(callSession: JMRtcSession?) {
            super.onCallOutgoing(callSession)

            session = callSession

            Log.v("zzw","onCallOutgoing")
        }

        override fun onCallInviteReceived(callSession: JMRtcSession?) {
            super.onCallInviteReceived(callSession)
            session = callSession

            Log.v("zzw","onCallInviteReceived")
        }

        override fun onCallOtherUserInvited(
            fromUserInfo: UserInfo?,
            invitedUserInfos: List<UserInfo>?,
            callSession: JMRtcSession?
        ) {
            super.onCallOtherUserInvited(fromUserInfo, invitedUserInfos, callSession)

            session = callSession

            Log.v("zzw","onCallOtherUserInvited")
        }

        //主线程回调
        override fun onCallConnected(callSession: JMRtcSession?, localSurfaceView: SurfaceView?) {
            super.onCallConnected(callSession, localSurfaceView)

            session = callSession


            runOnUiThread {
                tv_des.text="通话中"
                handler.sendEmptyMessageDelayed(VariableName.REQUEST_CODE_ONE,1000)

            }

            Log.v("zzw","onCallConnected")
        }

        //主线程回调
        override fun onCallMemberJoin(joinedUserInfo: UserInfo?, remoteSurfaceView: SurfaceView?) {
            super.onCallMemberJoin(joinedUserInfo, remoteSurfaceView)
            Log.v("zzw","onCallMemberJoin")
        }

        override fun onPermissionNotGranted(requiredPermissions: Array<String>) {

            Log.v("zzw","onPermissionNotGranted")
        }

        override fun onCallMemberOffline(
            leavedUserInfo: UserInfo?,
            reason: JMRtcClient.DisconnectReason?
        ) {
            super.onCallMemberOffline(leavedUserInfo, reason)



            runOnUiThread {
                L.t("对话挂断语音通话")
                Log.v("zzw","onCallMemberOffline")

                JMRtcClient.getInstance().hangup(object : BasicCallback() {
                    override fun gotResult(responseCode: Int, responseMessage: String) {


                        if(type == 0){
                            sendVideoMessage(object :BasicCallback(){
                                override fun gotResult(p0: Int, p1: String?) {
                                    if(p0!=0){
                                        Log.v("ChatDetailActivity",p1)
                                    }
                                    finish()
                                }

                            })
                        }else{
                            finish()
                        }

                    }
                })



            }
        }

        override fun onCallDisconnected(reason: JMRtcClient.DisconnectReason?) {
            super.onCallDisconnected(reason)
            session = null
            Log.v("zzw","onCallDisconnected")



            runOnUiThread {

                L.t("通话结束")
                if(type == 0){
                    sendVideoMessage(object :BasicCallback(){
                        override fun gotResult(p0: Int, p1: String?) {
                            if(p0!=0){
                                Log.v("ChatDetailActivity",p1)
                            }
                            finish()
                        }

                    })
                }else{
                    finish()
                }

            }

        }

        override fun onCallError(errorCode: Int, desc: String?) {
            super.onCallError(errorCode, desc)

            session = null
            Log.v("zzw","onCallError")
        }

        override fun onRemoteVideoMuted(remoteUser: UserInfo?, isMuted: Boolean) {
            super.onRemoteVideoMuted(remoteUser, isMuted)
            Log.v("zzw","onRemoteVideoMuted")
        }
    }

    fun sendVideoMessage(callback:BasicCallback){
        var map=HashMap<String,String>()
        map.put(VariableName.DATA,time.toString())
        map.put(VariableName.TYPE,VariableName.VIDEO_PHONE)


        var conversation: Conversation?=null

        conversation=JMessageClient.getSingleConversation(userName)

        if(conversation==null){
            conversation= Conversation.createSingleConversation(userName)
        }


        var msg: cn.jpush.im.android.api.model.Message?= null
        msg = conversation?.createSendCustomMessage(map)
        JMessageClient.sendMessage(msg)

        msg?.setOnSendCompleteCallback(callback)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_video_phone
    }

}
