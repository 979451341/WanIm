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
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.PromptContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import zzw.imtest.R;
import zzw.imtest.constant.VariableName;
import zzw.imtest.util.L;


public class ConversationAdapter extends BaseQuickAdapter<Conversation, BaseViewHolder> {

    public ConversationAdapter(int layoutResId, @Nullable List<Conversation> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, Conversation item) {


        if(item.getTargetInfo() instanceof UserInfo){
            UserInfo userInfo = (UserInfo)item.getTargetInfo() ;
            userInfo.getAvatarBitmap(new GetAvatarBitmapCallback(){

                @Override
                public void gotResult(int i, String s, Bitmap bitmap) {
                    if(i==0){
                        ((ImageView)helper.getView(R.id.iv_head)).setImageBitmap(bitmap);
                    }else{
                        helper.setImageResource(R.id.iv_head,R.mipmap.head_default);
                    }
                }

            });


            helper.setText(R.id.tv_name, L.getName(userInfo));
        }else{
            GroupInfo groupInfo = (GroupInfo)item.getTargetInfo() ;
            groupInfo.getAvatarBitmap(new GetAvatarBitmapCallback(){

                @Override
                public void gotResult(int i, String s, Bitmap bitmap) {
                    if(i==0){
                        ((ImageView)helper.getView(R.id.iv_head)).setImageBitmap(bitmap);
                    }else{
                        helper.setImageResource(R.id.iv_head,R.mipmap.head_default);
                    }
                }

            });

            helper.setText(R.id.tv_name,groupInfo.getGroupName());

        }





        Message lastMsg=item.getLatestMessage();
        if(lastMsg!=null){
            String contentStr;

            switch (lastMsg.getContentType()){
                case image:
                    contentStr = "[图片]";
                    break;
                case voice:
                    contentStr = "[语音]";
                    break;
                case location:
                    contentStr = "[位置]";
                    break;
                case file:
                    contentStr = "[文件]";
                    break;
                case video:
                    contentStr = "[视频]";
                    break;
                case eventNotification:
                    contentStr = "[群组消息]";
                    break;


                case custom:

                    CustomContent addressContent =   (CustomContent)lastMsg.getContent();
                    String type = addressContent.getStringValue(VariableName.TYPE);
                    if(TextUtils.isEmpty(type)){
                        contentStr = "";
                        helper.setText(R.id.tv_content,contentStr);
                        return;
                    }
                    if(type.equals(VariableName.RED_PACKEGE)){
                        contentStr = "[红包]";
                    }else if(type.equals(VariableName.ADDRESS)){
                        contentStr = "[定位]";
                    }else if(type.equals(VariableName.CARD)){
                        contentStr = "[个人名片]";
                    }else if(type.equals(VariableName.INVITATION)){
                        contentStr = "[群邀请]";
                    }else {
                        contentStr = "[语音通话]";
                    }
                    break;
                case prompt:
                    contentStr =  ((PromptContent) lastMsg.getContent()).getPromptText();
                    break;
                default:
                    contentStr = ((TextContent) lastMsg.getContent()).getText();
                    break;
            }

            helper.setText(R.id.tv_content,contentStr);
        }


        if(item.getExtra().equals(VariableName.NEW_MESSAGE)){
            helper.getView(R.id.v_new).setVisibility(View.VISIBLE);

        }else{
            helper.getView(R.id.v_new).setVisibility(View.GONE);
        }

    }
}