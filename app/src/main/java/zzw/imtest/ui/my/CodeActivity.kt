package zzw.imtest.ui.my

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import com.mylhyl.zxing.scanner.encode.QREncode
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_code.*
import me.leefeng.promptlibrary.PromptDialog
import zzw.imtest.R
import zzw.imtest.base.BaseActivity
import zzw.imtest.util.L
import zzw.imtest.util.SaveBitmapToPhoto

class CodeActivity : BaseActivity() {
    override fun initData() {

    }

    override fun initView() {

        var userInfo = JMessageClient.getMyInfo()

        userInfo.getAvatarBitmap(object :GetAvatarBitmapCallback(){
            override fun gotResult(p0: Int, p1: String?, p2: Bitmap?) {
                if(p0 == 0){
                    iv_logo.setImageBitmap(p2)
                }
            }

        })



        io.reactivex.Observable.create(ObservableOnSubscribe<Bitmap> { e ->


            var bitmap = QREncode.Builder(this)
                .setColor((this).resources.getColor(R.color.black))//二维码颜色
                .setContents(userInfo.userName)
                //  .setSize(500)//二维码等比大小
                //  .setLogoBitmap(resource, 90)
                .build().encodeAsBitmap()
            e.onNext(bitmap!!)
        }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { bitmap ->

                iv_code.setImageBitmap(bitmap)
            }


        tv_content.text = L.getName(userInfo)


        tv_save.setOnClickListener {
            var bitmap =  createViewBitmap(ll)

            if (bitmap != null) {
                val dialog = PromptDialog(this)
                dialog.showLoading("正在保存图片")
                io.reactivex.Observable.create(ObservableOnSubscribe<Bitmap> { e ->
                    SaveBitmapToPhoto.saveImageToGallery(this,bitmap!!, System.currentTimeMillis().toString()+".png")
                    // saveBitmap(bitmap!!, System.currentTimeMillis().toString() + "")
                    e.onNext(bitmap!!)
                }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        dialog.dismiss()
                        L.t("保存完毕")
                    }
            }

        }


    }

    fun createViewBitmap(v: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            v.getWidth(), v.getHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        v.draw(canvas)
        return bitmap
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_code
    }


}
