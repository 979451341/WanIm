package zzw.imtest.adapter;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


import java.util.List;

import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import zzw.imtest.R;


public class FriendAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {

    public FriendAdapter(int layoutResId, @Nullable List<UserInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, UserInfo item) {


        item.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                if(i == 0){
                    ((ImageView)helper.getView(R.id.iv)).setImageBitmap(bitmap);
                }
            }
        });

        if(TextUtils.isEmpty(item.getNickname())){
            helper.setText(R.id.tv,item.getUserName());
        }else{
            helper.setText(R.id.tv,item.getNickname());
        }


    }
}