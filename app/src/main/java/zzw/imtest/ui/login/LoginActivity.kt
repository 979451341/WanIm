package zzw.imtest.ui.login

import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.api.BasicCallback
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.include_toolbar.*
import zzw.imtest.App
import zzw.imtest.ui.MainActivity
import zzw.imtest.R
import zzw.imtest.base.BaseActivity
import zzw.imtest.constant.VariableName
import zzw.imtest.util.L
import zzw.imtest.util.SharedPreferencesHelper

class LoginActivity : BaseActivity() {
    override fun initData() {

    }

    override fun initView() {

        toolbar.run {
            setSupportActionBar(this)
            title = "登录"
            setNavigationIcon(R.drawable.ic_close)
            setNavigationOnClickListener { finish() }
        }


        login_sub.setOnClickListener {
            if(TextUtils.isEmpty(login_username.text.toString())){
                Toast.makeText(this@LoginActivity,"请输入账号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(login_pwd.text.toString())){
                Toast.makeText(this@LoginActivity,"请输入密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            JMessageClient.login(login_username.text.toString(),login_pwd.text.toString(),object :
                BasicCallback(){
                override fun gotResult(p0: Int, p1: String?) {
                   if(p0==0){


                       App.sharedPreferencesHelper.put(VariableName.USERNAME,login_username.text.toString())
                       App.sharedPreferencesHelper.put(VariableName.PASSWORD,login_pwd.text.toString())


                       L.t("登录成功")
                       var myIntent= Intent(this@LoginActivity, MainActivity::class.java)
                       startActivity(myIntent)
                       finish()

                   }else{
                       L.t(p1)

                   }



                }
            })

        }

        login_goregister.setOnClickListener {
            var myIntent = Intent(this@LoginActivity,RegisterActivity::class.java)
            startActivity(myIntent)
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }


}
