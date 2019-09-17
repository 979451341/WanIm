package zzw.imtest.adapter;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


import java.util.List;

import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;
import zzw.imtest.R;
import zzw.imtest.bean.NewGroupBean;
import zzw.imtest.util.L;

public class ChatDetailAdapter extends BaseQuickAdapter<NewGroupBean, BaseViewHolder> {

    public ChatDetailAdapter(int layoutResId, @Nullable List<NewGroupBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, NewGroupBean item) {


        if(item.userInfo == null){
            return;
        }

        helper.getView(R.id.iv).setVisibility(View.VISIBLE);
        helper.getView(R.id.tv).setVisibility(View.VISIBLE);

        item.userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                if(i==0){
                    ((ImageView)helper.getView(R.id.iv)).setImageBitmap(bitmap);
                }
            }
        });

        helper.setText(R.id.tv, L.getName(item.userInfo));



    }
}
