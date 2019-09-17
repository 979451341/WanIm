package zzw.imtest.ui.chat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.CreateGroupCallback
import cn.jpush.im.android.api.callback.GetUserInfoListCallback
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import kotlinx.android.synthetic.main.activity_new_group_chat.*
import zzw.imtest.R
import zzw.imtest.adapter.NewGroupChatAdapter
import zzw.imtest.base.BaseActivity
import zzw.imtest.bean.NewGroupBean
import zzw.imtest.constant.VariableName
import zzw.imtest.util.L

class NewGroupChatActivity : BaseActivity() {

    var list:MutableList<NewGroupBean> = mutableListOf()
    var adapter: NewGroupChatAdapter = NewGroupChatAdapter(R.layout.item_new_group_content,list)


    var list_id:MutableList<String> = mutableListOf()

    var type = VariableName.NEW_GROUP

    var groupId = 0L

    override fun initData() {

        type = intent.getIntExtra(VariableName.TYPE,VariableName.NEW_GROUP)
        groupId = intent.getLongExtra(VariableName.DATA,0L)

        ContactManager.getFriendList(object : GetUserInfoListCallback() {
            override fun gotResult(i: Int, s: String, data: List<UserInfo>) {
                if (i == 0) {

                    for(bean in data){
                        list.add(NewGroupBean(bean))
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        })

    }

    override fun initView() {

        recyc.adapter = adapter
        recyc.layoutManager = LinearLayoutManager(this)



        adapter.setOnItemClickListener { adapter, view, position ->
            list.get(position).select=!list.get(position).select
            adapter?.notifyItemChanged(position)
        }




        tv_confirm.setOnClickListener {
            list_id.clear()
            for(bean in list){
                if(bean.select){
                    list_id.add(bean.userInfo.userName)
                }
            }

            if(L.notNull(list_id)){


                var idList =ArrayList<String>()
                for(bean in list_id){
                    idList.add(bean)
                }
                //创建群

                if(type==VariableName.NEW_GROUP){
                    JMessageClient.createGroup("未命名群","",object :CreateGroupCallback(){
                        override fun gotResult(p0: Int, p1: String?, p2: Long) {
                            if(p0 == 0){


                                JMessageClient.addGroupMembers(p2,idList,object :BasicCallback(){
                                    override fun gotResult(p0: Int, p1: String?) {
                                        if(p0 == 0){
                                            L.t("创建成功")
                                            setResult(Activity.RESULT_OK)
                                            finish()
                                        }else{
                                            L.t(p1)
                                        }
                                    }

                                })

                            }else{
                                L.t(p1)
                            }

                        }

                    })
                }else if(type == VariableName.GROUP_ADD){
                    JMessageClient.addGroupMembers(groupId,idList,object :BasicCallback(){
                        override fun gotResult(p0: Int, p1: String?) {
                            if(p0 == 0){
                                L.t("添加成功")
                                finish()
                            }else{
                                L.t(p1)
                            }
                        }

                    })
                }



            }
        }
    }



    override fun getLayoutId(): Int {
        return R.layout.activity_new_group_chat
    }


}
