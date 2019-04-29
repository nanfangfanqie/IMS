package ims.chat.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import ims.chat.R;
import ims.chat.application.ImsApplication;
import ims.chat.ui.controller.ChatDetailController;
import ims.chat.ui.view.ChatDetailView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ChatDetailActivity extends BaseActivity {

    private static final String TAG = "ChatDetailActivity";

    private ChatDetailView mChatDetailView;
    private ChatDetailController mChatDetailController;
    private UIHandler mUIHandler = new UIHandler(this);
    public final static String START_FOR_WHICH = "which";
    private final static int GROUP_NAME_REQUEST_CODE = 1;
    private final static int MY_NAME_REQUEST_CODE = 2;
    private static final int ADD_FRIEND_REQUEST_CODE = 3;

    public static final int GROUP_DESC = 70;
    public static final int FLAGS_GROUP_DESC = 71;
    public static final String GROUP_DESC_KEY = "group_desc_key";

    public static final int GROUP_NAME = 72;
    public static final int FLAGS_GROUP_NAME = 73;
    public static final String GROUP_NAME_KEY = "group_name_key";

    private long groupID;


    private Context mContext;
    private ProgressDialog mDialog;
    private String mGroupName;
    private String mGroupDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        mContext = this;
        mChatDetailView = (ChatDetailView) findViewById(R.id.chat_detail_view);
        mChatDetailView.initModule();
        mChatDetailController = new ChatDetailController(mChatDetailView, this, mAvatarSize, mWidth);
        mChatDetailView.setListeners(mChatDetailController);
        mChatDetailView.setOnChangeListener(mChatDetailController);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ImsApplication.CONV_TITLE, mChatDetailController.getName());
        intent.putExtra(ImsApplication.MEMBERS_COUNT, mChatDetailController.getCurrentCount());
        intent.putExtra("deleteMsg", mChatDetailController.getDeleteFlag());
        setResult(ImsApplication.RESULT_CODE_CHAT_DETAIL, intent);
        finish();
        super.onBackPressed();
    }

    private void dismissSoftInput() {
        //隐藏软键盘
        InputMethodManager imm = ((InputMethodManager) mContext
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (this.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @SuppressLint("WrongConstant")
    public void updateGroupNameDesc(long groupId, int nameOrDesc) {
        this.groupID = groupId;
        Intent intent = new Intent(ChatDetailActivity.this, NickSignActivity.class);
        if (nameOrDesc == 1) {
            intent.setFlags(FLAGS_GROUP_NAME);
            intent.putExtra("group_name", mGroupName);
        } else {
            intent.setFlags(FLAGS_GROUP_DESC);
            intent.putExtra("group_desc", mGroupDesc);
        }
        startActivityForResult(intent, GROUP_NAME);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Bundle extras = data.getExtras();
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("正在修改");
            switch (resultCode) {
                case Activity.RESULT_CANCELED:

                    break;

            }

            switch (requestCode) {
                case MY_NAME_REQUEST_CODE:
                    if (data.getBooleanExtra("returnChatActivity", false)) {
                        data.putExtra("deleteMsg", mChatDetailController.getDeleteFlag());
                        data.putExtra(ImsApplication.NAME, mChatDetailController.getName());
                        setResult(ImsApplication.RESULT_CODE_CHAT_DETAIL, data);
                        finish();
                    }
                    break;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChatDetailController.initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    public void startMainActivity() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
    }

    public void startChatActivity(long groupID, String groupName, int member) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //设置跳转标志
        intent.putExtra("fromGroup", true);
        intent.putExtra(ImsApplication.MEMBERS_COUNT, member + 1);
        intent.putExtra(ImsApplication.GROUP_ID, groupID);
        intent.putExtra(ImsApplication.CONV_TITLE, groupName);
        intent.setClass(this, ChatActivity.class);
        startActivity(intent);
        finish();
    }

    private static class UIHandler extends Handler {

        private WeakReference<ChatDetailActivity> mActivity;

        public UIHandler(ChatDetailActivity activity) {
            mActivity = new WeakReference<ChatDetailActivity>(activity);
        }

    }


}
