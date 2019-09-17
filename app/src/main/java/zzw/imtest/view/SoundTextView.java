package zzw.imtest.view;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Chronometer;
import android.widget.Toast;


import androidx.appcompat.widget.AppCompatTextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import zzw.imtest.R;
import zzw.imtest.util.L;

public class SoundTextView extends AppCompatTextView {

    private Context mContext;

    private Dialog recordIndicator;

    private Chronometer mVoiceTime;

    private MediaRecorder recorder;
    private File myRecAudioFile;

    //依次为开始录音时刻，按下录音时刻，松开录音按钮时刻
    private long startTime, time1, time2;

    public Conversation mConv;

    String fileDir;

    public SoundTextView(Context context) {
        super(context);
        init();
    }

    public SoundTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public SoundTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init(){

        recordIndicator = new Dialog(getContext(), R.style.jmui_record_voice_dialog);
        recordIndicator.setContentView(R.layout.jmui_dialog_record_voice);
        mVoiceTime = (Chronometer) recordIndicator.findViewById(R.id.voice_time);


        //存放录音文件目录
        File rootDir = mContext.getFilesDir();
        fileDir = rootDir.getAbsolutePath() + "/voice";
        File destDir = new File(fileDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        //录音文件的命名格式
        myRecAudioFile = new File(fileDir,
                new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".amr");
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                time1 = System.currentTimeMillis();
                startRecording();
                recordIndicator.show();
                return  true;
            case MotionEvent.ACTION_UP:
                time2 = System.currentTimeMillis();
                if (time2 - time1 < 300) {
                    L.t("录音时间太短");
                    if(recordIndicator.isShowing()){
                        recordIndicator.dismiss();
                    }
                    return true;
                } else if (time2 - time1 < 1000) {
                    L.t("录音时间太短");
                    releaseRecorder();
                }  else if (time2 - time1 < 60000) {
                    releaseRecorder();
                    finishRecord();

                }



                if(recordIndicator.isShowing()){
                    recordIndicator.dismiss();
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                if(recordIndicator.isShowing()){
                    recordIndicator.dismiss();
                }
                break;
        }

        return super.onTouchEvent(event);
    }



    private void startRecording(){
        //录音文件的命名格式
        myRecAudioFile = new File(fileDir,
                new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".amr");
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setOutputFile(myRecAudioFile.getAbsolutePath());
            myRecAudioFile.createNewFile();
            recorder.prepare();
            recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mediaRecorder, int i, int i2) {
                    Log.i("RecordVoiceController", "recorder prepare failed!");
                }
            });
            recorder.start();
            startTime = System.currentTimeMillis();

            mVoiceTime.setBase(SystemClock.elapsedRealtime());
            mVoiceTime.start();



        } catch (IOException e) {
            e.printStackTrace();

            if (myRecAudioFile != null) {
                myRecAudioFile.delete();
            }
            recorder.release();
            recorder = null;
        } catch (RuntimeException e) {

            if (myRecAudioFile != null) {
                myRecAudioFile.delete();
            }
            recorder.release();
            recorder = null;
        }

    }


    public void releaseRecorder() {
        if (recorder != null) {
            try {
                recorder.stop();
            } catch (Exception e) {
                Log.d("RecordVoice", "Catch exception: stop recorder failed!");
            } finally {
                recorder.release();
                recorder = null;
            }
        }
    }


    //录音完毕加载 ListView item
    private void finishRecord() {


            if (myRecAudioFile != null && myRecAudioFile.exists()) {
                MediaPlayer mp = new MediaPlayer();
                try {
                    FileInputStream fis = new FileInputStream(myRecAudioFile);
                    mp.setDataSource(fis.getFD());
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //某些手机会限制录音，如果用户拒接使用录音，则需判断mp是否存在
                if (mp != null) {
                    int duration = mp.getDuration() / 1000;//即为时长 是s
                    if (duration < 1) {
                        duration = 1;
                    } else if (duration > 60) {
                        duration = 60;
                    }
                    try {
                        VoiceContent content = new VoiceContent(myRecAudioFile, duration);

                        if(mConv==null){

                            return;
                        }
                        Message msg = mConv.createSendMessage(content);
                        JMessageClient.sendMessage(msg);
                        if(onNewMessage!=null){
                            onNewMessage.newMessage(msg);
                        }



                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, "若要使用语音功能，请选择允许录音", Toast.LENGTH_SHORT).show();
                }
            }

    }

    public OnNewMessage onNewMessage;
    public interface OnNewMessage{
        public void newMessage(Message message);
    }
}
