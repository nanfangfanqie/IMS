package ims.chat.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Toast;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.*;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent;
import cn.jpush.im.android.api.event.MessageRetractEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.android.eventbus.EventBus;
import cn.jpush.im.api.BasicCallback;
import ims.chat.R;
import ims.chat.adapter.ChattingListAdapter;
import ims.chat.application.ImsApplication;
import ims.chat.emoji.EmojiBean;
import ims.chat.entity.Event;
import ims.chat.entity.EventType;
import ims.chat.model.Constants;
import ims.chat.ui.view.ChatView;
import ims.chat.ui.view.SimpleAppsGridView;
import ims.chat.utils.*;
import ims.chat.utils.event.ImageEvent;
import ims.chat.utils.imagepicker.bean.ImageItem;
import ims.chat.utils.keyboard.XhsEmoticonsKeyBoard;
import ims.chat.utils.keyboard.data.EmoticonEntity;
import ims.chat.utils.keyboard.interfaces.EmoticonClickListener;
import ims.chat.utils.keyboard.utils.EmoticonsKeyboardUtils;
import ims.chat.utils.keyboard.widget.EmoticonsEditText;
import ims.chat.utils.keyboard.widget.FuncLayout;
import ims.chat.utils.listview.DropDownListView;
import ims.chat.utils.photovideo.takevideo.CameraActivity;
import ims.chat.utils.pickerimage.PickImageActivity;
import ims.chat.utils.pickerimage.utils.Extras;
import ims.chat.utils.pickerimage.utils.RequestCode;
import ims.chat.utils.pickerimage.utils.SendImageHelper;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity implements FuncLayout.OnFuncKeyBoardListener, View.OnClickListener {
    public static final String JPG = ".jpg";
    private static String MsgIDs = "msgIDs";

    private String mTitle;
    private boolean mLongClick = false;

    private static final String MEMBERS_COUNT = "membersCount";
    private static final String GROUP_NAME = "groupName";

    public static final String TARGET_ID = "targetId";
    public static final String TARGET_APP_KEY = "targetAppKey";
    private static final String DRAFT = "draft";
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private ChatView mChatView;
    private boolean mIsSingle = true;
    private Conversation mConv;
    private String mTargetId;
    private String mTargetAppKey;
    private Activity mContext;
    private ChattingListAdapter mChatAdapter;
    private List<UserInfo> mAtList;
    private long mGroupId;
    private static final int REFRESH_LAST_PAGE = 0x1023;
    private static final int REFRESH_CHAT_TITLE = 0x1024;
    private static final int REFRESH_GROUP_NAME = 0x1025;

    private UserInfo mMyInfo;
    private static final String GROUP_ID = "groupId";
    private int mAtMsgId;
    private int mUnreadMsgCnt;
    private boolean mShowSoftInput = false;
    private List<UserInfo> forDel = new ArrayList<>();
    DropDownListView lvChat;
    XhsEmoticonsKeyBoard ekBar;

    Window mWindow;
    InputMethodManager mImm;
    private final UIHandler mUIHandler = new UIHandler(this);
    private boolean mAtAll = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_chat);
        mChatView = (ChatView) findViewById(R.id.chat_view);
        mChatView.initModule(mDensity, mDensityDpi);
        this.mWindow = getWindow();
        this.mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mChatView.setListeners(this);
        initData();
        initView();

    }

    private void initData() {
        Intent intent = getIntent();
        ekBar = (XhsEmoticonsKeyBoard) findViewById(R.id.ek_bar);
        lvChat = (DropDownListView) findViewById(R.id.lv_chat);
        SimpleCommonUtils.initEmoticonsEditText(ekBar.getEtChat());
        mTargetId = intent.getStringExtra(TARGET_ID);
        mTargetAppKey = intent.getStringExtra(TARGET_APP_KEY);
        mTitle = intent.getStringExtra(ImsApplication.CONV_TITLE);
        mMyInfo = JMessageClient.getMyInfo();
        //单聊
        mIsSingle = true;
        mChatView.setChatTitle(mTitle);
        mConv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
        if (mConv == null) {
            mConv = Conversation.createSingleConversation(mTargetId, mTargetAppKey);
        }
        mChatAdapter = new ChattingListAdapter(mContext, mConv, longClickListener);
        String draft = intent.getStringExtra(DRAFT);
        if (draft != null && !"null".equals(draft)) {
            ekBar.getEtChat().setText(draft);
        }
        mChatView.setChatListAdapter(mChatAdapter);
        mChatView.getListView().setOnDropDownListener(new DropDownListView.OnDropDownListener() {
            @Override
            public void onDropDown() {
                mUIHandler.sendEmptyMessageDelayed(REFRESH_LAST_PAGE, 1000);
            }
        });
        mChatView.setToBottom();
        mChatView.setConversation(mConv);
    }

    private void initView() {
        initEmoticonsKeyBoardBar();
        initListView();
    }

    private void initEmoticonsKeyBoardBar() {
        ekBar.setAdapter(SimpleCommonUtils.getCommonAdapter(this, emoticonClickListener));
        ekBar.addOnFuncKeyBoardListener(this);
        SimpleAppsGridView gridView = new SimpleAppsGridView(this);
        ekBar.addFuncView(gridView);

        ekBar.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                scrollToBottom();
            }
        });
        //发送按钮
        ekBar.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mcgContent = ekBar.getEtChat().getText().toString();
                scrollToBottom();
                if (mcgContent.equals("")) {
                    return;
                }
                Message msg;
                TextContent content = new TextContent(mcgContent);
                if (mAtAll) {
                    msg = mConv.createSendMessageAtAllMember(content, null);
                    mAtAll = false;
                } else if (null != mAtList) {
                    msg = mConv.createSendMessage(content, mAtList, null);
                } else {
                    msg = mConv.createSendMessage(content);
                }
                //设置需要已读回执
                MessageSendingOptions options = new MessageSendingOptions();
                options.setNeedReadReceipt(true);
                JMessageClient.sendMessage(msg, options);
                mChatAdapter.addMsgFromReceiptToList(msg);
                ekBar.getEtChat().setText("");
                if (mAtList != null) {
                    mAtList.clear();
                }
                if (forDel != null) {
                    forDel.clear();
                }
            }
        });
        //切换语音输入
        ekBar.getVoiceOrText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.btn_voice_or_text) {
                    ekBar.setVideoText();
                    ekBar.getBtnVoice().initConv(mConv, mChatAdapter, mChatView);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jmui_return_btn:
                returnBtn();
                break;
            case R.id.jmui_right_btn:
                startChatDetailActivity(mTargetId, mTargetAppKey, mGroupId);
                break;
            case R.id.jmui_at_me_btn:
                if (mUnreadMsgCnt < ChattingListAdapter.PAGE_MESSAGE_COUNT) {
                    int position = ChattingListAdapter.PAGE_MESSAGE_COUNT + mAtMsgId - mConv.getLatestMessage().getId();
                    mChatView.setToPosition(position);
                } else {
                    mChatView.setToPosition(mAtMsgId + mUnreadMsgCnt - mConv.getLatestMessage().getId());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        returnBtn();
    }

    private void returnBtn() {
        mConv.resetUnreadCount();
        dismissSoftInput();
        JMessageClient.exitConversation();
        EventBus.getDefault().post(new Event.Builder().setType(EventType.draft)
                .setConversation(mConv)
                .setDraft(ekBar.getEtChat().getText().toString())
                .build());
        finish();
    }

    private void dismissSoftInput() {
        if (mShowSoftInput) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(ekBar.getEtChat().getWindowToken(), 0);
                mShowSoftInput = false;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startChatDetailActivity(String targetId, String appKey, long groupId) {
        Intent intent = new Intent();
        intent.putExtra(TARGET_ID, targetId);
        intent.putExtra(TARGET_APP_KEY, appKey);
        intent.putExtra(GROUP_ID, groupId);
        intent.setClass(this, ChatDetailActivity.class);
        startActivityForResult(intent, ImsApplication.REQUEST_CODE_CHAT_DETAIL);
    }

    EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {
            if (isDelBtn) {
                SimpleCommonUtils.delClick(ekBar.getEtChat());
            } else {
                if (o == null) {
                    return;
                }
                if (actionType == Constants.EMOTICON_CLICK_BIGIMAGE) {
                    if (o instanceof EmoticonEntity) {
                        OnSendImage(((EmoticonEntity) o).getIconUri());
                    }
                } else {
                    String content = null;
                    if (o instanceof EmojiBean) {
                        content = ((EmojiBean) o).emoji;
                    } else if (o instanceof EmoticonEntity) {
                        content = ((EmoticonEntity) o).getContent();
                    }
                    if (TextUtils.isEmpty(content)) {
                        return;
                    }
                    int index = ekBar.getEtChat().getSelectionStart();
                    Editable editable = ekBar.getEtChat().getText();
                    editable.insert(index, content);
                }
            }
        }
    };


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (EmoticonsKeyboardUtils.isFullScreen(this)) {
            boolean isConsum = ekBar.dispatchKeyEventInFullScreen(event);
            return isConsum ? isConsum : super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    private void initListView() {
        lvChat.setAdapter(mChatAdapter);
        lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        break;
                    case SCROLL_STATE_IDLE:
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        ekBar.reset();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }


    private void scrollToBottom() {
        lvChat.requestLayout();
        lvChat.post(new Runnable() {
            @Override
            public void run() {
                lvChat.setSelection(lvChat.getBottom());
            }
        });
    }

    @Override
    public void OnFuncPop(int height) {
        scrollToBottom();
    }

    @Override
    public void OnFuncClose() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        JMessageClient.exitConversation();
        ekBar.reset();
    }

    @Override
    protected void onResume() {
        String targetId = getIntent().getStringExtra(TARGET_ID);
        if (!mIsSingle) {
            long groupId = getIntent().getLongExtra(GROUP_ID, 0);
            if (groupId != 0) {
                ImsApplication.isAtMe.put(groupId, false);
                ImsApplication.isAtall.put(groupId, false);
                JMessageClient.enterGroupConversation(groupId);
            }
        } else if (null != targetId) {
            String appKey = getIntent().getStringExtra(TARGET_APP_KEY);
            JMessageClient.enterSingleConversation(targetId, appKey);
        }

        //历史消息中删除后返回到聊天界面刷新界面
        if (ImsApplication.ids != null && ImsApplication.ids.size() > 0) {
            for (Message msg : ImsApplication.ids) {
                mChatAdapter.removeMessage(msg);
            }
        }
        mChatAdapter.notifyDataSetChanged();
        //发送名片返回聊天界面刷新信息
        if (SharePreferenceManager.getIsOpen()) {
            initData();
            SharePreferenceManager.setIsOpen(false);
        }
        super.onResume();

    }

    public void onEvent(MessageEvent event) {
        final Message message = event.getMessage();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UserInfo userInfo = (UserInfo) message.getTargetInfo();
                String targetId = userInfo.getUserName();
                String appKey = userInfo.getAppKey();
                if (mIsSingle && targetId.equals(mTargetId) && appKey.equals(mTargetAppKey)) {
                    Message lastMsg = mChatAdapter.getLastMsg();
                    if (lastMsg == null || message.getId() != lastMsg.getId()) {
                        mChatAdapter.addMsgToList(message);
                    } else {
                        mChatAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }

    public void onEventMainThread(MessageRetractEvent event) {
        Message retractedMessage = event.getRetractedMessage();
        mChatAdapter.delMsgRetract(retractedMessage);
    }

    /**
     * 当在聊天界面断网再次连接时收离线事件刷新
     */
    public void onEvent(OfflineMessageEvent event) {
        Conversation conv = event.getConversation();
        if (conv.getType().equals(ConversationType.single)) {
            UserInfo userInfo = (UserInfo) conv.getTargetInfo();
            String targetId = userInfo.getUserName();
            String appKey = userInfo.getAppKey();
            if (mIsSingle && targetId.equals(mTargetId) && appKey.equals(mTargetAppKey)) {
                List<Message> singleOfflineMsgList = event.getOfflineMessageList();
                if (singleOfflineMsgList != null && singleOfflineMsgList.size() > 0) {
                    mChatView.setToBottom();
                    mChatAdapter.addMsgListToList(singleOfflineMsgList);
                }
            }
        } else {
            long groupId = ((GroupInfo) conv.getTargetInfo()).getGroupID();
            if (groupId == mGroupId) {
                List<Message> offlineMessageList = event.getOfflineMessageList();
                if (offlineMessageList != null && offlineMessageList.size() > 0) {
                    mChatView.setToBottom();
                    mChatAdapter.addMsgListToList(offlineMessageList);
                }
            }
        }
    }


    private ChattingListAdapter.ContentLongClickListener longClickListener = new ChattingListAdapter.ContentLongClickListener() {

        @Override
        public void onContentLongClick(final int position, View view) {
            final Message msg = mChatAdapter.getMessage(position);
            if (msg == null) {
                return;
            }
            //如果是文本消息
            if ((msg.getContentType() == ContentType.text) && ((TextContent) msg.getContent()).getStringExtra("businessCard") == null) {
                //接收方
                if (msg.getDirect() == MessageDirect.receive) {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mChatView, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("复制"))
                            .addItem(new TipItem("转发"))
                            .addItem(new TipItem("删除"))
                            .setOnItemClickListener(new TipView.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str, final int position) {
                                    if (position == 0) {
                                        if (msg.getContentType() == ContentType.text) {
                                            final String content = ((TextContent) msg.getContent()).getText();
                                            if (Build.VERSION.SDK_INT > 11) {
                                                ClipboardManager clipboard = (ClipboardManager) mContext
                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clip = ClipData.newPlainText("Simple text", content);
                                                clipboard.setPrimaryClip(clip);
                                            } else {
                                                android.text.ClipboardManager clip = (android.text.ClipboardManager) mContext
                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                                if (clip.hasText()) {
                                                    clip.getText();
                                                }
                                            }
                                            Toast.makeText(ChatActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChatActivity.this, "只支持复制文字", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (position == 1) {
                                    } else {
                                        //删除
                                        mConv.deleteMessage(msg.getId());
                                        mChatAdapter.removeMessage(msg);
                                    }
                                }

                                @Override
                                public void dismiss() {

                                }
                            })
                            .create();
                    //发送方
                } else {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mChatView, (int) OldListX + view.getWidth(), (int) OldListY + view.getHeight())
                            .addItem(new TipItem("复制"))
                            .addItem(new TipItem("撤回"))
                            .addItem(new TipItem("删除"))
                            .setOnItemClickListener(new TipView.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str, final int position) {
                                    switch (position) {
                                        case 0:
                                            if (msg.getContentType() == ContentType.text) {
                                                final String content = ((TextContent) msg.getContent()).getText();
                                                if (Build.VERSION.SDK_INT > 11) {
                                                    ClipboardManager clipboard = (ClipboardManager) mContext
                                                            .getSystemService(Context.CLIPBOARD_SERVICE);
                                                    ClipData clip = ClipData.newPlainText("Simple text", content);
                                                    clipboard.setPrimaryClip(clip);
                                                } else {
                                                    android.text.ClipboardManager clip = (android.text.ClipboardManager) mContext
                                                            .getSystemService(Context.CLIPBOARD_SERVICE);
                                                    if (clip.hasText()) {
                                                        clip.getText();
                                                    }
                                                }
                                                Toast.makeText(ChatActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ChatActivity.this, "只支持复制文字", Toast.LENGTH_SHORT).show();
                                            }
                                            break;
                                        case 1:
                                            //撤回
                                            mConv.retractMessage(msg, new BasicCallback() {
                                                @Override
                                                public void gotResult(int i, String s) {
                                                    if (i == 855001) {
                                                        Toast.makeText(ChatActivity.this, "发送时间过长，不能撤回", Toast.LENGTH_SHORT).show();
                                                    } else if (i == 0) {
                                                        mChatAdapter.delMsgRetract(msg);
                                                    }
                                                }
                                            });
                                            break;
                                        case 2:
                                            //删除
                                            mConv.deleteMessage(msg.getId());
                                            mChatAdapter.removeMessage(msg);
                                            break;
                                        default:
                                            break;

                                    }
                                }

                                @Override
                                public void dismiss() {
                                }
                            })
                            .create();
                }
                //除了文本消息类型之外的消息类型
            } else {
                //接收方
                if (msg.getDirect() == MessageDirect.receive) {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mChatView, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("删除"))
                            .setOnItemClickListener(new TipView.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str, final int position) {
                                    if (position == 0) {
                                        //删除
                                        mConv.deleteMessage(msg.getId());
                                        mChatAdapter.removeMessage(msg);
                                    }
                                }

                                @Override
                                public void dismiss() {

                                }
                            })
                            .create();
                    //发送方
                } else {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mChatView, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("撤回"))
                            .addItem(new TipItem("删除"))
                            .setOnItemClickListener(new TipView.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str, final int position) {
                                    switch (position) {
                                        case 0:
                                            //撤回
                                            mConv.retractMessage(msg, new BasicCallback() {
                                                @Override
                                                public void gotResult(int i, String s) {
                                                    if (i == 855001) {
                                                        Toast.makeText(ChatActivity.this, "发送时间过长，不能撤回", Toast.LENGTH_SHORT).show();
                                                    } else if (i == 0) {
                                                        mChatAdapter.delMsgRetract(msg);
                                                    }
                                                }
                                            });
                                            break;

                                        case 1:
                                            //删除
                                            mConv.deleteMessage(msg.getId());
                                            mChatAdapter.removeMessage(msg);
                                            break;
                                        default:
                                            break;

                                    }
                                }

                                @Override
                                public void dismiss() {
                                }
                            })
                            .create();
                }
            }
        }
    };

    /**
     * 消息已读事件
     */
    public void onEventMainThread(MessageReceiptStatusChangeEvent event) {
        List<MessageReceiptStatusChangeEvent.MessageReceiptMeta> messageReceiptMetas = event.getMessageReceiptMetas();
        for (MessageReceiptStatusChangeEvent.MessageReceiptMeta meta : messageReceiptMetas) {
            long serverMsgId = meta.getServerMsgId();
            int unReceiptCnt = meta.getUnReceiptCnt();
            mChatAdapter.setUpdateReceiptCount(serverMsgId, unReceiptCnt);
        }
    }

    public void onEventMainThread(ImageEvent event) {
        Intent intent;
        switch (event.getFlag()) {
            case ImsApplication.IMAGE_MESSAGE:
                int from = PickImageActivity.FROM_LOCAL;
                int requestCode = RequestCode.PICK_IMAGE;
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请在应用管理中打开“读写存储”访问权限！", Toast.LENGTH_LONG).show();
                } else {
                    PickImageActivity.start(ChatActivity.this, requestCode, from, tempFile(), true, 9,
                            true, false, 0, 0);
                }
                break;
            case ImsApplication.TAKE_PHOTO_MESSAGE:
                if ((ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "请在应用管理中打开“相机,读写存储,录音”访问权限！", Toast.LENGTH_LONG).show();
                } else {
                    intent = new Intent(ChatActivity.this, CameraActivity.class);
                    startActivityForResult(intent, RequestCode.TAKE_PHOTO);
                }
                break;
            case ImsApplication.FILE_MESSAGE:
            case ImsApplication.BUSINESS_CARD:
            case ImsApplication.TACK_VIDEO:
            case ImsApplication.TACK_VOICE:
                ToastUtil.shortToast(mContext, "该功能正在添加中");
                break;
            default:
                break;
        }

    }

    private String tempFile() {
        String filename = StringUtil.INSTANCE.get32UUID() + JPG;
        return StorageUtil.getWritePath(filename, StorageType.TYPE_TEMP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.PICK_IMAGE://4
                onPickImageActivityResult(requestCode, data);
                break;
        }

        switch (resultCode) {
            case ImsApplication.RESULT_CODE_AT_MEMBER:
                if (!mIsSingle) {
                    GroupInfo groupInfo = (GroupInfo) mConv.getTargetInfo();
                    String username = data.getStringExtra(ImsApplication.TARGET_ID);
                    String appKey = data.getStringExtra(ImsApplication.TARGET_APP_KEY);
                    UserInfo userInfo = groupInfo.getGroupMemberInfo(username, appKey);
                    if (null == mAtList) {
                        mAtList = new ArrayList<UserInfo>();
                    }
                    mAtList.add(userInfo);
                    mLongClick = true;
                    ekBar.getEtChat().appendMention(data.getStringExtra(ImsApplication.NAME));
                    ekBar.getEtChat().setSelection(ekBar.getEtChat().getText().length());
                }
                break;
            case RequestCode.TAKE_PHOTO:
                if (data != null) {
                    String name = data.getStringExtra("take_photo");
                    Bitmap bitmap = BitmapFactory.decodeFile(name);
                    ImageContent.createImageContentAsync(bitmap, new ImageContent.CreateImageContentCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, ImageContent imageContent) {
                            if (responseCode == 0) {
                                Message msg = mConv.createSendMessage(imageContent);
                                handleSendMsg(msg.getId());
                            }
                        }
                    });
                }
                break;
            case RequestCode.TAKE_VIDEO:
                if (data != null) {
                    String path = data.getStringExtra("video");
                    try {
                        FileContent fileContent = new FileContent(new File(path));
                        fileContent.setStringExtra("video", "mp4");
                        Message msg = mConv.createSendMessage(fileContent);
                        handleSendMsg(msg.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case ImsApplication.RESULT_CODE_SEND_FILE:
                int[] intArrayExtra = data.getIntArrayExtra(MsgIDs);
                for (int msgId : intArrayExtra) {
                    handleSendMsg(msgId);
                }
                break;
            case ImsApplication.RESULT_CODE_CHAT_DETAIL:
                String title = data.getStringExtra(ImsApplication.CONV_TITLE);
                if (!mIsSingle) {
                    GroupInfo groupInfo = (GroupInfo) mConv.getTargetInfo();
                    UserInfo userInfo = groupInfo.getGroupMemberInfo(mMyInfo.getUserName(), mMyInfo.getAppKey());
                    //如果自己在群聊中，同时显示群人数
                    if (userInfo != null) {
                        if (TextUtils.isEmpty(title)) {
                            mChatView.setChatTitle(IdHelper.getString(mContext, "group"),
                                    data.getIntExtra(MEMBERS_COUNT, 0));
                        } else {
//                            mChatView.setChatTitle(title, data.getIntExtra(MEMBERS_COUNT, 0));
                        }
                    } else {
                        if (TextUtils.isEmpty(title)) {
                            mChatView.setChatTitle(IdHelper.getString(mContext, "group"));
                        } else {
                            mChatView.setChatTitle(title);
                        }
                        mChatView.dismissGroupNum();
                    }

                } else mChatView.setChatTitle(title);
                if (data.getBooleanExtra("deleteMsg", false)) {
                    mChatAdapter.clearMsgList();
                }
                break;
        }

    }


    /**
     * 图片选取回调
     */
    private void onPickImageActivityResult(int requestCode, Intent data) {
        if (data == null) {
            return;
        }
        boolean local = data.getBooleanExtra(Extras.EXTRA_FROM_LOCAL, false);
        if (local) {
            // 本地相册
            sendImageAfterSelfImagePicker(data);
        }
    }

    /**
     * 发送图片
     */

    private void sendImageAfterSelfImagePicker(final Intent data) {
        SendImageHelper.sendImageAfterSelfImagePicker(this, data, new SendImageHelper.Callback() {
            @Override
            public void sendImage(final File file, boolean isOrig) {
                //所有图片都在这里拿到
                ImageContent.createImageContentAsync(file, new ImageContent.CreateImageContentCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, ImageContent imageContent) {
                        if (responseCode == 0) {
                            Message msg = mConv.createSendMessage(imageContent);
                            handleSendMsg(msg.getId());
                        }
                    }
                });

            }
        });
    }

    //发送极光熊
    private void OnSendImage(String iconUri) {
        String substring = iconUri.substring(7);
        File file = new File(substring);
        ImageContent.createImageContentAsync(file, new ImageContent.CreateImageContentCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, ImageContent imageContent) {
                if (responseCode == 0) {
                    imageContent.setStringExtra("jiguang", "xiong");
                    Message msg = mConv.createSendMessage(imageContent);
                    handleSendMsg(msg.getId());
                } else {
                    ToastUtil.shortToast(mContext, responseMessage);
                }
            }
        });
    }

    /**
     * 处理发送图片，刷新界面
     * @param data intent
     */
    private void handleSendMsg(int data) {
        mChatAdapter.setSendMsgs(data);
        mChatView.setToBottom();
    }

    private static class UIHandler extends Handler {
        private final WeakReference<ChatActivity> mActivity;

        public UIHandler(ChatActivity activity) {
            mActivity = new WeakReference<ChatActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            ChatActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case REFRESH_LAST_PAGE:
                        activity.mChatAdapter.dropDownToRefresh();
                        activity.mChatView.getListView().onDropDownComplete();
                        if (activity.mChatAdapter.isHasLastPage()) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                activity.mChatView.getListView()
                                        .setSelectionFromTop(activity.mChatAdapter.getOffset(),
                                                activity.mChatView.getListView().getHeaderHeight());
                            } else {
                                activity.mChatView.getListView().setSelection(activity.mChatAdapter
                                        .getOffset());
                            }
                            activity.mChatAdapter.refreshStartPosition();
                        } else {
                            activity.mChatView.getListView().setSelection(0);
                        }
                        //显示上一页的消息数18条
                        activity.mChatView.getListView()
                                .setOffset(activity.mChatAdapter.getOffset());
                        break;
                    case REFRESH_GROUP_NAME:
                    case REFRESH_CHAT_TITLE:
                        break;
                }
            }
        }
    }

}
