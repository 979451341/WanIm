package zzw.imtest.adapter;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import zzw.imtest.R;


public class NewFriendAdapter extends BaseQuickAdapter<ContactNotifyEvent, BaseViewHolder> {

    public NewFriendAdapter(int layoutResId, @Nullable List<ContactNotifyEvent> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, ContactNotifyEvent item) {


        JMessageClient.getUserInfo(item.getFromUsername(), new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                if(i==0){
                    userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                        @Override
                        public void gotResult(int i, String s, Bitmap bitmap) {
                            if(i==0){
                                ((ImageView)helper.getView(R.id.iv)).setImageBitmap(bitmap);
                            }
                        }
                    });

                    if(TextUtils.isEmpty(userInfo.getNickname())){
                        helper.setText(R.id.tv,userInfo.getUserName());
                    }else{
                        helper.setText(R.id.tv,userInfo.getNickname());
                    }
                }
            }
        });
       // helper.setText(R.id.tv_name,item.getFromUsername());


        helper.setText(R.id.tv_comment,item.getReason());

















    }
}