package zzw.imtest.ui.my

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.api.BasicCallback
import com.google.zxing.Result
import com.google.zxing.client.result.ParsedResult
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.mylhyl.circledialog.CircleDialog
import com.mylhyl.zxing.scanner.OnScannerCompletionListener
import com.mylhyl.zxing.scanner.common.Scanner
import com.mylhyl.zxing.scanner.decode.QRDecode
import kotlinx.android.synthetic.main.activity_scanner.*
import zzw.imtest.R
import zzw.imtest.base.BaseActivity
import zzw.imtest.constant.VariableName
import zzw.imtest.util.L

class ScannerActivity : BaseActivity() ,  OnScannerCompletionListener {
    override fun onScannerCompletion(
        rawResult: Result?,
        parsedResult: ParsedResult?,
        barcode: Bitmap?
    ) {
        if(rawResult == null){
            return
        }
        if(TextUtils.isEmpty(rawResult.text)){
            return
        }

        val ans = rawResult.getText()




        CircleDialog.Builder()
            .setTitle("添加好友")
            .setText("是否添加好友"+ans)
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
                        ContactManager.sendInvitationRequest(ans,
                            VariableName.JIGUANG_APP_KEY,
                            text,object : BasicCallback(){
                                override fun gotResult(p0: Int, p1: String?) {
                                    if(p0==0){

                                        L.t("申请发送成功")
                                    }else{
                                        L.t(p1)
                                    }


                                }

                            })



                        return@setPositiveInput true
                    })
                    .show(supportFragmentManager)




            }
            .show(getSupportFragmentManager())
    }


    var isOpen = false

    override fun initData() {

    }

    override fun initView() {
        scanner_view.setOnScannerCompletionListener(this)
        scanner_view.setDrawText("", false)
        scanner_view.setScanMode(Scanner.ScanMode.QR_CODE_MODE)
        scanner_view.isScanFullScreen(false)
        scanner_view.isHideLaserFrame(false)
        scanner_view.setLaserLineResId(R.drawable.wx_scan_line)

        ll_flashlight.setOnClickListener(View.OnClickListener {
            isOpen = !isOpen
            scanner_view.toggleLight(isOpen)
            tv_flashlight.setBackgroundResource(if (isOpen) R.drawable.scanner_flashlight_pressed else R.drawable.scanner_flashlight_normal)
        })



        tv_album.setOnClickListener {
            PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(1)
                .minSelectNum(1)
                .selectionMode(PictureConfig.SINGLE)
                .previewImage(true)
                .compress(true)
                .forResult(VariableName.REQUEST_CODE_ONE)
        }

        iv_back.setOnClickListener {
            finish()
        }
    }


    override fun onResume() {
        scanner_view.onResume()
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
        scanner_view.onPause()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {

            VariableName.REQUEST_CODE_ONE -> {
                val selectList = PictureSelector.obtainMultipleResult(data)
                if ( selectList != null && selectList.size == 1 && selectList[0] != null) {


                    QRDecode.decodeQR(selectList[0].path, this)

                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_scanner
    }


}
