package zzw.imtest.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_preview.*
import zzw.imtest.R
import zzw.imtest.base.BaseActivity
import zzw.imtest.constant.VariableName

class PreviewImageActivity : BaseActivity() {
    override fun initData() {

    }

    override fun initView() {

        Glide.with(this).load(intent.getStringExtra(VariableName.DATA)).into(iv)

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_preview
    }


}
