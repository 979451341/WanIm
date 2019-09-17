package zzw.imtest.ui.my

import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import kotlinx.android.synthetic.main.fragment_my.*
import zzw.imtest.App
import zzw.imtest.R
import zzw.imtest.base.BaseFragment
import zzw.imtest.ui.login.LoginActivity
import zzw.imtest.util.L

class MyFragment:BaseFragment(){
    override fun initData() {

    }

    override fun initView() {


        rl_info.setOnClickListener {
            var myIntent= Intent(activity,PersonInformActivity::class.java)
            startActivity(myIntent)
        }

        tv_exit.setOnClickListener {

            App.sharedPreferencesHelper.clear()

            var myIntent= Intent(activity,LoginActivity::class.java)
            startActivity(myIntent)
            activity?.finish()
        }

        rl_scanner.setOnClickListener {
            var myIntent= Intent(activity,ScannerActivity::class.java)
            startActivity(myIntent)
        }


    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    fun getData(){
        var userInfo = JMessageClient.getMyInfo()

        userInfo.getAvatarBitmap(object :GetAvatarBitmapCallback(){
            override fun gotResult(p0: Int, p1: String?, p2: Bitmap?) {
                if(p0 == 0){
                    iv_head.setImageBitmap(p2)
                }
            }
        })

        tv_username.text = L.getName(userInfo)
    }

    override fun getLayoutId(): Int {
        return  R.layout.fragment_my
    }

}
