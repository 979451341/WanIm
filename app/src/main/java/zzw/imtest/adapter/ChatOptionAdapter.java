package zzw.imtest.adapter;


import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import zzw.imtest.R;
import zzw.imtest.bean.ChatOptionBean;

public class ChatOptionAdapter extends BaseQuickAdapter<ChatOptionBean, BaseViewHolder> {

    public ChatOptionAdapter(int layoutResId, @Nullable List<ChatOptionBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChatOptionBean item) {


        helper.setImageResource(R.id.iv,item.img);


    }
}
