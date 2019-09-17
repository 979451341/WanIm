package zzw.imtest.ui

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.api.BasicCallback
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.RationaleListener
import kotlinx.android.synthetic.main.activity_main.*
import zzw.imtest.App
import zzw.imtest.R
import zzw.imtest.base.BaseActivity
import zzw.imtest.constant.VariableName
import zzw.imtest.ui.chat.ChatFragment
import zzw.imtest.ui.login.LoginActivity
import zzw.imtest.ui.maillist.MailListFragment
import zzw.imtest.ui.my.MyFragment
import zzw.imtest.util.L
import zzw.imtest.util.SharedPreferencesHelper

class MainActivity : BaseActivity() {


    var one  = ChatFragment()
    var two= MailListFragment()
    var three= MyFragment()

    var index = 0
    var frags: MutableList<Fragment> = mutableListOf()
    var fragTag: MutableList<String> = mutableListOf()


    override fun initData() {
        var userName:String = App.sharedPreferencesHelper.getSharedPreference(VariableName.USERNAME,"") as String
        var passWord:String = App.sharedPreferencesHelper.getSharedPreference(VariableName.PASSWORD,"") as String
        if(TextUtils.isEmpty(userName)){
            var myIntent = Intent(this@MainActivity,LoginActivity::class.java)
            startActivity(myIntent)
            finish()
        }else{
            //登录
            JMessageClient.login(userName, passWord, object : BasicCallback() {
                    override fun gotResult(i: Int, s: String) {

                        if(i==0){
                            L.t("登录成功")
                        }


                    }
                })
        }
    }

    override fun initView() {




        if(frags.size != 3){
            frags.add(one as Fragment)
            frags.add(two as Fragment)
            frags.add(three as Fragment)

            fragTag.add("1")
            fragTag.add("2")
            fragTag.add("3")



            val fm = supportFragmentManager
            val transaction = fm.beginTransaction()
            transaction.add(R.id.fl, frags.get(0), fragTag.get(0))
            transaction.commit()
        }





        ll_dialog.setOnClickListener {
            if(index!=0){
                switchFragment(index,0)
                select(index,0)
            }
        }

        ll_mail_list.setOnClickListener {
            if(index!=1){
                switchFragment(index,1)
                select(index,1)
            }
        }

        ll_my.setOnClickListener {
            if(index!=2){
                switchFragment(index,2)
                select(index,2)
            }
        }





    }




    fun switchFragment(from: Int, to: Int) {
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()


        val fm_one = fm.findFragmentByTag(fragTag.get(from))
        if (fm_one != null) {
            transaction.hide(fm_one)
        }
        val fm_two = fm.findFragmentByTag(fragTag.get(to))
        if (fm_two != null) {
            transaction.show(fm_two)
        } else {
            transaction.add(R.id.fl, frags.get(to), fragTag.get(to))
        }
        transaction.commit()
    }


    fun select(from: Int, to: Int) {
        when (from) {

            0 -> {
                iv_dialog.setImageResource(R.mipmap.dialog_unselect)
                tv_dialog.setTextColor(resources.getColor(R.color.main_black))
            }
            1 -> {
                iv_mail_list.setImageResource(R.mipmap.mail_list_unselect)
                tv_mail_list.setTextColor(resources.getColor(R.color.main_black))
            }
            2 -> {
                iv_my.setImageResource(R.mipmap.my_unselect)
                tv_my.setTextColor(resources.getColor(R.color.main_black))
            }
        }

        when (to) {
            0 -> {
                iv_dialog.setImageResource(R.mipmap.dialog_select)
                tv_dialog.setTextColor(resources.getColor(R.color.main))
            }
            1 -> {
                iv_mail_list.setImageResource(R.mipmap.mail_list_select)
                tv_mail_list.setTextColor(resources.getColor(R.color.main))
            }
            2 -> {
                iv_my.setImageResource(R.mipmap.my_select)
                tv_my.setTextColor(resources.getColor(R.color.main))
            }
        }

        index=to


    }




    var first =true
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if(first){
            first=false


            // 申请多个权限。
            AndPermission.with(this)
                .requestCode(VariableName.REQUEST_CODE_ONE)
                .permission(
                    Permission.STORAGE,
                    Permission.LOCATION, arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO),
                    Permission.CAMERA )
                .callback(this)
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                // 你也可以不设置。
                .rationale(RationaleListener { requestCode, rationale ->
                    // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                    AndPermission.rationaleDialog(this@MainActivity, rationale).show()
                })
                .start()


        }
    }

    open fun setNew(isNew: Boolean){
        if(isNew){
            v_new_dialog.visibility = View.VISIBLE
        }else{
            v_new_dialog.visibility = View.INVISIBLE
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }


}
