package ims.chat.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetUserInfoCallback
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.android.eventbus.EventBus
import cn.jpush.im.api.BasicCallback
import ims.chat.R
import ims.chat.application.ImsApplication
import ims.chat.database.FriendEntry
import ims.chat.database.FriendRecommendEntry
import ims.chat.entity.Event
import ims.chat.entity.EventType
import ims.chat.entity.FriendInvitation
import ims.chat.ui.activity.FriendInfoActivity
import ims.chat.ui.activity.GroupNotFriendActivity
import ims.chat.ui.activity.SearchFriendDetailActivity
import ims.chat.utils.*
import ims.chat.utils.photochoose.SelectableRoundedImageView

import java.util.ArrayList

class FriendRecommendAdapter(
    private val mContext: Activity, list: MutableList<FriendRecommendEntry>, private val mDensity: Float,
    private val mWidth: Int
) : BaseAdapter() {
    private var mList = ArrayList<FriendRecommendEntry>()
    private val mInflater: LayoutInflater

    init {
        this.mList = list as ArrayList<FriendRecommendEntry>
        this.mInflater = LayoutInflater.from(mContext)
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun getItem(position: Int): Any {
        return mList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_friend_recomend, null)
        }
        val headIcon = ViewHolder.get<SelectableRoundedImageView>(convertView!!, R.id.item_head_icon)
        val name = ViewHolder.get<TextView>(convertView, R.id.item_name)
        val reason = ViewHolder.get<TextView>(convertView, R.id.item_reason)
        val addBtn = ViewHolder.get<TextView>(convertView, R.id.item_add_btn)
        val state = ViewHolder.get<TextView>(convertView, R.id.item_state)
        val itemLl = ViewHolder.get<LinearLayout>(convertView, R.id.friend_verify_item_ll)


        val swp_layout = ViewHolder.get<SwipeLayout>(convertView, R.id.swp_layout)
        val txt_del = ViewHolder.get<TextView>(convertView, R.id.txt_del)

        val item = mList[position]
        SharePreferenceManager.setItem(item.id)
        var bitmap = NativeImageLoader.getInstance().getBitmapFromMemCache(item.username)
        if (bitmap == null) {
            val path = item.avatar
            if (path == null || TextUtils.isEmpty(path)) {
                headIcon.setImageResource(R.drawable.jmui_head_icon)
            } else {
                bitmap = BitmapLoader.getBitmapFromFile(path, (50 * mDensity).toInt(), (50 * mDensity).toInt())
                NativeImageLoader.getInstance().updateBitmapFromCache(item.username, bitmap)
                headIcon.setImageBitmap(bitmap)
            }
        } else {
            headIcon.setImageBitmap(bitmap)
        }

        name.text = item.displayName
        reason.text = item.reason
        JMessageClient.getUserInfo(item.username, object : GetUserInfoCallback() {
            override fun gotResult(i: Int, s: String, userInfo: UserInfo) {
                if (i == 0) {
                    if (userInfo.isFriend) {
                        item.state = FriendInvitation.ACCEPTED.value
                        item.save()
                        val entry = FriendEntry.getFriend(ImsApplication.getUserEntry(), item.username, item.appKey)
                        if (entry == null) {
                            EventBus.getDefault().post(
                                Event.Builder().setType(EventType.addFriend)
                                    .setFriendId(item.id!!).build()
                            )
                        }
                    }
                }
            }
        })

        when {
            item.state == FriendInvitation.INVITED.value -> {
                addBtn.visibility = View.VISIBLE
                state.visibility = View.GONE
                addBtn.setOnClickListener {
                    val dialog = DialogCreator.createLoadingDialog(mContext, "正在加载")
                    ContactManager.acceptInvitation(item.username, item.appKey, object : BasicCallback() {
                        override fun gotResult(responseCode: Int, responseMessage: String) {
                            dialog.dismiss()
                            if (responseCode == 0) {
                                item.state = FriendInvitation.ACCEPTED.value
                                item.save()
                                addBtn.visibility = View.GONE
                                state.visibility = View.VISIBLE
                                state.setTextColor(mContext.resources.getColor(R.color.contacts_pinner_txt))
                                state.text = "已添加"
                                EventBus.getDefault().post(
                                    Event.Builder().setType(EventType.addFriend)
                                        .setFriendId(item.id!!).build()
                                )

                                //添加好友成功创建个会话
                                var conversation: Conversation? =
                                    JMessageClient.getSingleConversation(item.username, item.appKey)
                                if (conversation == null) {
                                    conversation = Conversation.createSingleConversation(item.username, item.appKey)
                                    EventBus.getDefault().post(
                                        Event.Builder()
                                            .setType(EventType.createConversation)
                                            .setConversation(conversation)
                                            .build()
                                    )
                                }
                            }
                        }
                    })
                }
            }
            item.state == FriendInvitation.ACCEPTED.value -> {
                addBtn.visibility = View.GONE
                state.visibility = View.VISIBLE
                state.setTextColor(mContext.resources.getColor(R.color.contacts_pinner_txt))
                state.text = mContext.getString(R.string.added)
            }
            item.state == FriendInvitation.INVITING.value -> {
                addBtn.visibility = View.GONE
                state.visibility = View.VISIBLE
                state.setTextColor(mContext.resources.getColor(R.color.finish_btn_clickable_color))
                state.text = mContext.getString(R.string.friend_inviting)
                state.setTextColor(mContext.resources.getColor(R.color.wait_inviting))
            }
            item.state == FriendInvitation.BE_REFUSED.value -> {
                addBtn.visibility = View.GONE
                reason.setTextColor(mContext.resources.getColor(R.color.contacts_pinner_txt))
                state.visibility = View.VISIBLE
                state.setTextColor(mContext.resources.getColor(R.color.contacts_pinner_txt))
                state.text = mContext.getString(R.string.decline_friend_invitation)
            }
            else -> {
                addBtn.visibility = View.GONE
                state.visibility = View.VISIBLE
                state.setTextColor(mContext.resources.getColor(R.color.contacts_pinner_txt))
                state.text = mContext.getString(R.string.refused)
            }
        }


        itemLl.setOnClickListener {
            val entry = mList[position]
            val intent: Intent
            if (entry.state == FriendInvitation.INVITED.value) {
                //1.没同意也没拒绝时--> 是否同意界面
                intent = Intent(mContext, SearchFriendDetailActivity::class.java)
                intent.putExtra("reason", item.reason)
                intent.putExtra("position", position)
                intent.putExtra(ImsApplication.TARGET_ID, entry.username)
                intent.putExtra(ImsApplication.TARGET_APP_KEY, entry.appKey)
                mContext.startActivityForResult(intent, 0)
                //2.已经添加的 --> 好友详情
            } else if (entry.state == FriendInvitation.ACCEPTED.value) {
                JMessageClient.getUserInfo(item.username, object : GetUserInfoCallback() {
                    override fun gotResult(i: Int, s: String, userInfo: UserInfo) {
                        if (i == 0) {
                            val intent1 = Intent()
                            if (userInfo.isFriend) {
                                intent1.setClass(mContext, FriendInfoActivity::class.java)
                                intent1.putExtra("fromContact", true)
                            } else {
                                intent1.setClass(mContext, GroupNotFriendActivity::class.java)
                            }
                            intent1.putExtra(ImsApplication.TARGET_ID, entry.username)
                            intent1.putExtra(ImsApplication.TARGET_APP_KEY, entry.appKey)
                            mContext.startActivityForResult(intent1, 0)
                        }
                    }
                })

                //3.自己拒绝、被对方拒绝、等待对方验证 --> 用户资料界面
            } else {
                intent = Intent(mContext, GroupNotFriendActivity::class.java)
                intent.putExtra("reason", item.reason)
                intent.putExtra(ImsApplication.TARGET_ID, entry.username)
                intent.putExtra(ImsApplication.TARGET_APP_KEY, entry.appKey)
                mContext.startActivityForResult(intent, 0)
            }
        }


        swp_layout.addSwipeListener(object : SwipeLayout.SwipeListener {
            override fun onStartOpen(layout: SwipeLayout) {

            }

            override fun onOpen(layout: SwipeLayout) {
                //侧滑删除拉出来后,点击删除,删除此条目
                txt_del.setOnClickListener {
                    val entry = mList[position]
                    FriendRecommendEntry.deleteEntry(entry)
                    mList.removeAt(position)
                    notifyDataSetChanged()
                }
                //侧滑删除拉出来后,点击整个条目的话,删除回退回去
                itemLl.setOnClickListener { swp_layout.cancelPull() }
            }

            override fun onStartClose(layout: SwipeLayout) {

            }

            override fun onClose(layout: SwipeLayout) {
                /**
                 * 这里分三种情况
                 * 1.没同意也没拒绝时--> 是否同意界面
                 * 2.已经添加的 --> 好友详情
                 * 3.自己拒绝、被对方拒绝、等待对方验证 --> 用户资料界面
                 */
                itemLl.setOnClickListener {
                    val entry = mList[position]
                    val intent: Intent
                    if (entry.state == FriendInvitation.INVITED.value) {
                        //1.没同意也没拒绝时--> 是否同意界面
                        intent = Intent(mContext, SearchFriendDetailActivity::class.java)
                        intent.putExtra("reason", item.reason)
                        intent.putExtra("position", position)
                        //2.已经添加的 --> 好友详情
                    } else if (entry.state == FriendInvitation.ACCEPTED.value) {
                        intent = Intent(mContext, FriendInfoActivity::class.java)
                        intent.putExtra("fromContact", true)
                        //3.自己拒绝、被对方拒绝、等待对方验证 --> 用户资料界面
                    } else {
                        intent = Intent(mContext, GroupNotFriendActivity::class.java)
                        intent.putExtra("reason", item.reason)
                    }
                    intent.putExtra(ImsApplication.TARGET_ID, entry.username)
                    intent.putExtra(ImsApplication.TARGET_APP_KEY, entry.appKey)
                    mContext.startActivityForResult(intent, 0)
                }
            }

            override fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int) {

            }

            override fun onHandRelease(layout: SwipeLayout, xvel: Float, yvel: Float) {

            }
        })
        return convertView
    }

}