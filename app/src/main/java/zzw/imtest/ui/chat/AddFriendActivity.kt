package zzw.imtest.ui.chat

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetUserInfoCallback
import cn.jpush.im.android.api.callback.GetUserInfoListCallback
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import com.mylhyl.circledialog.CircleDialog
import kotlinx.android.synthetic.main.activity_add_friend.*
import zzw.imtest.R
import zzw.imtest.base.BaseActivity
import zzw.imtest.constant.VariableName
import zzw.imtest.ui.my.ScannerActivity
import zzw.imtest.util.L

class AddFriendActivity : BaseActivity() {
    override fun initData() {

    }

    override fun initView() {

        rl_scanner.setOnClickListener {
            var myIntent = Intent(this@AddFriendActivity, ScannerActivity::class.java)
            startActivity(myIntent)
        }

        search_layout.et.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(!TextUtils.isEmpty(s.toString())){
                    JMessageClient.getUserInfo(s.toString(), object : GetUserInfoCallback() {
                        override fun gotResult(
                            responseCode: Int,
                            responseMessage: String,
                            info: UserInfo
                        ) {
                            if (responseCode == 0) {
                                if (info.isFriend()) {
                                    L.t("已经是好友")
                                }else{
                                    CircleDialog.Builder()
                                        .setTitle("添加好友")
                                        .setText("是否添加好友"+info.userName)
                                        .setNegative("取消", null)
                                        .setPositive("确定") { v ->



                                            var dialogFragment = CircleDialog.Builder()
                                                .setCanceledOnTouchOutside(false)
                                                .setCancelable(true)
                                                .setTitle("请输入验证信息")
                                                .setInputHint("")
                                                .setInputCounter(20)
                                                .setNegative("取消", null)
                                                .setPositiveInput("确定", { text, v ->
                                                    ContactManager.sendInvitationRequest(info.userName,
                                                        VariableName.JIGUANG_APP_KEY,
                                                        text,object : BasicCallback(){
                                                            override fun gotResult(p0: Int, p1: String?) {
                                                                if(p0==0){

                                                                    L.t("申请发送成功")
                                                                }
                                                                Log.v("zzw",p0.toString()+p1)

                                                            }

                                                        })



                                                    return@setPositiveInput true
                                                })
                                                .show(supportFragmentManager)




                                        }
                                        .show(getSupportFragmentManager())
                                }
                            }
                        }
                    })
                }
            }
        })

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_friend
    }


}
