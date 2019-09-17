package zzw.imtest.ui.maillist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.callback.GetUserInfoListCallback
import cn.jpush.im.android.api.event.ContactNotifyEvent
import cn.jpush.im.android.api.model.UserInfo
import com.mylhyl.circledialog.CircleDialog
import kotlinx.android.synthetic.main.activity_new_friend.*
import zzw.imtest.R
import zzw.imtest.adapter.NewFriendAdapter
import zzw.imtest.base.BaseActivity
import zzw.imtest.util.L
import cn.jpush.im.api.BasicCallback



class NewFriendActivity : BaseActivity() {

    var list:MutableList<ContactNotifyEvent> = mutableListOf()
    var adapter: NewFriendAdapter = NewFriendAdapter(R.layout.item_new_friend,list)

    override fun initData() {

    }

    override fun initView() {

        list.addAll(BaseActivity.friendApply)

        recyc.adapter = adapter
        recyc.layoutManager = LinearLayoutManager(this)


        adapter.setOnItemClickListener { adapter, view, position ->


            CircleDialog.Builder()
                .setTitle("好友申请")
                .setText("是否通过好友申请")
                .setNegative("取消", null)
                .setPositive("确定") { v ->
                    ContactManager.acceptInvitation(
                        list.get(0).fromUsername,
                        list.get(0).getfromUserAppKey(),
                        object : BasicCallback() {
                            override fun gotResult(responseCode: Int, responseMessage: String) {
                                if (0 == responseCode) {
                                    L.t("接收好友请求成功")
                                    list.removeAt(position)
                                    adapter.notifyItemRemoved(position)
                                } else {
                                    //接收好友请求失败
                                }
                            }
                        })
                }
                .show(getSupportFragmentManager())
        }




    }

    override fun getLayoutId(): Int {

        return R.layout.activity_new_friend
    }



}
