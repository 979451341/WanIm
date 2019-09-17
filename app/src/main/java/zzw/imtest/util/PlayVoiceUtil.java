package zzw.imtest.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.model.Message;
import zzw.imtest.adapter.ChatAdapter;
import zzw.imtest.bean.ChatBean;

public class PlayVoiceUtil {


    Context mContext;
    public final MediaPlayer mp = new MediaPlayer();
    private FileInputStream mFIS;
    private FileDescriptor mFD;
    private boolean mIsEarPhoneOn;
    ChatAdapter adapter;

    public PlayVoiceUtil(Context mContext, ChatAdapter adapter){
        this.mContext=mContext;
        this.adapter = adapter;

        AudioManager audioManager = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        if (audioManager.isSpeakerphoneOn()) {
            audioManager.setSpeakerphoneOn(true);
        } else {
            audioManager.setSpeakerphoneOn(false);
        }
        mp.setAudioStreamType(AudioManager.STREAM_RING);
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }


    public void playVoice(final List<ChatBean> list, final int position){

        if(mp.isPlaying()){
            mp.stop();
            return;
        }

        Message message = list.get(position).message;
        VoiceContent vc=(VoiceContent) message.getContent();

        try {
            mp.reset();
            mFIS = new FileInputStream(vc.getLocalPath());
            mFD = mFIS.getFD();
            mp.setDataSource(mFD);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.prepare();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mp.start();
                    adapter.playVoiceIndex = position;
                    adapter.notifyItemChanged(position);

                }
            });
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    adapter.playVoiceIndex = -1;
                    adapter.notifyItemChanged(position);
                    mp.reset();
/*                    if (mFIS != null) {
                        try {
                            mFIS.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }*/
                    if(list.size()-1>position&&(list.get(position+1).itemType == ChatBean.VOICE_RECEIVE||list.get(position+1).itemType == ChatBean.VOICE_SEND)){
                        playVoice(list,position+1);
                    }

                }
            });


        } catch (Exception e) {
            Toast.makeText(mContext, "文件丢失, 尝试重新获取",
                    Toast.LENGTH_SHORT).show();
            vc.downloadVoiceFile(message, new DownloadCompletionCallback() {
                @Override
                public void onComplete(int status, String desc, File file) {
                    if (status == 0) {
                        Toast.makeText(mContext, "下载完成",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "文件获取失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } finally {
            try {
                if (mFIS != null) {
                    mFIS.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}
