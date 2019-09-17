package zzw.imtest.adapter;


import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


import java.io.File;
import java.text.NumberFormat;
import java.util.List;

import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VideoContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import zzw.imtest.R;
import zzw.imtest.bean.ChatBean;
import zzw.imtest.constant.VariableName;
import zzw.imtest.util.TimeFormat;

public class ChatAdapter extends BaseMultiItemQuickAdapter<ChatBean, BaseViewHolder> {

    public int playVoiceIndex = -1;

    public ChatAdapter(@Nullable List<ChatBean> data) {
        super( data);

        addItemType(ChatBean.TEXT_SEND, R.layout.item_chat_text_send);
        addItemType(ChatBean.TEXT_RECEIVE,R.layout.item_chat_text_receive);

        addItemType(ChatBean.IMG_SEND, R.layout.item_chat_img_send);
        addItemType(ChatBean.IMG_RECEIVE,R.layout.item_chat_img_receive);

        addItemType(ChatBean.VOICE_SEND, R.layout.item_chat_voice_send);
        addItemType(ChatBean.VOICE_RECEIVE,R.layout.item_chat_voice_receive);

        addItemType(ChatBean.FILE_SEND, R.layout.item_chat_file_send);
        addItemType(ChatBean.FILE_RECEIVE,R.layout.item_chat_file_receive);

        addItemType(ChatBean.REDP_SEND, R.layout.item_chat_redp_send);
        addItemType(ChatBean.REDP_RECEIVE,R.layout.item_chat_redp_receive);

        addItemType(ChatBean.ADDRESS_SEND, R.layout.item_chat_address_send);
        addItemType(ChatBean.ADDRESS_RECEIVE,R.layout.item_chat_address_receive);

        addItemType(ChatBean.CARD_SEND, R.layout.item_chat_card_send);
        addItemType(ChatBean.CARD_RECEIVE,R.layout.item_chat_card_receive);

        addItemType(ChatBean.VIDEO_SEND, R.layout.item_chat_img_send);
        addItemType(ChatBean.VIDEO_RECEIVE,R.layout.item_chat_img_receive);

        addItemType(ChatBean.GROUP_INVITA_SEND, R.layout.item_chat_card_send);
        addItemType(ChatBean.GROUP_INVITA_RECEIVE,R.layout.item_chat_card_receive);

        addItemType(ChatBean.VIDEO_PHONE_SEND, R.layout.item_chat_text_send);
        addItemType(ChatBean.VIDEO_PHONE_RECEIVE,R.layout.item_chat_text_receive);


        addItemType(ChatBean.RETRACT,R.layout.item_chat_retract);

    }

    @Override
    protected void convert(final BaseViewHolder helper, ChatBean item) {



        if(item.itemType == ChatBean.RETRACT){
            return;
        }

        if(item.message==null){
            return;
        }

        if(helper.getAdapterPosition()==0){
            TimeFormat timeFormat = new TimeFormat(mContext, item.message.getCreateTime());
            helper.setText(R.id.tv_time,timeFormat.getDetailTime());
            helper.getView(R.id.tv_time).setVisibility(View.VISIBLE);
        }else{
            ChatBean oldBean = getData().get(helper.getAdapterPosition()-1);
            ChatBean nowBean = item;

            if( oldBean!=null&&nowBean!=null){
                if(oldBean.message!=null&&nowBean.message!=null){


                    long oldTime = oldBean.message.getCreateTime();
                    long nowTime = nowBean.message.getCreateTime();

                    // 如果两条消息之间的间隔超过五分钟则显示时间
                    if (nowTime - oldTime > 300000){
                        TimeFormat timeFormat = new TimeFormat(mContext, nowBean.message.getCreateTime());
                        helper.setText(R.id.tv_time,timeFormat.getDetailTime());
                        helper.getView(R.id.tv_time).setVisibility(View.VISIBLE);
                    }else{
                        helper.getView(R.id.tv_time).setVisibility(View.GONE);
                    }

                }else{
                    helper.getView(R.id.tv_time).setVisibility(View.GONE);
                }


            }else{
                helper.getView(R.id.tv_time).setVisibility(View.GONE);
            }


        }



        switch (helper.getItemViewType()){
            case ChatBean.TEXT_SEND:
            case ChatBean.TEXT_RECEIVE:


                helper.setText(R.id.tv, ((TextContent)item.message.getContent()).getText());



                break;


/*                helper.setText(R.id.tv,((TextContent)item.message.getContent()).getText());
                if(item.upload){
                    helper.getView(R.id.pb).setVisibility(View.INVISIBLE);
                }else{
                    helper.getView(R.id.pb).setVisibility(View.VISIBLE);
                }
                break;*/
            case ChatBean.IMG_SEND:
            case ChatBean.IMG_RECEIVE:

                RequestOptions options= new RequestOptions();
                options.centerInside()
                        .placeholder(R.color.white)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);

                ImageContent imageContent = ((ImageContent)item.message.getContent());
                Glide.with(mContext).load(((ImageContent)item.message.getContent()).getLocalThumbnailPath()).apply(options)
                        .into((ImageView) helper.getView(R.id.iv));





                break;

            case ChatBean.VIDEO_SEND:
            case ChatBean.VIDEO_RECEIVE:

                VideoContent videoContent = (VideoContent)item.message.getContent();

                final RequestOptions options2= new RequestOptions();
                options2.centerInside()
                        .placeholder(R.color.white)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);




                videoContent.downloadThumbImage(item.message,new DownloadCompletionCallback(){

                    @Override
                    public void onComplete(int i, String s, File file) {
                        Glide.with(mContext).load(file.getPath()).apply(options2)
                                .into((ImageView) helper.getView(R.id.iv));

                    }
                });




                break;

            case ChatBean.VOICE_RECEIVE:
            case ChatBean.VOICE_SEND:

                helper.setText(R.id.tv,((VoiceContent)item.message.getContent()).getDuration()+"");

                ImageView iv_voice = helper.getView(R.id.iv_voice);
                AnimationDrawable  mVoiceAnimation = (AnimationDrawable) iv_voice.getDrawable();

                mVoiceAnimation.start();

                if(playVoiceIndex == helper.getAdapterPosition()){
                    if(mVoiceAnimation.isRunning()){

                    }else{
                        mVoiceAnimation.start();
                    }
                }else{
                    if(mVoiceAnimation.isRunning()){
                        mVoiceAnimation.stop();
                    }
                }

                break;

            case ChatBean.FILE_SEND:
            case ChatBean.FILE_RECEIVE:
                Number fileSize = ((FileContent)item.message.getContent()).getNumberExtra("fileSize");

                String size = getFileSize(fileSize);
                helper.setText(R.id.tv_name,((FileContent)item.message.getContent()).getFileName())
                        .setText(R.id.tv_size,size);

                break;

            case ChatBean.REDP_SEND:
            case ChatBean.REDP_RECEIVE:

                break;

            case ChatBean.ADDRESS_RECEIVE:
            case ChatBean.ADDRESS_SEND:
                LocationContent addressContent =   (LocationContent)item.message.getContent();
                String detail = addressContent.getAddress();
                String address = addressContent.getStringExtra("path");

                if(!TextUtils.isEmpty(address))
                    helper.setText(R.id.tv, address);
                if(!TextUtils.isEmpty(detail))
                    helper.setText(R.id.tv_detail,detail);


                break;

            case ChatBean.CARD_RECEIVE:
            case ChatBean.CARD_SEND:


                break;

            case ChatBean.GROUP_INVITA_RECEIVE:
            case ChatBean.GROUP_INVITA_SEND:



                break;

            case ChatBean.VIDEO_PHONE_SEND:
            case ChatBean.VIDEO_PHONE_RECEIVE:


                CustomContent videoPhoneContent =   (CustomContent)item.message.getContent();
                String time = videoPhoneContent.getStringValue(VariableName.DATA);

                int t = Integer.parseInt(time);

                helper.setText(R.id.tv,"语音通话 " + (t/60) +"分"+(t%60)+"秒");


                break;

        }


        helper.addOnClickListener(R.id.iv_head);

        item.message.getFromUser().getAvatarBitmap(new GetAvatarBitmapCallback(){

            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                if(i==0)
                ((ImageView)helper.getView(R.id.iv_head)).setImageBitmap(bitmap);
            }
        });




        if(item.upload){
            helper.getView(R.id.pb).setVisibility(View.INVISIBLE);
        }else{
            helper.getView(R.id.pb).setVisibility(View.VISIBLE);
        }
    }





    public static String getFileSize(Number fileSize) {
        NumberFormat ddf1 = NumberFormat.getNumberInstance();
        //保留小数点后两位
        ddf1.setMaximumFractionDigits(2);
        double size = fileSize.doubleValue();
        String sizeDisplay;
        if (size> 1048576.0) {
            double result = size / 1048576.0;
            sizeDisplay = ddf1.format(result) + " MB";
        } else if (size > 1024) {
            double result = size/ 1024;
            sizeDisplay = ddf1.format(result) + " KB";

        } else {
            sizeDisplay = ddf1.format(size) + " B";
        }
        return sizeDisplay;
    }
}
