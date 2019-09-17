package zzw.imtest.ui.my

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.mylhyl.circledialog.CircleDialog
import kotlinx.android.synthetic.main.activity_person_inform.*
import zzw.imtest.R
import zzw.imtest.base.BaseActivity
import zzw.imtest.constant.VariableName
import zzw.imtest.util.L
import java.io.File

class PersonInformActivity : BaseActivity() {
    override fun initData() {

    }

    override fun initView() {


        getData()




        rl_head.setOnClickListener {
            //选图片
            PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(1)
                .minSelectNum(1)
                .selectionMode(PictureConfig.SINGLE)
                .previewImage(true)
                .compress(true)
                .forResult(VariableName.REQUEST_CODE_ONE)
        }


        info_username.setOnClickListener {
            var dialogFragment = CircleDialog.Builder()
                .setCanceledOnTouchOutside(false)
                .setCancelable(true)
                .setTitle("请输入昵称")
                .setInputHint("")
                .setInputCounter(20)
                .setNegative("取消", null)
                .setPositiveInput("确定", { text, v ->
                    var userInfo = JMessageClient.getMyInfo()
                    userInfo.nickname = text
                    JMessageClient.updateMyInfo(UserInfo.Field.nickname,userInfo,object :BasicCallback(){
                        override fun gotResult(p0: Int, p1: String?) {
                            if(p0 == 0){
                                L.t("更新昵称成功")

                                getData()
                            }
                        }

                    })
                    return@setPositiveInput true
                })
                .show(supportFragmentManager)

        }


        info_sex.setOnClickListener {

            var list_sex= mutableListOf<String>()
            list_sex.add("男")
            list_sex.add("女")

            var linearLayoutManager= LinearLayoutManager(this)
            CircleDialog.Builder()
                .setTitle("请选择性别")
                .setItems(list_sex,linearLayoutManager){
                        view, position ->
                    var userInfo = JMessageClient.getMyInfo()
                    if(position==0){
                        userInfo?.gender= UserInfo.Gender.male
                    }else{
                        userInfo?.gender= UserInfo.Gender.female
                    }
                    JMessageClient.updateMyInfo(UserInfo.Field.gender,userInfo,object :BasicCallback(){
                        override fun gotResult(p0: Int, p1: String?) {
                            if(p0 == 0){
                                L.t("更新性别成功")
                                getData()
                            }
                        }

                    })
                    return@setItems true
                }
                .setNegative("取消", null)
                .show(supportFragmentManager)

        }



        info_code.setOnClickListener {
            var myIntent = Intent(this@PersonInformActivity,CodeActivity::class.java)
            startActivity(myIntent)
        }


    }

    fun getData(){
        var userInfo = JMessageClient.getMyInfo()

        userInfo.getAvatarBitmap(object : GetAvatarBitmapCallback(){
            override fun gotResult(p0: Int, p1: String?, p2: Bitmap?) {
                if(p0 == 0){
                    iv_head.setImageBitmap(p2)
                }
            }
        })

        info_username.tv_content.text = userInfo.nickname

        if(userInfo.gender == UserInfo.Gender.male){
            info_sex.tv_content.text = "男"
        }else{
            info_sex.tv_content.text = "女"
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if(requestCode==VariableName.REQUEST_CODE_ONE){
            val selectList = PictureSelector.obtainMultipleResult(data)
            if ( selectList != null && selectList.size == 1 && selectList[0] != null) {
                JMessageClient.updateUserAvatar(File(selectList.get(0).path),object : BasicCallback(){
                    override fun gotResult(p0: Int, p1: String?) {
                        if(p0 == 0){
                            L.t("更新头像成功")

                            getData()
                        }
                    }

                })
            }
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_person_inform
    }

}
