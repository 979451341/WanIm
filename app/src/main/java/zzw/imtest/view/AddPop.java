package zzw.imtest.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import zzw.imtest.R;


public class AddPop {



    public PopupWindow pop;

    public LinearLayout ll_add,ll_gourp_chat,ll_scanner;


    public AddPop(Context context){

        View view = LayoutInflater.from(context).inflate(R.layout.layout_pop_dialog_add,null,false);
        pop= new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new ColorDrawable());
        pop.setOutsideTouchable(true);
        pop.getContentView().measure(0,0);
        pop.setFocusable(true);

        ll_add=(LinearLayout)view.findViewById(R.id.ll_add);
        ll_gourp_chat=(LinearLayout)view.findViewById(R.id.ll_gourp_chat);
        ll_scanner=(LinearLayout)view.findViewById(R.id.ll_scanner);

    }
    public void show(View v_target){
        if(pop!=null){
            pop.showAsDropDown(v_target,-((pop.getContentView().getMeasuredWidth()-v_target.getWidth())),0);
        }
    }


    public void dismiss(){
        if(pop!=null){
            pop.dismiss();
        }
    }



}
