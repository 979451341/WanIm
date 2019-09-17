package zzw.imtest.ui.chat

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.callback.GetUserInfoCallback
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import com.mylhyl.circledialog.CircleDialog
import kotlinx.android.synthetic.main.activity_user_detail.*
import zzw.imtest.R
import zzw.imtest.base.BaseActivity
import zzw.imtest.constant.VariableName
import zzw.imtest.util.L

class UserDetailActivity : BaseActivity() {

    var userName = ""
    var userInfo:UserInfo ?= null

    override fun initData() {
        userName = intent.getStringExtra(VariableName.DATA)
    }

    override fun initView() {

        JMessageClient.getUserInfo(userName,object :GetUserInfoCallback(){
            override fun gotResult(p0: Int, p1: String?, p2: UserInfo?) {
                if(p0==0){
                    userInfo = p2
                    tv_name.text = L.getName(p2)
                    p2?.getAvatarBitmap(object :GetAvatarBitmapCallback(){
                        override fun gotResult(p0: Int, p1: String?, p2: Bitmap?) {
                            if(p0 == 0){
                                iv_head.setImageBitmap(p2)
                            }
                        }

                    })
                }
            }

        })




        select_delete.setOnClickListener {

            CircleDialog.Builder()
                .setCanceledOnTouchOutside(false)
                .setCancelable(false)
                .setTitle("删除好友")
                .setText("确认删除该好友吗？")
                .setNegative("取消", null)
                .setPositive("确定") { v ->

                    userInfo?.removeFromFriendList(object :BasicCallback(){
                        override fun gotResult(p0: Int, p1: String?) {
                            if(p0 == 0){
                                L.t("删除好友成功")
                            }else{
                                L.t("删除好友失败")
                            }
                        }
                    })


                }
                .show(supportFragmentManager)


        }


        tv_confirm.setOnClickListener {
            var myIntent = Intent(this@UserDetailActivity,ChatActivity::class.java)
            myIntent.putExtra(VariableName.TYPE, VariableName.SINGLE)
            myIntent.putExtra(VariableName.DATA,userInfo?.userName)
            myIntent.putExtra(VariableName.DATA_TWO, L.getName(userInfo))
            startActivity(myIntent)
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_user_detail
    }


}
