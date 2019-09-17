package zzw.imtest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import zzw.imtest.R;


public class SelectLayout extends RelativeLayout {


    public TextView tv_title,tv_content,tv_right;
    ImageView iv_back;


    public SelectLayout(Context context) {
        super(context);
    }

    public SelectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.layout_select, this);

        tv_title=(TextView)findViewById(R.id.tv_title);
        iv_back=(ImageView) findViewById(R.id.iv_back);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectLayout);
        String title = typedArray.getString(R.styleable.SelectLayout_SelectLayout_title);
        typedArray.recycle();//释放资源


        tv_title.setText(title);


    }



}