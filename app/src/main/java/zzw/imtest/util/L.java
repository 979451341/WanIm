package zzw.imtest.util;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import cn.jpush.im.android.api.model.UserInfo;
import zzw.imtest.App;

public class L {

    public static void v(String info){
        Log.v("BitPai===>> ",info);
    }
    public static void v(String sort, String info){
        Log.v(sort,info);
    }

    public static void v(Class<?> classz , String info){
        Log.v(classz.getName()+"===>> ",info);
    }

    public static void t(String msg){
        Toast.makeText(App.app, msg, Toast.LENGTH_SHORT).show();
    }
    public static void tLong(String msg){
        Toast.makeText(App.app, msg, Toast.LENGTH_LONG).show();
    }


    public static String getName(UserInfo userInfo){
        if(userInfo == null){
            return "";
        }

        if(TextUtils.isEmpty(userInfo.getNickname())){
            return userInfo.getUserName();
        }else{
            return userInfo.getNickname();
        }
    }

    public static boolean notNull(List list){
        if(list!=null&&list.size()>0){
            return true;
        }else{
            return false;
        }
    }

}
