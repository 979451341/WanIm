package zzw.imtest.ui.maillist

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.callback.GetGroupIDListCallback
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.GroupInfo
import kotlinx.android.synthetic.main.activity_group.*
import zzw.imtest.R
import zzw.imtest.adapter.GroupAdapter
import zzw.imtest.base.BaseActivity
import zzw.imtest.constant.VariableName
import zzw.imtest.ui.chat.ChatActivity
import zzw.imtest.util.L

class GroupActivity : BaseActivity() {

    var list: MutableList<Long> = mutableListOf()
    var groupAdapter: GroupAdapter = GroupAdapter(R.layout.item_group,list)

    override fun initData() {
        JMessageClient.getGroupIDList(object : GetGroupIDListCallback(){
            override fun gotResult(p0: Int, p1: String?, p2: MutableList<Long>?) {
                if(L.notNull(p2)){

                    list.clear()
                    list.addAll(p2!!)

                    groupAdapter?.notifyDataSetChanged()

                }
            }

        })

    }

    override fun initView() {
        recyc.adapter=groupAdapter
        recyc.layoutManager= LinearLayoutManager(this)

        groupAdapter.setOnItemClickListener { adapter, view, position ->

            var conversation: Conversation? = JMessageClient.getGroupConversation(list.get(position))
            if (conversation == null) {
                conversation = Conversation.createGroupConversation(list.get(position))
            }
            val groupInfo = conversation!!.targetInfo as GroupInfo


            var myIntent= Intent(this@GroupActivity, ChatActivity::class.java)
            myIntent.putExtra(VariableName.TYPE, VariableName.GROUP)
            myIntent.putExtra(VariableName.DATA,list.get(position))
            myIntent.putExtra(VariableName.DATA_TWO, groupInfo.groupName)
            startActivity(myIntent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_group
    }


}
