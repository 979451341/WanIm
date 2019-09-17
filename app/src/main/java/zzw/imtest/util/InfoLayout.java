package zzw.imtest.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import zzw.imtest.R;


public class InfoLayout extends RelativeLayout {


    public TextView tv_title,tv_content;

    ImageView iv;

    public InfoLayout(Context context) {
        super(context);
    }

    public InfoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.layout_info, this);

        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_content=(TextView)findViewById(R.id.tv_content);
        iv=(ImageView)findViewById(R.id.iv);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InfoLayout);
        String title = typedArray.getString(R.styleable.InfoLayout_InfoLayout_title);
        String content = typedArray.getString(R.styleable.InfoLayout_InfoLayout_content);
        boolean showiv = typedArray.getBoolean(R.styleable.InfoLayout_InfoLayout_showiv,false);
        typedArray.recycle();//释放资源


        tv_title.setText(title);
        tv_content.setText(content);

        if(showiv){
            tv_content.setVisibility(View.INVISIBLE);
            iv.setVisibility(View.VISIBLE);
        }else{

        }


    }



}