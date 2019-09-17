package zzw.imtest.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import zzw.imtest.util.L;

public abstract class BaseActivity extends AppCompatActivity {



    public abstract int getLayoutId();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        setContentView(getLayoutId());



        //订阅接收消息,子类只要重写onEvent就能收到消息
        JMessageClient.registerEventReceiver(this);




        initData();
        initView();




    }






    protected abstract void initView();

    protected abstract void initData();


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //注销消息接收
        JMessageClient.unRegisterEventReceiver(this);
    }


    public void onEventMainThread(LoginStateChangeEvent event){
        LoginStateChangeEvent.Reason reason = event.getReason();
        if(reason == LoginStateChangeEvent.Reason.user_logout){
            L.t("登录失效，重新登录");
        }else if(reason == LoginStateChangeEvent.Reason.user_password_change){
            L.t("修改密码，重新登录");
        }

        if(!isFinishing()){
            JMessageClient.logout();
/*            Intent myIntent = new Intent(this, LoginRegisterActivity.class);
            startActivity(myIntent);

            App.app().getUser().clearUser();
            finish();*/
        }



    }


    public static List<ContactNotifyEvent> friendApply = new ArrayList<>();

    public void onEvent(ContactNotifyEvent event){
        if(event.getType() == ContactNotifyEvent.Type.invite_received){

            boolean has = false;

            for(int i= 0 ;i < friendApply.size() ;i++){
                if(friendApply.get(i).getFromUsername().equals(event.getFromUsername())){
                    has = true;
                }
            }
            if(!has){
                friendApply.add(event);
                L.t("收到了好友邀请");
            }

        }
    }


}
