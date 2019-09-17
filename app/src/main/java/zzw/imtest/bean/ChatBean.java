package zzw.imtest.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import cn.jpush.im.android.api.model.Message;

public class ChatBean implements MultiItemEntity {

    public static final int TEXT_SEND = 1;
    public static final int TEXT_RECEIVE = 2;
    public static final int IMG_SEND = 3;
    public static final int IMG_RECEIVE = 4;
    public static final int VOICE_SEND = 5;
    public static final int VOICE_RECEIVE = 6;
    public static final int FILE_SEND = 7;
    public static final int FILE_RECEIVE = 8;
    public static final int REDP_SEND = 9;
    public static final int REDP_RECEIVE = 10;

    public static final int ADDRESS_SEND = 11;
    public static final int ADDRESS_RECEIVE = 12;

    public static final int CARD_SEND = 13;
    public static final int CARD_RECEIVE = 14;

    public static final int VIDEO_SEND = 15;
    public static final int VIDEO_RECEIVE = 16;

    public static final int GROUP_INVITA_SEND = 17;
    public static final int GROUP_INVITA_RECEIVE = 18;

    public static final int VIDEO_PHONE_SEND = 19;
    public static final int VIDEO_PHONE_RECEIVE = 20;

    public static final int RETRACT = 99;

    public int itemType=1;
  //  public ChatImgBean chatImgBean;
    public Message message;
    public boolean upload=true;




/*
    public ChatBean(ChatImgBean chatImgBean,int type){
        this.chatImgBean=chatImgBean;
        this.itemType=type;
    }
*/

    public ChatBean(Message message, int type){
        this.message=message;
        this.itemType=type;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
