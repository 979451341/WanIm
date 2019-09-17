package zzw.imtest.ui.maillist

import android.content.Intent
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetUserInfoListCallback
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.UserInfo
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.recyc
import kotlinx.android.synthetic.main.fragment_mail_list.*
import zzw.imtest.R
import zzw.imtest.adapter.ConversationAdapter
import zzw.imtest.adapter.FriendAdapter
import zzw.imtest.base.BaseFragment
import zzw.imtest.constant.VariableName
import zzw.imtest.ui.chat.ChatActivity
import zzw.imtest.ui.chat.UserDetailActivity

class MailListFragment:BaseFragment(){

    var list:MutableList<UserInfo> = mutableListOf()
    var adapter: FriendAdapter = FriendAdapter(R.layout.item_mail_list_content,list)

    override fun initData() {




    }

    override fun initView() {

        recyc.layoutManager= LinearLayoutManager(activity)
        recyc.adapter= adapter
        recyc.setHasFixedSize(true)


        adapter.setOnItemClickListener { adapter, view, position ->

            var myIntent = Intent(activity,UserDetailActivity::class.java)
            myIntent.putExtra(VariableName.DATA,list.get(position).userName)
            startActivity(myIntent)



        }



        ll_new_friend.setOnClickListener {
            var myIntent = Intent(activity,NewFriendActivity::class.java)
            startActivity(myIntent)
        }


        ll_group.setOnClickListener {
            var myIntent = Intent(activity,GroupActivity::class.java)
            startActivity(myIntent)
        }


    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    fun refresh(){
        ContactManager.getFriendList(object : GetUserInfoListCallback() {
            override fun gotResult(i: Int, s: String, data: List<UserInfo>) {
                if (i == 0) {
                    list.clear()
                    list.addAll(data)

                    adapter.notifyDataSetChanged()
                }
            }
        })

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_mail_list
    }


}
