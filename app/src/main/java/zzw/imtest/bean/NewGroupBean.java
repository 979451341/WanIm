package zzw.imtest.bean;

import cn.jpush.im.android.api.model.UserInfo;

public class NewGroupBean {

    public NewGroupBean(){

    }
    public NewGroupBean(UserInfo info){
        userInfo = info;
    }
    public UserInfo userInfo;
    public boolean select=false;
}
