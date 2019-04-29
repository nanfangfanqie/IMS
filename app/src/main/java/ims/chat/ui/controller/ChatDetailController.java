package ims.chat.ui.controller;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.eventbus.EventBus;
import cn.jpush.im.api.BasicCallback;
import ims.chat.R;
import ims.chat.application.ImsApplication;
import ims.chat.database.FriendEntry;
import ims.chat.database.FriendRecommendEntry;
import ims.chat.entity.Event;
import ims.chat.entity.EventType;
import ims.chat.ui.activity.*;
import ims.chat.ui.view.ChatDetailView;
import ims.chat.ui.view.SlipButton;
import ims.chat.utils.DialogCreator;
import ims.chat.utils.ToastUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class ChatDetailController implements OnClickListener, OnItemClickListener,
        SlipButton.OnChangedListener {


    private ChatDetailView mChatDetailView;
    private ChatDetailActivity mContext;
    //GridView的数据源
    private List<UserInfo> mMemberInfoList = new ArrayList<UserInfo>();
    private int mCurrentNum;
    // 空白项的项数
    // 除了群成员Item和添加、删除按钮，剩下的都看成是空白项，
    // 对应的mRestNum[mCurrent%4]的值即为空白项的数目
    private int[] mRestArray = new int[]{2, 1, 0, 3};
    private boolean mIsGroup = false;
    private boolean mIsCreator = false;
    private long mGroupId;
    private String mTargetId;
    private Dialog mLoadingDialog = null;
    private static final int ADD_MEMBERS_TO_GRIDVIEW = 2048;
    private static final int ADD_A_MEMBER_TO_GRIDVIEW = 2049;
    private static final int MAX_GRID_ITEM = 40;
    private String mGroupName;
    private final MyHandler myHandler = new MyHandler(this);
    private Dialog mDialog;
    private boolean mDeleteMsg;
    private int mAvatarSize;
    private String mMyUsername;
    private String mTargetAppKey;
    private int mWidth;
    private GroupInfo mGroupInfo;
    private UserInfo mUserInfo;
    private boolean mShowMore;
    private String mMyNickName;
    private String mNickName;
    private String mAvatarPath;
    private boolean mFriend;
    private Long mUid;

    public ChatDetailController(ChatDetailView chatDetailView, ChatDetailActivity context, int size,
                                int width) {
        this.mChatDetailView = chatDetailView;
        this.mContext = context;
        this.mAvatarSize = size;
        this.mWidth = width;
        initData();
    }

    public void initData() {
        Intent intent = mContext.getIntent();
        mGroupId = intent.getLongExtra(ImsApplication.GROUP_ID, 0);
        mTargetId = intent.getStringExtra(ImsApplication.TARGET_ID);
        mTargetAppKey = intent.getStringExtra(ImsApplication.TARGET_APP_KEY);
        UserInfo myInfo = JMessageClient.getMyInfo();
        mMyNickName = myInfo.getNickname();
        mMyUsername = myInfo.getUserName();
        mChatDetailView.setTitle("聊天设置");
        Conversation conv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
        mUserInfo = (UserInfo) conv.getTargetInfo();
        mChatDetailView.initNoDisturb(mUserInfo.getNoDisturb());
        mCurrentNum = 1;
        mChatDetailView.setSingleView(mUserInfo.isFriend());
        JMessageClient.getUserInfo(mTargetId, new GetUserInfoCallback() {

            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                if (i == 0) {
                    mFriend = userInfo.isFriend();
                    mNickName = userInfo.getNickname();
                    mUid = userInfo.getUserID();
                    if (TextUtils.isEmpty(mNickName)) {
                        mNickName = mTargetId;
                    }
                    File avatarFile = userInfo.getAvatarFile();
                    if (avatarFile != null) {
                        mAvatarPath = avatarFile.getAbsolutePath();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.return_btn:
                intent.putExtra("deleteMsg", mDeleteMsg);
                intent.putExtra(ImsApplication.CONV_TITLE, getName());
                intent.putExtra(ImsApplication.MEMBERS_COUNT, mMemberInfoList.size());
                mContext.setResult(ImsApplication.RESULT_CODE_CHAT_DETAIL, intent);
                mContext.finish();
                break;
            case R.id.group_chat_del_ll:
                OnClickListener listener = new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.jmui_cancel_btn:
                                mDialog.cancel();
                                break;
                            case R.id.jmui_commit_btn:
                                Conversation conv;
                                if (mIsGroup) {
                                    conv = JMessageClient.getGroupConversation(mGroupId);
                                } else {
                                    conv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
                                }
                                if (conv != null) {
                                    conv.deleteAllMessage();
                                    mDeleteMsg = true;
                                }
                                ToastUtil.shortToast(mContext, "清空成功");
                                mDialog.cancel();
                                break;
                        }
                    }
                };
                mDialog = DialogCreator.createDeleteMessageDialog(mContext, listener);
                mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                mDialog.show();
                break;
            //删除好友
            case R.id.del_friend:
                listener = new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.jmui_cancel_btn:
                                mDialog.cancel();
                                break;
                            case R.id.jmui_commit_btn:
                                deleteFriend();
                                mDialog.cancel();
                                break;
                        }
                    }
                };
                if (mIsGroup) {
                    mDialog = DialogCreator.createExitGroupDialog(mContext, listener);
                } else {
                    mDialog = DialogCreator.createBaseDialogWithTitle(mContext,
                            mContext.getString(R.string.delete_friend_dialog_title), listener);
                }
                mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                mDialog.show();
                break;
            case R.id.clear_rl:
                final Dialog clear = new Dialog(mContext, R.style.jmui_default_dialog_style);
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View view = inflater.inflate(R.layout.dialog_clear, null);
                clear.setContentView(view);
                Window window = clear.getWindow();
                window.setGravity(Gravity.BOTTOM);
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                clear.show();
                clear.setCanceledOnTouchOutside(true);
                Button delete = (Button) view.findViewById(R.id.btn_del);
                Button cancel = (Button) view.findViewById(R.id.btn_cancel);
                OnClickListener listen = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.btn_del:
                                if (mIsGroup) {
                                    Conversation singleConversation = JMessageClient.getGroupConversation(mGroupId);
                                    List<cn.jpush.im.android.api.model.Message> allMessage = singleConversation.getAllMessage();
                                    for (cn.jpush.im.android.api.model.Message msg : allMessage) {
                                        MessageContent content = msg.getContent();
                                        if (content.getContentType() == ContentType.image) {
                                            ImageContent imageContent = (ImageContent) content;
                                            String localPath = imageContent.getLocalPath();
                                            if (!TextUtils.isEmpty(localPath)) {
                                                File imageFile = new File(localPath);
                                                if (imageFile.exists()) {
                                                    imageFile.delete();
                                                }
                                            }
                                        } else if (content.getContentType() == ContentType.file) {
                                            FileContent fileContent = (FileContent) content;
                                            String localPath = fileContent.getLocalPath();
                                            if (!TextUtils.isEmpty(localPath)) {
                                                File file = new File(localPath);
                                                if (file.exists()) {
                                                    file.delete();
                                                }
                                            }
                                        }
                                    }
                                    ToastUtil.shortToast(mContext, "清理成功");
                                } else {
                                    Conversation singleConversation = JMessageClient.getSingleConversation(mTargetId);
                                    List<cn.jpush.im.android.api.model.Message> allMessage = singleConversation.getAllMessage();
                                    for (cn.jpush.im.android.api.model.Message msg : allMessage) {
                                        MessageContent content = msg.getContent();
                                        if (content.getContentType() == ContentType.image) {
                                            ImageContent imageContent = (ImageContent) content;
                                            String localPath = imageContent.getLocalPath();
                                            if (!TextUtils.isEmpty(localPath)) {
                                                File imageFile = new File(localPath);
                                                if (imageFile.exists()) {
                                                    imageFile.delete();
                                                }
                                            }
                                        } else if (content.getContentType() == ContentType.file) {
                                            FileContent fileContent = (FileContent) content;
                                            String localPath = fileContent.getLocalPath();
                                            if (!TextUtils.isEmpty(localPath)) {
                                                File file = new File(localPath);
                                                if (file.exists()) {
                                                    boolean delete1 = file.delete();
                                                    File copyFile = new File(ImsApplication.FILE_DIR + fileContent.getFileName());
                                                    boolean delete2 = copyFile.delete();
                                                }
                                            }
                                        }
                                    }
                                    ToastUtil.shortToast(mContext, "清理成功");
                                }
                                clear.dismiss();
                                break;
                            case R.id.btn_cancel:
                                clear.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                };
                delete.setOnClickListener(listen);
                cancel.setOnClickListener(listen);
                break;
        }
    }

    /**
     * 删除好友
     */
    private void deleteFriend() {
        mLoadingDialog = DialogCreator.createLoadingDialog(mContext,
                mContext.getString(R.string.exiting_group_toast));
        mLoadingDialog.show();
        mUserInfo.removeFromFriendList(new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                mLoadingDialog.dismiss();
                if (responseCode == 0) {
                    //删除好友时候要从list中移除.准备接收下一次添加好友申请
                    ImsApplication.forAddFriend.remove(mUserInfo.getUserName());
                    //将好友删除时候还原黑名单设置
                    List<String> name = new ArrayList<>();
                    name.add(mUserInfo.getUserName());
                    JMessageClient.delUsersFromBlacklist(name, null);

                    FriendEntry friend = FriendEntry.getFriend(ImsApplication.getUserEntry(),
                            mUserInfo.getUserName(), mUserInfo.getAppKey());
                    if (friend != null) {
                        friend.delete();
                    }
                    FriendRecommendEntry entry = FriendRecommendEntry
                            .getEntry(ImsApplication.getUserEntry(),
                                    mUserInfo.getUserName(), mUserInfo.getAppKey());
                    if (entry != null) {
                        entry.delete();
                    }
                    ToastUtil.shortToast(mContext, "移除好友");
                    delConvAndReturnMainActivity();
                } else {
                    ToastUtil.shortToast(mContext, "移除失败");
                }
            }
        });

    }

    public void delConvAndReturnMainActivity() {
        Conversation conversation = JMessageClient.getSingleConversation(mUserInfo.getUserName(), mUserInfo.getAppKey());
        EventBus.getDefault().post(new Event.Builder().setType(EventType.deleteConversation)
                .setConversation(conversation)
                .build());
        JMessageClient.deleteSingleConversation(mUserInfo.getUserName(), mUserInfo.getAppKey());
        Intent intent = new Intent(mContext, MainActivity.class);
        mContext.startActivity(intent);
    }

    // GridView点击事件
    @Override
    public void onItemClick(AdapterView<?> viewAdapter, View view, final int position, long id) {
        Intent intent = new Intent();
        if (position < mCurrentNum) {
            //会话中点击右上角进入拉人进群界面,点击add按钮之前的user头像.
            if (mFriend) {
                intent.setClass(mContext, FriendInfoActivity.class);
            } else {
                intent.setClass(mContext, GroupNotFriendActivity.class);
            }
            intent.putExtra(ImsApplication.TARGET_ID, mTargetId);
            intent.putExtra(ImsApplication.TARGET_APP_KEY, mTargetAppKey);
            mContext.startActivityForResult(intent, ImsApplication.REQUEST_CODE_FRIEND_INFO);
        } else if (position == mCurrentNum) {
        }

    }

    /**
     * 添加成员时检查是否存在该群成员
     *
     * @param targetID 要添加的用户
     * @return 返回是否存在该用户
     */
    private boolean checkIfNotContainUser(String targetID) {
        if (mMemberInfoList != null) {
            for (UserInfo userInfo : mMemberInfoList) {
                if (userInfo.getUserName().equals(targetID))
                    return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public void onChanged(int id, final boolean checked) {
        switch (id) {
            case R.id.no_disturb_slip_btn:
                final Dialog dialog = DialogCreator.createLoadingDialog(mContext, mContext.getString(R.string.processing));
                dialog.show();
                //设置免打扰,1为将当前用户或群聊设为免打扰,0为移除免打扰
                if (mIsGroup) {
                    mGroupInfo.setNoDisturb(checked ? 1 : 0, new BasicCallback() {
                        @Override
                        public void gotResult(int status, String desc) {
                            dialog.dismiss();
                            if (status == 0) {
                                if (checked) {
                                    Toast.makeText(mContext, mContext.getString(R.string.set_do_not_disturb_success_hint),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.remove_from_no_disturb_list_hint),
                                            Toast.LENGTH_SHORT).show();
                                }
                                //设置失败,恢复为原来的状态
                            } else {
                                if (checked) {
                                    mChatDetailView.setNoDisturbChecked(false);
                                } else {
                                    mChatDetailView.setNoDisturbChecked(true);
                                }
                            }
                        }
                    });
                } else {
                    mUserInfo.setNoDisturb(checked ? 1 : 0, new BasicCallback() {
                        @Override
                        public void gotResult(int status, String desc) {
                            dialog.dismiss();
                            if (status == 0) {
                                if (checked) {
                                    Toast.makeText(mContext, mContext.getString(R.string.set_do_not_disturb_success_hint),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.remove_from_no_disturb_list_hint),
                                            Toast.LENGTH_SHORT).show();
                                }
                                //设置失败,恢复为原来的状态
                            } else {
                                if (checked) {
                                    mChatDetailView.setNoDisturbChecked(false);
                                } else {
                                    mChatDetailView.setNoDisturbChecked(true);
                                }
                            }
                        }
                    });
                }
                break;
            case R.id.block_slip_btn:
                mDialog = DialogCreator.createLoadingDialog(mContext, mContext.getString(R.string.processing));
                mDialog.show();
                mGroupInfo.setBlockGroupMessage(checked ? 1 : 0, new BasicCallback() {
                    @Override
                    public void gotResult(int status, String desc) {
                        mDialog.dismiss();
                        if (status == 0) {
                            if (checked) {
                                Toast.makeText(mContext, mContext.getString(R.string
                                        .set_block_succeed_hint), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string
                                        .remove_block_succeed_hint), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                        }
                    }
                });
                break;
        }
    }

    public void getNoDisturb() {
        if (mUserInfo != null) {
            ChatDetailView.Companion.getMNoDisturbBtn().setChecked(mUserInfo.getNoDisturb() == 1);
        }

    }


    private static class MyHandler extends Handler {
        private final WeakReference<ChatDetailController> mController;

        public MyHandler(ChatDetailController controller) {
            mController = new WeakReference<ChatDetailController>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatDetailController controller = mController.get();
            if (controller != null) {
                switch (msg.what) {
                    //无好友模式一次添加一个
                    case ADD_A_MEMBER_TO_GRIDVIEW:
                        if (controller.mLoadingDialog != null) {
                            controller.mLoadingDialog.dismiss();
                        }
                        final UserInfo userInfo = (UserInfo) msg.obj;
                        break;
                    //好友模式从通讯录中添加好友
                    case ADD_MEMBERS_TO_GRIDVIEW:
                        break;
                }
            }
        }
    }

    public String getName() {
            Conversation conv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
            return conv.getTitle();
    }

    public int getCurrentCount() {
        return mMemberInfoList.size();
    }

    public boolean getDeleteFlag() {
        return mDeleteMsg;
    }


}
