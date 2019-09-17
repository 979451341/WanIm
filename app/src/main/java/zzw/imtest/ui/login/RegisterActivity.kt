package zzw.imtest.ui.login

import android.text.TextUtils
import android.widget.Toast
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.api.BasicCallback
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.include_toolbar.*
import zzw.imtest.R
import zzw.imtest.base.BaseActivity
import zzw.imtest.util.L

class RegisterActivity : BaseActivity() {
    override fun initData() {

    }

    override fun initView() {
        toolbar.run {
            setSupportActionBar(this)
            title = "注册"
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener { finish() }
        }

        register_sub.setOnClickListener {
            if(TextUtils.isEmpty(register_username.text.toString())){
                Toast.makeText(this@RegisterActivity,"请输入账号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(register_pwd.text.toString())){
                Toast.makeText(this@RegisterActivity,"请输入密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(register_pwd1.text.toString())){
                Toast.makeText(this@RegisterActivity,"请输入密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            JMessageClient.register(register_username.text.toString(),register_pwd.text.toString(),object :
                BasicCallback(){
                override fun gotResult(p0: Int, p1: String?) {

                    if(p0==0){
                        L.t("注册成功")
                        finish()

                    }else{
                        L.t(p1)

                    }
                }
            })

        }

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_register
    }


}
