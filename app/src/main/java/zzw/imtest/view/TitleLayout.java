package zzw.imtest.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import zzw.imtest.R;


public class TitleLayout extends RelativeLayout {


   public TextView tv;
    ImageView iv_back;


    public TitleLayout(Context context) {
        super(context);
    }

    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.layout_title, this);

        tv=(TextView)findViewById(R.id.tv);
        iv_back=(ImageView)findViewById(R.id.iv_back);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleLayout);
        String hint = typedArray.getString(R.styleable.TitleLayout_TitleLayout_title);
        typedArray.recycle();//释放资源


        tv.setText(hint);

        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getContext() instanceof Activity &&getContext()!=null){
                    ((Activity)getContext()).finish();
                }
            }
        });

    }



}