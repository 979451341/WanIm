package zzw.imtest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import zzw.imtest.R;


public class SearchLayout extends RelativeLayout {


    TextView tv;
    public EditText et;


    public SearchLayout(Context context) {
        super(context);
    }

    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.layout_search, this);

        tv=(TextView)findViewById(R.id.tv);
        et=(EditText)findViewById(R.id.et);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchLayout);
        String hint = typedArray.getString(R.styleable.SearchLayout_SearchLayout_hint);
        boolean isInput = typedArray.getBoolean(R.styleable.SearchLayout_SearchLayout_isInput,false);
        typedArray.recycle();//释放资源


        if(isInput){
            et.setVisibility(View.VISIBLE);
            tv.setVisibility(View.INVISIBLE);
            et.setHint(hint);
        }else{
            tv.setText(hint);


        }

    }



}