package zzw.imtest;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.view.SurfaceView;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

import cn.jiguang.jmrtc.api.JMRtcClient;
import cn.jiguang.jmrtc.api.JMRtcListener;
import cn.jiguang.jmrtc.api.JMRtcSession;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import io.reactivex.functions.Consumer;
import zzw.imtest.constant.VariableName;
import zzw.imtest.ui.chat.VideoPhoneActivity;
import zzw.imtest.util.L;
import zzw.imtest.util.NotificationClickEventReceiver;
import zzw.imtest.util.SharedPreferencesHelper;

public class App extends Application {



    public static Application app ;

    public static SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;

        //bugly 崩溃上传
        CrashReport.initCrashReport(getApplicationContext(), "ff80887691", false);

        JMessageClient.setDebugMode(true);
        JMessageClient.init(this);
        JMRtcClient.getInstance().initEngine(jmRtcListener);


        sharedPreferencesHelper = new SharedPreferencesHelper(this);


        //注册Notification点击的接收器
        NotificationClickEventReceiver notificationClickEventReceiver = new NotificationClickEventReceiver(this);
    }


    private static JMRtcSession session;//通话数据元信息对象


    static String TAG = "VIDEOPHONE";



    public static void reinitVideo(){
        JMRtcClient.getInstance().initEngine(jmRtcListener);
    }
    public static JMRtcListener jmRtcListener = new JMRtcListener() {
        @Override
        public void onEngineInitComplete(final int errCode, final String errDesc) {
            super.onEngineInitComplete(errCode, errDesc);

            Log.v(TAG,"onEngineInitComplete");
        }

        @Override
        public void onCallOutgoing(JMRtcSession callSession) {
            super.onCallOutgoing(callSession);

            session = callSession;

            Log.v(TAG,"onCallOutgoing");
        }

        @Override
        public void onCallInviteReceived(JMRtcSession callSession) {
            super.onCallInviteReceived(callSession);
            Log.v(TAG,"onCallInviteReceived");
            session = callSession;



            session.getInviterUserInfo(new RequestCallback(){

                @Override
                public void gotResult(int i, String s, Object o) {
                    if(i!=0){
                        JMRtcClient.getInstance().hangup(new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {



                            }
                        });
                        return;
                    }

                    if(o instanceof UserInfo){
                        UserInfo userInfo = (UserInfo) o;
                        userInfo.getUserName();

                        Intent intent = new Intent(App.app, VideoPhoneActivity.class);
                        intent.putExtra(VariableName.TYPE,1);
                        intent.putExtra(VariableName.DATA,userInfo.getUserName());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        App.app.startActivity(intent);


                    }
                }
            });




        }

        @Override
        public void onCallOtherUserInvited(UserInfo fromUserInfo, List<UserInfo> invitedUserInfos, JMRtcSession callSession) {
            super.onCallOtherUserInvited(fromUserInfo, invitedUserInfos, callSession);

            session = callSession;
            Log.v(TAG,"onCallOtherUserInvited");
        }

        //主线程回调
        @Override
        public void onCallConnected(JMRtcSession callSession, SurfaceView localSurfaceView) {
            super.onCallConnected(callSession, localSurfaceView);

            session = callSession;

            Log.v(TAG,"onCallConnected");
        }

        //主线程回调
        @Override
        public void onCallMemberJoin(UserInfo joinedUserInfo, SurfaceView remoteSurfaceView) {
            super.onCallMemberJoin(joinedUserInfo, remoteSurfaceView);
            Log.v(TAG,"onCallMemberJoin");
        }

        @Override
        public void onPermissionNotGranted(final String[] requiredPermissions) {
            Log.v(TAG,"onPermissionNotGranted");
        }

        @Override
        public void onCallMemberOffline(final UserInfo leavedUserInfo, JMRtcClient.DisconnectReason reason) {
            super.onCallMemberOffline(leavedUserInfo, reason);
            Log.v(TAG,"onCallMemberOffline");
            JMRtcClient.getInstance().hangup(new BasicCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage) {

                }
            });
        }

        @Override
        public void onCallDisconnected(JMRtcClient.DisconnectReason reason) {
            super.onCallDisconnected(reason);

            session = null;

            Log.v(TAG,"onCallDisconnected");
        }

        @Override
        public void onCallError(int errorCode, String desc) {
            super.onCallError(errorCode, desc);

            session = null;

            Log.v(TAG,"onCallError");
        }

        @Override
        public void onRemoteVideoMuted(UserInfo remoteUser, boolean isMuted) {
            super.onRemoteVideoMuted(remoteUser, isMuted);
            Log.v(TAG,"onRemoteVideoMuted");
        }
    };

}
