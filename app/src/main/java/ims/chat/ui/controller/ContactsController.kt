package ims.chat.ui.controller

import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.text.TextUtils
import android.view.View
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetUserInfoListCallback
import cn.jpush.im.android.api.model.UserInfo
import com.activeandroid.ActiveAndroid
import ims.chat.R
import ims.chat.adapter.StickyListAdapter
import ims.chat.application.ImsApplication
import ims.chat.database.FriendEntry
import ims.chat.database.UserEntry
import ims.chat.ui.activity.FriendRecommendActivity
import ims.chat.ui.sidebar.SideBar
import ims.chat.ui.view.ContactsView
import ims.chat.utils.pinyin.HanziToPinyin
import ims.chat.utils.pinyin.PinyinComparator

import java.util.ArrayList
import java.util.Collections


class ContactsController(private val mContactsView: ContactsView, context: FragmentActivity) : View.OnClickListener,
    SideBar.OnTouchingLetterChangedListener {
    private val mContext: Activity
    private var mList: MutableList<FriendEntry> = ArrayList()
    private var mAdapter: StickyListAdapter? = null
    private val forDelete = ArrayList<FriendEntry>()


    init {
        this.mContext = context
    }


    override fun onClick(v: View) {
        val intent = Intent()
        when (v.id) {
            R.id.verify_ll//验证消息
            -> {
                intent.setClass(mContext, FriendRecommendActivity::class.java)
                mContext.startActivity(intent)
                mContactsView.dismissNewFriends()
            }

            R.id.search_title//查找
            -> {
            }
            //                intent.setClass(mContext, SearchContactsActivity.class);
            //                mContext.startActivity(intent);
            //                break;
            else -> {
            }
        }
    }


    fun initContacts() {
        val user = UserEntry.getUser(
            JMessageClient.getMyInfo().userName,
            JMessageClient.getMyInfo().appKey
        )
        mContactsView.showLoadingHeader()
        ContactManager.getFriendList(object : GetUserInfoListCallback() {
            override fun gotResult(responseCode: Int, responseMessage: String, userInfoList: List<UserInfo>) {
                mContactsView.dismissLoadingHeader()
                if (responseCode == 0) {
                    if (userInfoList.size != 0) {
                        mContactsView.dismissLine()
                        ActiveAndroid.beginTransaction()
                        try {
                            for (userInfo in userInfoList) {
                                val displayName = userInfo.displayName
                                val letter: String
                                if (!TextUtils.isEmpty(displayName.trim { it <= ' ' })) {
                                    val tokens = HanziToPinyin.getInstance()
                                        .get(displayName)
                                    val sb = StringBuilder()
                                    if (tokens !=
                                        null && tokens.size > 0
                                    ) {
                                        for (token in tokens) {
                                            if (token.type == HanziToPinyin.Token.PINYIN) {
                                                sb.append(token.target)
                                            } else {
                                                sb.append(token.source)
                                            }
                                        }
                                    }
                                    val sortString = sb.toString().substring(0, 1).toUpperCase()
                                    if (sortString.matches("[A-Z]".toRegex())) {
                                        letter = sortString.toUpperCase()
                                    } else {
                                        letter = "#"
                                    }
                                } else {
                                    letter = "#"
                                }
                                //避免重复请求时导致数据重复A
                                var friend: FriendEntry? = FriendEntry.getFriend(
                                    user,
                                    userInfo.userName, userInfo.appKey
                                )
                                if (null == friend) {
                                    if (TextUtils.isEmpty(userInfo.avatar)) {
                                        friend = FriendEntry(
                                            userInfo.userID,
                                            userInfo.userName,
                                            userInfo.notename,
                                            userInfo.nickname,
                                            userInfo.appKey,
                                            null,
                                            displayName,
                                            letter,
                                            user
                                        )
                                    } else {
                                        friend = FriendEntry(
                                            userInfo.userID,
                                            userInfo.userName,
                                            userInfo.notename,
                                            userInfo.nickname,
                                            userInfo.appKey,
                                            userInfo.avatarFile.absolutePath,
                                            displayName,
                                            letter,
                                            user
                                        )
                                    }
                                    friend.save()
                                    mList.add(friend)
                                }
                                forDelete.add(friend)
                            }
                            ActiveAndroid.setTransactionSuccessful()
                        } finally {
                            ActiveAndroid.endTransaction()
                        }
                    } else {
                        mContactsView.showLine()
                    }
                    //其他端删除好友后,登陆时把数据库中的也删掉
                    val friends = ImsApplication.getUserEntry().friends
                    friends.removeAll(forDelete)
                    for (del in friends) {
                        del.delete()
                        mList.remove(del)
                    }
                    Collections.sort(mList, PinyinComparator())
                    mAdapter = StickyListAdapter(mContext, mList, false)
                    mContactsView.setAdapter(mAdapter!!)
                }
            }
        })

    }

    override fun onTouchingLetterChanged(s: String) {
        //该字母首次出现的位置
        if (null != mAdapter) {
            val position = mAdapter!!.getSectionForLetter(s)
            if (position != -1 && position < mAdapter!!.count) {
                mContactsView.setSelection(position)
            }
        }
    }

    fun refresh(entry: FriendEntry) {
        mList.add(entry)
        if (null == mAdapter) {
            mAdapter = StickyListAdapter(mContext, mList, false)
        } else {
            Collections.sort(mList, PinyinComparator())
        }
        mAdapter!!.notifyDataSetChanged()
    }

    fun refreshContact() {
        val user = UserEntry.getUser(
            JMessageClient.getMyInfo().userName,
            JMessageClient.getMyInfo().appKey
        )
        mList = user.friends
        Collections.sort(mList, PinyinComparator())
        mAdapter = StickyListAdapter(mContext, mList, false)
        mContactsView.setAdapter(mAdapter!!)
    }

}
