package zzw.imtest.adapter;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import zzw.imtest.R;

public class GroupAdapter extends BaseQuickAdapter<Long, BaseViewHolder> {

    public GroupAdapter(int layoutResId, @Nullable List<Long> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, Long item) {






        Conversation conversation= JMessageClient.getGroupConversation(item);
        if(conversation==null){
            conversation= Conversation.createGroupConversation(item);
        }
        GroupInfo groupInfo = (GroupInfo)conversation.getTargetInfo() ;

        helper.setText(R.id.tv,groupInfo.getGroupName());
        groupInfo.getAvatarBitmap(new GetAvatarBitmapCallback(){

            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                if(i==0){
                    ((ImageView)helper.getView(R.id.iv)).setImageBitmap(bitmap);
                }else{


                }
            }
        });




    }
}
