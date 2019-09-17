package zzw.imtest.ui.chat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.RequestCallback
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.GroupMemberInfo
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.mylhyl.circledialog.CircleDialog
import kotlinx.android.synthetic.main.activity_chat_detail.*
import zzw.imtest.R
import zzw.imtest.adapter.ChatDetailAdapter
import zzw.imtest.base.BaseActivity
import zzw.imtest.bean.NewGroupBean
import zzw.imtest.constant.VariableName
import zzw.imtest.util.L
import java.io.File

class ChatDetailActivity : BaseActivity() {

    var type = VariableName.SINGLE

    var userName = ""
    var groupId = 0L

    var conversation:Conversation ?= null

    var list:MutableList<NewGroupBean> = mutableListOf()

    var adapter: ChatDetailAdapter = ChatDetailAdapter(R.layout.item_chat_detail,list)

    override fun initData() {

        type = intent.getIntExtra(VariableName.TYPE,VariableName.SINGLE)

        if(type == VariableName.SINGLE){
            userName = intent.getStringExtra(VariableName.DATA)


            conversation = JMessageClient.getSingleConversation(userName)
        }else{
            groupId = intent.getLongExtra(VariableName.DATA,0L)


            conversation = JMessageClient.getGroupConversation(groupId)

            switch_edit_name.visibility = View.VISIBLE
            select_group_logo.visibility = View.VISIBLE


        }


    }



    override fun initView() {

        recyc.adapter = adapter
        var layoutManager = GridLayoutManager(this,4)
        recyc.layoutManager = layoutManager

        adapter.setOnItemClickListener { adapter, view, position ->

            if(position == list.size-1){
                var myIntent =Intent(this@ChatDetailActivity,NewGroupChatActivity::class.java)
                myIntent.putExtra(VariableName.TYPE,VariableName.GROUP_ADD)
                myIntent.putExtra(VariableName.DATA,groupId)
                startActivityForResult(myIntent,VariableName.REQUEST_CODE_ONE)
            }
        }


        select_delete.setOnClickListener {
            CircleDialog.Builder()
                .setCanceledOnTouchOutside(false)
                .setCancelable(false)
                .setTitle("清空聊天记录")
                .setText("确认清空聊天记录吗？")
                .setNegative("取消", null)
                .setPositive("确定") { v ->


                    conversation?.deleteAllMessage()

                    setResult(Activity.RESULT_OK)
                }
                .show(supportFragmentManager)
        }


        switch_edit_name.setOnClickListener {
            var dialogFragment = CircleDialog.Builder()
                .setCanceledOnTouchOutside(false)
                .setCancelable(true)
                .setTitle("请输入新的群名")
                .setInputHint("")
                .setInputCounter(20)
                .setNegative("取消", null)
                .setPositiveInput("确定", { text, v ->


                    (conversation!!.targetInfo as GroupInfo).updateName(text,object :BasicCallback(){
                        override fun gotResult(p0: Int, p1: String?) {
                            if(p0 == 0){
                                L.t("修改群名成功")
                            }else{
                                L.t(p1)
                            }
                        }

                    })

                    return@setPositiveInput true
                })
                .show(supportFragmentManager)
        }


        select_group_logo.setOnClickListener {
            //选图片
            PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(1)
                .minSelectNum(1)
                .selectionMode(PictureConfig.SINGLE)
                .previewImage(true)
                .compress(true)
                .forResult(VariableName.REQUEST_CODE_TWO)
        }


        refresh()

    }

    override fun onRestart() {
        super.onRestart()
        refresh()
    }

    fun refresh(){
        if(type == VariableName.GROUP){
            JMessageClient.getGroupMembers(groupId,object :RequestCallback<List<GroupMemberInfo>>(){
                override fun gotResult(p0: Int, p1: String?, p2: List<GroupMemberInfo>?) {
                    if(p2==null){
                        return
                    }
                    if(p0==0){
                        list.clear()
                        for(bean in p2!!){
                            list.add(NewGroupBean(bean.userInfo))
                        }
                        var newGroupBean = NewGroupBean()
                        list.add(newGroupBean)
                        adapter.notifyDataSetChanged()
                    }
                }

            })
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if(data==null){
            return
        }
        when(requestCode){
            VariableName.REQUEST_CODE_TWO -> {
                val selectList = PictureSelector.obtainMultipleResult(data)
                if ( selectList != null && selectList.size == 1 && selectList[0] != null) {

                    (conversation!!.targetInfo as GroupInfo).updateAvatar(File(selectList[0].path),"",object :BasicCallback(){
                        override fun gotResult(p0: Int, p1: String?) {

                            if(p0==0){
                                L.t("更新群头像成功")
                            }
                        }

                    })


                }
            }
        }

    }


    override fun getLayoutId(): Int {
        return R.layout.activity_chat_detail
    }


}
