package ims.chat.ui.controller


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast

import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.squareup.picasso.Picasso

import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.LinkedList

import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.DownloadCompletionCallback
import cn.jpush.im.android.api.callback.GetUserInfoCallback
import cn.jpush.im.android.api.callback.ProgressUpdateCallback
import cn.jpush.im.android.api.content.CustomContent
import cn.jpush.im.android.api.content.EventNotificationContent
import cn.jpush.im.android.api.content.FileContent
import cn.jpush.im.android.api.content.ImageContent
import cn.jpush.im.android.api.content.LocationContent
import cn.jpush.im.android.api.content.PromptContent
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.content.VoiceContent
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.enums.MessageDirect
import cn.jpush.im.android.api.enums.MessageStatus
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.Message
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.android.api.options.MessageSendingOptions
import cn.jpush.im.api.BasicCallback
import ims.chat.R

import ims.chat.adapter.ChattingListAdapter
import ims.chat.adapter.ChattingListAdapter.ViewHolder
import ims.chat.application.ImsApplication
import ims.chat.ui.activity.BrowserViewPagerActivity
import ims.chat.ui.activity.DownLoadActivity
import ims.chat.ui.activity.FriendInfoActivity
import ims.chat.ui.activity.GroupNotFriendActivity
import ims.chat.utils.*


class ChatItemController(
    private val mAdapter: ChattingListAdapter,
    private val mContext: Activity,
    private val mConv: Conversation,
    private val mMsgList: MutableList<Message>,
    private val mDensity: Float,
    private val mLongClickListener: ChattingListAdapter.ContentLongClickListener
) {
    var mSendingAnim: Animation
    private var mSetData = false
    private val mp = MediaPlayer()
    private var mVoiceAnimation: AnimationDrawable? = null
    private var mPosition = -1// 和mSetData一起组成判断播放哪条录音的依据
    private val mIndexList = ArrayList<Int>()//语音索引
    private var mFIS: FileInputStream? = null
    private var mFD: FileDescriptor? = null
    private var autoPlay = false
    private var nextPlayPosition = 0
    private var mIsEarPhoneOn: Boolean = false
    private var mSendMsgId: Int = 0
    private val mMsgQueue = LinkedList<Message>()
    private var mUserInfo: UserInfo? = null
    private val mUserInfoMap = HashMap<Int, UserInfo>()


    private val options = createImageOptions()

    private val hasLoaded = false

    private val imgMsgIDList: ArrayList<Int>
        get() {
            val imgMsgIDList = ArrayList<Int>()
            for (msg in mMsgList) {
                if (msg.contentType == ContentType.image) {
                    imgMsgIDList.add(msg.id)
                }
            }
            return imgMsgIDList
        }

    init {
        if (mConv.type == ConversationType.single) {
            mUserInfo = mConv.targetInfo as UserInfo
        }
        mSendingAnim = AnimationUtils.loadAnimation(mContext, R.anim.jmui_rotate)
        val lin = LinearInterpolator()
        mSendingAnim.interpolator = lin

        val audioManager = mContext
            .getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = AudioManager.MODE_NORMAL
        audioManager.isSpeakerphoneOn = audioManager.isSpeakerphoneOn
        mp.setAudioStreamType(AudioManager.STREAM_RING)
        mp.setOnErrorListener { _, _, _ -> false }
    }

    fun handleTextMsg(msg: Message, holder: ViewHolder, position: Int) {
        val content = (msg.content as TextContent).text
        SimpleCommonUtils.spannableEmoticonFilter(holder.txtContent, content)
        holder.txtContent.text = content
        holder.txtContent.tag = position
        holder.txtContent.setOnLongClickListener(mLongClickListener)
        // 检查发送状态，发送方有重发机制
        if (msg.direct == MessageDirect.send) {
            when (msg.status) {
                MessageStatus.created -> if (null != mUserInfo) {
                    holder.sendingIv.visibility = View.GONE
                    holder.resend.visibility = View.VISIBLE
                    holder.text_receipt.visibility = View.GONE
                }
                MessageStatus.send_success -> {
                    holder.text_receipt.visibility = View.VISIBLE
                    holder.sendingIv.clearAnimation()
                    holder.sendingIv.visibility = View.GONE
                    holder.resend.visibility = View.GONE
                }
                MessageStatus.send_fail -> {
                    holder.text_receipt.visibility = View.GONE
                    holder.sendingIv.clearAnimation()
                    holder.sendingIv.visibility = View.GONE
                    holder.resend.visibility = View.VISIBLE
                }
                MessageStatus.send_going -> sendingTextOrVoice(holder, msg)
            }

        } else {
            if (mConv.type == ConversationType.group) {
                if (msg.isAtMe) {
                    mConv.updateMessageExtra(msg, "isRead", true)
                }
                if (msg.isAtAll) {
                    mConv.updateMessageExtra(msg, "isReadAtAll", true)
                }
                holder.displayName.visibility = View.VISIBLE
                if (TextUtils.isEmpty(msg.fromUser.nickname)) {
                    holder.displayName.text = msg.fromUser.userName
                } else {
                    holder.displayName.text = msg.fromUser.nickname
                }
            }
        }
        if (holder.resend != null) {
            holder.resend.setOnClickListener { mAdapter.showResendDialog(holder, msg) }
        }
    }

    // 处理图片
    fun handleImgMsg(msg: Message, holder: ViewHolder, position: Int) {
        val imgContent = msg.content as ImageContent
        val jiguang = imgContent.getStringExtra("jiguang")
        // 先拿本地缩略图
        val path = imgContent.localThumbnailPath
        if (path == null) {
            //从服务器上拿缩略图
            imgContent.downloadThumbnailImage(msg, object : DownloadCompletionCallback() {
                override fun onComplete(status: Int, desc: String, file: File) {
                    if (status == 0) {
                        val imageView = setPictureScale(jiguang, msg, file.path, holder.picture)
                        Picasso.with(mContext).load(file).into(imageView)
                    }
                }
            })
        } else {
            val imageView = setPictureScale(jiguang, msg, path, holder.picture)
            Picasso.with(mContext).load(File(path)).into(imageView)
        }

        // 接收图片
        if (msg.direct == MessageDirect.receive) {
            //群聊中显示昵称
            if (mConv.type == ConversationType.group) {
                holder.displayName.visibility = View.VISIBLE
                if (TextUtils.isEmpty(msg.fromUser.nickname)) {
                    holder.displayName.text = msg.fromUser.userName
                } else {
                    holder.displayName.text = msg.fromUser.nickname
                }
            }

            when (msg.status) {
                MessageStatus.receive_fail -> {
                    holder.picture.setImageResource(R.drawable.jmui_fetch_failed)
                    holder.resend.visibility = View.VISIBLE
                    holder.resend.setOnClickListener {
                        imgContent.downloadOriginImage(msg, object : DownloadCompletionCallback() {
                            override fun onComplete(i: Int, s: String, file: File) {
                                if (i == 0) {
                                    ToastUtil.shortToast(mContext, "下载成功")
                                    holder.sendingIv.visibility = View.GONE
                                    mAdapter.notifyDataSetChanged()
                                } else {
                                    ToastUtil.shortToast(mContext, "下载失败$s")
                                }
                            }
                        })
                    }
                }
            }
            // 发送图片方，直接加载缩略图
        } else {
            //检查状态
            when (msg.status) {
                MessageStatus.created -> {
                    holder.picture.isEnabled = false
                    holder.resend.isEnabled = false
                    holder.text_receipt.visibility = View.GONE
                    holder.sendingIv.visibility = View.VISIBLE
                    holder.resend.visibility = View.GONE
                    holder.progressTv.text = "0%"
                }
                MessageStatus.send_success -> {
                    holder.picture.isEnabled = true
                    holder.sendingIv.clearAnimation()
                    holder.text_receipt.visibility = View.VISIBLE
                    holder.sendingIv.visibility = View.GONE
                    holder.picture.alpha = 1.0f
                    holder.progressTv.visibility = View.GONE
                    holder.resend.visibility = View.GONE
                }
                MessageStatus.send_fail -> {
                    holder.resend.isEnabled = true
                    holder.picture.isEnabled = true
                    holder.sendingIv.clearAnimation()
                    holder.sendingIv.visibility = View.GONE
                    holder.text_receipt.visibility = View.GONE
                    holder.picture.alpha = 1.0f
                    holder.progressTv.visibility = View.GONE
                    holder.resend.visibility = View.VISIBLE
                }
                MessageStatus.send_going -> {
                    holder.picture.isEnabled = false
                    holder.resend.isEnabled = false
                    holder.text_receipt.visibility = View.GONE
                    holder.resend.visibility = View.GONE
                    sendingImage(msg, holder)
                }
                else -> {
                    holder.picture.alpha = 0.75f
                    holder.sendingIv.visibility = View.VISIBLE
                    holder.sendingIv.startAnimation(mSendingAnim)
                    holder.progressTv.visibility = View.VISIBLE
                    holder.progressTv.text = "0%"
                    holder.resend.visibility = View.GONE
                    //从别的界面返回聊天界面，继续发送
                    if (!mMsgQueue.isEmpty()) {
                        val message = mMsgQueue.element()
                        if (message.id == msg.id) {
                            val options = MessageSendingOptions()
                            options.isNeedReadReceipt = true
                            JMessageClient.sendMessage(message, options)
                            mSendMsgId = message.id
                            sendingImage(message, holder)
                        }
                    }
                }
            }
        }
        if (holder.picture != null) {
            // 点击预览图片
            holder.picture.setOnClickListener(BtnOrTxtListener(position, holder))
            holder.picture.tag = position
            holder.picture.setOnLongClickListener(mLongClickListener)

        }
        if (msg.direct == MessageDirect.send && holder.resend != null) {
            holder.resend.setOnClickListener { mAdapter.showResendDialog(holder, msg) }
        }
    }

    private fun sendingImage(msg: Message, holder: ViewHolder) {
        holder.picture.alpha = 0.75f
        holder.sendingIv.visibility = View.VISIBLE
        holder.sendingIv.startAnimation(mSendingAnim)
        holder.progressTv.visibility = View.VISIBLE
        holder.progressTv.text = "0%"
        holder.resend.visibility = View.GONE
        //如果图片正在发送，重新注册上传进度Callback
        if (!msg.isContentUploadProgressCallbackExists) {
            msg.setOnContentUploadProgressCallback(object : ProgressUpdateCallback() {
                override fun onProgressUpdate(v: Double) {
                    val progressStr = (v * 100).toInt().toString() + "%"
                    holder.progressTv.text = progressStr
                }
            })
        }
        if (!msg.isSendCompleteCallbackExists) {
            msg.setOnSendCompleteCallback(object : BasicCallback() {
                override fun gotResult(status: Int, desc: String) {
                    if (!mMsgQueue.isEmpty() && mMsgQueue.element().id == mSendMsgId) {
                        mMsgQueue.poll()
                        if (!mMsgQueue.isEmpty()) {
                            val nextMsg = mMsgQueue.element()
                            val options = MessageSendingOptions()
                            options.isNeedReadReceipt = true
                            JMessageClient.sendMessage(nextMsg, options)
                            mSendMsgId = nextMsg.id
                        }
                    }
                    holder.picture.alpha = 1.0f
                    holder.sendingIv.clearAnimation()
                    holder.sendingIv.visibility = View.GONE
                    holder.progressTv.visibility = View.GONE
                    if (status == 803008) {
                        val customContent = CustomContent()
                        customContent.setBooleanValue("blackList", true)
                        val customMsg = mConv.createSendMessage(customContent)
                        mAdapter.addMsgToList(customMsg)
                    } else if (status != 0) {
                        holder.resend.visibility = View.VISIBLE
                    }

                    val message = mConv.getMessage(msg.id)
                    mMsgList[mMsgList.indexOf(msg)] = message
                    //                    notifyDataSetChanged();
                }
            })

        }
    }

    fun handleVoiceMsg(msg: Message, holder: ViewHolder, position: Int) {
        val content = msg.content as VoiceContent
        val msgDirect = msg.direct
        val length = content.duration
        val lengthStr = length.toString() + mContext.getString(R.string.jmui_symbol_second)
        holder.voiceLength.text = lengthStr
        //控制语音长度显示，长度增幅随语音长度逐渐缩小
        val width = (-0.04 * length.toDouble() * length.toDouble() + 4.526 * length + 75.214).toInt()
        holder.txtContent.width = (width * mDensity).toInt()
        //要设置这个position
        holder.txtContent.tag = position
        holder.txtContent.setOnLongClickListener(mLongClickListener)
        if (msgDirect == MessageDirect.send) {
            holder.voice.setImageResource(R.drawable.send_3)
            when (msg.status) {
                MessageStatus.created -> {
                    holder.sendingIv.visibility = View.VISIBLE
                    holder.resend.visibility = View.GONE
                    holder.text_receipt.visibility = View.GONE
                }
                MessageStatus.send_success -> {
                    holder.sendingIv.clearAnimation()
                    holder.sendingIv.visibility = View.GONE
                    holder.resend.visibility = View.GONE
                    holder.text_receipt.visibility = View.VISIBLE
                }
                MessageStatus.send_fail -> {
                    holder.sendingIv.clearAnimation()
                    holder.sendingIv.visibility = View.GONE
                    holder.text_receipt.visibility = View.GONE
                    holder.resend.visibility = View.VISIBLE
                }
                MessageStatus.send_going -> sendingTextOrVoice(holder, msg)
            }
        } else
            when (msg.status) {
                MessageStatus.receive_success -> {
                    if (mConv.type == ConversationType.group) {
                        holder.displayName.visibility = View.VISIBLE
                        if (TextUtils.isEmpty(msg.fromUser.nickname)) {
                            holder.displayName.text = msg.fromUser.userName
                        } else {
                            holder.displayName.text = msg.fromUser.nickname
                        }
                    }
                    holder.voice.setImageResource(R.drawable.jmui_receive_3)
                    // 收到语音，设置未读
                    if (msg.content.getBooleanExtra("isRead") == null || !msg.content.getBooleanExtra("isRead")) {
                        mConv.updateMessageExtra(msg, "isRead", false)
                        holder.readStatus.visibility = View.VISIBLE
                        if (mIndexList.size > 0) {
                            if (!mIndexList.contains(position)) {
                                addToListAndSort(position)
                            }
                        } else {
                            addToListAndSort(position)
                        }
                        if (nextPlayPosition == position && autoPlay) {
                            playVoice(position, holder, false)
                        }
                    } else if (msg.content.getBooleanExtra("isRead")!!) {
                        holder.readStatus.visibility = View.GONE
                    }
                }
                MessageStatus.receive_fail -> {
                    holder.voice.setImageResource(R.drawable.jmui_receive_3)
                    // 接收失败，从服务器上下载
                    content.downloadVoiceFile(msg,
                        object : DownloadCompletionCallback() {
                            override fun onComplete(status: Int, desc: String, file: File) {

                            }
                        })
                }
                MessageStatus.receive_going -> {
                }
            }

        if (holder.resend != null) {
            holder.resend.setOnClickListener {
                if (msg.content != null) {
                    mAdapter.showResendDialog(holder, msg)
                } else {
                    Toast.makeText(mContext, R.string.jmui_sdcard_not_exist_toast, Toast.LENGTH_SHORT).show()
                }
            }
        }
        holder.txtContent.setOnClickListener(BtnOrTxtListener(position, holder))
    }



    //正在发送文字或语音
    private fun sendingTextOrVoice(holder: ViewHolder, msg: Message) {
        holder.text_receipt.visibility = View.GONE
        holder.resend.visibility = View.GONE
        holder.sendingIv.visibility = View.VISIBLE
        holder.sendingIv.startAnimation(mSendingAnim)
        //消息正在发送，重新注册一个监听消息发送完成的Callback
        if (!msg.isSendCompleteCallbackExists) {
            msg.setOnSendCompleteCallback(object : BasicCallback() {
                override fun gotResult(status: Int, desc: String) {
                    holder.sendingIv.visibility = View.GONE
                    holder.sendingIv.clearAnimation()
                    if (status == 803008) {
                        val customContent = CustomContent()
                        customContent.setBooleanValue("blackList", true)
                        val customMsg = mConv.createSendMessage(customContent)
                        mAdapter.addMsgToList(customMsg)
                    } else if (status != 0) {
                        holder.resend.visibility = View.VISIBLE
                        HandleResponseCode.onHandle(mContext, status, false)
                    }
                }
            })
        }
    }

    //小视频
    fun handleVideo(msg: Message, holder: ViewHolder, position: Int) {
        val fileContent = msg.content as FileContent
        val videoPath = fileContent.localPath
        if (videoPath != null) {
            //            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + msg.getServerMessageId();
            val thumbPath = Environment.getExternalStorageDirectory().absolutePath + "/" + msg.serverMessageId
            val path = BitmapDecoder.extractThumbnail(videoPath, thumbPath)
            setPictureScale(null, msg, path, holder.picture)
            Picasso.with(mContext).load(File(path)).into(holder.picture)
        } else {
            Picasso.with(mContext).load(R.drawable.video_not_found).into(holder.picture)
        }

        if (msg.direct == MessageDirect.send) {
            when (msg.status) {
                MessageStatus.created -> {
                    holder.videoPlay.visibility = View.GONE
                    holder.text_receipt.visibility = View.GONE
                    if (null != mUserInfo) {
                        holder.sendingIv.visibility = View.GONE
                        holder.resend.visibility = View.VISIBLE
                    } else {
                        holder.sendingIv.visibility = View.VISIBLE
                        holder.resend.visibility = View.GONE
                    }
                }
                MessageStatus.send_success -> {
                    holder.sendingIv.clearAnimation()
                    holder.picture.alpha = 1.0f
                    holder.text_receipt.visibility = View.VISIBLE
                    holder.sendingIv.visibility = View.GONE
                    holder.progressTv.visibility = View.GONE
                    holder.resend.visibility = View.GONE
                    holder.videoPlay.visibility = View.VISIBLE
                }
                MessageStatus.send_fail -> {
                    holder.sendingIv.clearAnimation()
                    holder.sendingIv.visibility = View.GONE
                    holder.picture.alpha = 1.0f
                    holder.text_receipt.visibility = View.GONE
                    holder.progressTv.visibility = View.GONE
                    holder.resend.visibility = View.VISIBLE
                    holder.videoPlay.visibility = View.VISIBLE
                }
                MessageStatus.send_going -> {
                    holder.text_receipt.visibility = View.GONE
                    holder.videoPlay.visibility = View.GONE
                    sendingImage(msg, holder)
                }
                else -> {
                    holder.picture.alpha = 0.75f
                    holder.sendingIv.visibility = View.VISIBLE
                    holder.sendingIv.startAnimation(mSendingAnim)
                    holder.progressTv.visibility = View.VISIBLE
                    holder.videoPlay.visibility = View.GONE
                    holder.progressTv.text = "0%"
                    holder.resend.visibility = View.GONE
                    //从别的界面返回聊天界面，继续发送
                    if (!mMsgQueue.isEmpty()) {
                        val message = mMsgQueue.element()
                        if (message.id == msg.id) {
                            val options = MessageSendingOptions()
                            options.isNeedReadReceipt = true
                            JMessageClient.sendMessage(message, options)
                            mSendMsgId = message.id
                            sendingImage(message, holder)
                        }
                    }
                }
            }

            holder.resend.setOnClickListener { mAdapter.showResendDialog(holder, msg) }

        } else {
            when (msg.status) {
                MessageStatus.receive_going -> holder.videoPlay.visibility = View.VISIBLE
                MessageStatus.receive_fail -> holder.videoPlay.visibility = View.VISIBLE
                MessageStatus.receive_success -> holder.videoPlay.visibility = View.VISIBLE
                else -> {
                }
            }

        }
        holder.picture.setOnClickListener(BtnOrTxtListener(position, holder))
        holder.picture.tag = position
        holder.picture.setOnLongClickListener(mLongClickListener)
    }


    fun handleFileMsg(msg: Message, holder: ViewHolder, position: Int) {
        val content = msg.content as FileContent
        if (holder.txtContent != null) {
            holder.txtContent.text = content.fileName
        }
        val fileSize = content.getNumberExtra("fileSize")
        if (fileSize != null && holder.sizeTv != null) {
            val size = FileUtils.getFileSize(fileSize)
            holder.sizeTv.text = size
        }
        val fileType = content.getStringExtra("fileType")
        val drawable: Drawable
        if (fileType != null && (fileType == "mp4" || fileType == "mov" || fileType == "rm" ||
                    fileType == "rmvb" || fileType == "wmv" || fileType == "avi" ||
                    fileType == "3gp" || fileType == "mkv")
        ) {
            drawable = mContext.resources.getDrawable(R.drawable.jmui_video)
        } else if (fileType != null && (fileType == "wav" || fileType == "mp3" || fileType == "wma" || fileType == "midi")) {
            drawable = mContext.resources.getDrawable(R.drawable.jmui_audio)
        } else if (fileType != null && (fileType == "ppt" || fileType == "pptx" || fileType == "doc" ||
                    fileType == "docx" || fileType == "pdf" || fileType == "xls" ||
                    fileType == "xlsx" || fileType == "txt" || fileType == "wps")
        ) {
            drawable = mContext.resources.getDrawable(R.drawable.jmui_document)
            //.jpeg .jpg .png .bmp .gif
        } else if (fileType != null && (fileType == "jpeg" || fileType == "jpg" || fileType == "png" ||
                    fileType == "bmp" || fileType == "gif")
        ) {
            drawable = mContext.resources.getDrawable(R.drawable.image_file)
        } else {
            drawable = mContext.resources.getDrawable(R.drawable.jmui_other)
        }
        val bitmapDrawable = drawable as BitmapDrawable
        if (holder.ivDocument != null)
            holder.ivDocument.setImageBitmap(bitmapDrawable.bitmap)

        if (msg.direct == MessageDirect.send) {
            when (msg.status) {
                MessageStatus.created -> {
                    holder.progressTv.visibility = View.VISIBLE
                    holder.progressTv.text = "0%"
                    holder.resend.visibility = View.GONE
                    holder.text_receipt.visibility = View.GONE
                    if (null != mUserInfo) {
                        holder.progressTv.visibility = View.GONE
                        holder.resend.visibility = View.VISIBLE
                    } else {
                        holder.progressTv.visibility = View.VISIBLE
                        holder.progressTv.text = "0%"
                        holder.resend.visibility = View.GONE
                    }
                }
                MessageStatus.send_going -> {
                    holder.text_receipt.visibility = View.GONE
                    holder.progressTv.visibility = View.VISIBLE
                    holder.resend.visibility = View.GONE
                    if (!msg.isContentUploadProgressCallbackExists) {
                        msg.setOnContentUploadProgressCallback(object : ProgressUpdateCallback() {
                            override fun onProgressUpdate(v: Double) {
                                val progressStr = (v * 100).toInt().toString() + "%"
                                holder.progressTv.text = progressStr
                            }
                        })
                    }
                    if (!msg.isSendCompleteCallbackExists) {
                        msg.setOnSendCompleteCallback(object : BasicCallback() {
                            override fun gotResult(status: Int, desc: String) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    holder.contentLl.background = mContext.getDrawable(R.drawable.jmui_msg_send_bg)
                                }
                                holder.progressTv.visibility = View.GONE
                                if (status == 803008) {
                                    val customContent = CustomContent()
                                    customContent.setBooleanValue("blackList", true)
                                    val customMsg = mConv.createSendMessage(customContent)
                                    mAdapter.addMsgToList(customMsg)
                                } else if (status != 0) {
                                    holder.resend.visibility = View.VISIBLE
                                }
                            }
                        })
                    }
                }
                MessageStatus.send_success -> {
                    holder.text_receipt.visibility = View.VISIBLE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.contentLl.background = mContext.getDrawable(R.drawable.jmui_msg_send_bg)
                    }
                    holder.alreadySend.visibility = View.VISIBLE
                    holder.progressTv.visibility = View.GONE
                    holder.resend.visibility = View.GONE
                }
                MessageStatus.send_fail -> {
                    holder.alreadySend.visibility = View.VISIBLE
                    holder.alreadySend.text = "发送失败"
                    holder.text_receipt.visibility = View.GONE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.contentLl.background = mContext.getDrawable(R.drawable.jmui_msg_send_bg)
                    }
                    holder.progressTv.visibility = View.GONE
                    holder.resend.visibility = View.VISIBLE
                }
            }
        } else {
            when (msg.status) {
                MessageStatus.receive_going -> {
                    holder.contentLl.setBackgroundColor(Color.parseColor("#86222222"))
                    holder.progressTv.visibility = View.VISIBLE
                    holder.fileLoad.text = ""
                    if (!msg.isContentDownloadProgressCallbackExists) {
                        msg.setOnContentDownloadProgressCallback(object : ProgressUpdateCallback() {
                            override fun onProgressUpdate(v: Double) {
                                if (v < 1) {
                                    val progressStr = (v * 100).toInt().toString() + "%"
                                    holder.progressTv.text = progressStr
                                } else {
                                    holder.progressTv.visibility = View.GONE
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        holder.contentLl.background =
                                            mContext.getDrawable(R.drawable.jmui_msg_receive_bg)
                                    }
                                }

                            }
                        })
                    }
                }
                MessageStatus.receive_fail//收到文件没下载也是这个状态
                -> {
                    holder.progressTv.visibility = View.GONE
                    holder.contentLl.background = ContextCompat.getDrawable(mContext, R.drawable.jmui_msg_receive_bg)
                    holder.fileLoad.text = "未下载"
                }
                MessageStatus.receive_success -> {
                    holder.progressTv.visibility = View.GONE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.contentLl.background = mContext.getDrawable(R.drawable.jmui_msg_receive_bg)
                    }
                    holder.fileLoad.text = "已下载"
                }
            }
        }

        if (holder.fileLoad != null) {
            holder.fileLoad.setOnClickListener {
                if (msg.direct == MessageDirect.send) {
                    mAdapter.showResendDialog(holder, msg)
                } else {
                    holder.contentLl.setBackgroundColor(Color.parseColor("#86222222"))
                    holder.progressTv.text = "0%"
                    holder.progressTv.visibility = View.VISIBLE
                    if (!msg.isContentDownloadProgressCallbackExists) {
                        msg.setOnContentDownloadProgressCallback(object : ProgressUpdateCallback() {
                            override fun onProgressUpdate(v: Double) {
                                val progressStr = (v * 100).toInt().toString() + "%"
                                holder.progressTv.text = progressStr
                            }
                        })
                    }
                    content.downloadFile(msg, object : DownloadCompletionCallback() {
                        override fun onComplete(status: Int, desc: String, file: File) {
                            holder.progressTv.visibility = View.GONE
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                holder.contentLl.background = mContext.getDrawable(R.drawable.jmui_msg_receive_bg)
                            }
                            if (status != 0) {
                                holder.fileLoad.text = "未下载"
                                Toast.makeText(
                                    mContext, R.string.download_file_failed,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(mContext, R.string.download_file_succeed, Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            }
        }
        holder.contentLl.tag = position
        holder.contentLl.setOnLongClickListener(mLongClickListener)
        holder.contentLl.setOnClickListener(BtnOrTxtListener(position, holder))
    }


    fun handleGroupChangeMsg(msg: Message, holder: ViewHolder) {
        val content = (msg.content as EventNotificationContent).eventText
        val type = (msg
            .content as EventNotificationContent).eventNotificationType
        when (type) {
            EventNotificationContent.EventNotificationType.group_member_added, EventNotificationContent.EventNotificationType.group_member_exit, EventNotificationContent.EventNotificationType.group_member_removed, EventNotificationContent.EventNotificationType.group_info_updated -> {
                holder.groupChange.text = content
                holder.groupChange.visibility = View.VISIBLE
                holder.msgTime.visibility = View.GONE
            }
        }
    }

    fun handlePromptMsg(msg: Message, holder: ViewHolder) {
        val promptText = (msg.content as PromptContent).promptText
        holder.groupChange.text = promptText
        holder.groupChange.visibility = View.VISIBLE
        holder.msgTime.visibility = View.GONE
    }

    fun handleCustomMsg(msg: Message, holder: ViewHolder) {
        val content = msg.content as CustomContent
        val isBlackListHint = content.getBooleanValue("blackList")
        val notFriendFlag = content.getBooleanValue("notFriend")
        if (isBlackListHint != null && isBlackListHint) {
            holder.groupChange.setText(R.string.jmui_server_803008)
            holder.groupChange.visibility = View.VISIBLE
        } else {
            holder.groupChange.visibility = View.GONE
        }

        holder.groupChange.visibility = View.GONE
    }

    inner class BtnOrTxtListener(private val position: Int, private val holder: ViewHolder) : View.OnClickListener {

        override fun onClick(v: View) {
            val msg = mMsgList[position]
            val msgDirect = msg.direct
            when (msg.contentType) {
                ContentType.voice -> {
                    if (!FileHelper.isSdCardExist()) {
                        Toast.makeText(
                            mContext, R.string.jmui_sdcard_not_exist_toast,
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    // 如果之前存在播放动画，无论这次点击触发的是暂停还是播放，停止上次播放的动画
                    if (mVoiceAnimation != null) {
                        mVoiceAnimation!!.stop()
                    }
                    // 播放中点击了正在播放的Item 则暂停播放
                    if (mp.isPlaying && mPosition == position) {
                        if (msgDirect == MessageDirect.send) {
                            holder.voice.setImageResource(R.drawable.jmui_voice_send)
                        } else {
                            holder.voice.setImageResource(R.drawable.jmui_voice_receive)
                        }
                        mVoiceAnimation = holder.voice.drawable as AnimationDrawable
                        pauseVoice(msgDirect, holder.voice)
                        // 开始播放录音
                    } else if (msgDirect == MessageDirect.send) {
                        holder.voice.setImageResource(R.drawable.jmui_voice_send)
                        mVoiceAnimation = holder.voice.drawable as AnimationDrawable

                        // 继续播放之前暂停的录音
                        if (mSetData && mPosition == position) {
                            mVoiceAnimation!!.start()
                            mp.start()
                            // 否则重新播放该录音或者其他录音
                        } else {
                            playVoice(position, holder, true)
                        }
                        // 语音接收方特殊处理，自动连续播放未读语音
                    } else {
                        try {
                            // 继续播放之前暂停的录音
                            if (mSetData && mPosition == position) {
                                if (mVoiceAnimation != null) {
                                    mVoiceAnimation!!.start()
                                }
                                mp.start()
                                // 否则开始播放另一条录音
                            } else {
                                // 选中的录音是否已经播放过，如果未播放，自动连续播放这条语音之后未播放的语音
                                if (msg.content.getBooleanExtra("isRead") == null || !msg.content.getBooleanExtra("isRead")) {
                                    autoPlay = true
                                    playVoice(position, holder, false)
                                    // 否则直接播放选中的语音
                                } else {
                                    holder.voice.setImageResource(R.drawable.jmui_voice_receive)
                                    mVoiceAnimation = holder.voice.drawable as AnimationDrawable
                                    playVoice(position, holder, false)
                                }
                            }
                        } catch (e: IllegalArgumentException) {
                            e.printStackTrace()
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }

                    }
                }
                ContentType.image -> if (holder.picture != null && v.id == holder.picture.id) {
                    val intent = Intent()
                    intent.putExtra(ImsApplication.TARGET_ID, mConv.targetId)
                    intent.putExtra("msgId", msg.id)
                    intent.putExtra(ImsApplication.TARGET_APP_KEY, mConv.targetAppKey)
                    intent.putExtra("msgCount", mMsgList.size)
                    intent.putIntegerArrayListExtra(ImsApplication.MsgIDs, imgMsgIDList)
                    intent.putExtra("fromChatActivity", true)
                    intent.setClass(mContext, BrowserViewPagerActivity::class.java)
                    mContext.startActivity(intent)
                }
                ContentType.file -> {
                    val content = msg.content as FileContent
                    var fileName = content.fileName
                    val extra = content.getStringExtra("video")
                    if (extra != null) {
                        fileName = msg.serverMessageId.toString() + "." + extra
                    }
                    val path = content.localPath
                    if (path != null && File(path).exists()) {
                        val newPath = ImsApplication.FILE_DIR + fileName
                        val file = File(newPath)
                        if (file.exists() && file.isFile) {
                            browseDocument(fileName, newPath)
                        } else {
                            val finalFileName = fileName
                            FileHelper.getInstance().copyFile(
                                fileName, path, mContext
                            ) { browseDocument(finalFileName, newPath) }
                        }
                    } else {
                        org.greenrobot.eventbus.EventBus.getDefault().postSticky(msg)
                        val intent = Intent(mContext, DownLoadActivity::class.java)
                        mContext.startActivity(intent)
                    }
                }
            }

        }
    }


    fun playVoice(position: Int, holder: ViewHolder, isSender: Boolean) {
        // 记录播放录音的位置
        mPosition = position
        val msg = mMsgList[position]
        if (autoPlay) {
            mConv.updateMessageExtra(msg, "isRead", true)
            holder.readStatus.visibility = View.GONE
            if (mVoiceAnimation != null) {
                mVoiceAnimation!!.stop()
                mVoiceAnimation = null
            }
            holder.voice.setImageResource(R.drawable.jmui_voice_receive)
            mVoiceAnimation = holder.voice.drawable as AnimationDrawable
        }
        try {
            mp.reset()
            val vc = msg.content as VoiceContent
            mFIS = FileInputStream(vc.localPath)
            mFD = mFIS!!.fd
            mp.setDataSource(mFD)
            if (mIsEarPhoneOn) {
                mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL)
            } else {
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC)
            }
            mp.prepare()
            mp.setOnPreparedListener { mp ->
                mVoiceAnimation!!.start()
                mp.start()
            }
            mp.setOnCompletionListener { mp ->
                mVoiceAnimation!!.stop()
                mp.reset()
                mSetData = false
                if (isSender) {
                    holder.voice.setImageResource(R.drawable.send_3)
                } else {
                    holder.voice.setImageResource(R.drawable.jmui_receive_3)
                }
                if (autoPlay) {
                    val curCount = mIndexList.indexOf(position)
                    if (curCount + 1 >= mIndexList.size) {
                        nextPlayPosition = -1
                        autoPlay = false
                    } else {
                        nextPlayPosition = mIndexList[curCount + 1]
                        mAdapter.notifyDataSetChanged()
                    }
                    mIndexList.removeAt(curCount)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                mContext, R.string.jmui_file_not_found_toast,
                Toast.LENGTH_SHORT
            ).show()
            val vc = msg.content as VoiceContent
            vc.downloadVoiceFile(msg, object : DownloadCompletionCallback() {
                override fun onComplete(status: Int, desc: String, file: File) {
                    if (status == 0) {
                        Toast.makeText(
                            mContext, R.string.download_completed_toast,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            mContext, R.string.file_fetch_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        } finally {
            try {
                if (mFIS != null) {
                    mFIS!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }

    /**
     * 设置图片最小宽高
     *
     * @param path      图片路径
     * @param imageView 显示图片的View
     */
    private fun setPictureScale(extra: String?, message: Message, path: String, imageView: ImageView): ImageView {

        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, opts)


        //计算图片缩放比例
        val imageWidth = opts.outWidth.toDouble()
        val imageHeight = opts.outHeight.toDouble()
        return setDensity(extra, imageWidth, imageHeight, imageView)
    }

    private fun setDensity(
        extra: String?,
        imageWidth: Double,
        imageHeight: Double,
        imageView: ImageView
    ): ImageView {
        var imageWidth = imageWidth
        var imageHeight = imageHeight
        if (extra != null) {
            imageWidth = 200.0
            imageHeight = 200.0
        } else {
            if (imageWidth > 350) {
                imageWidth = 550.0
                imageHeight = 250.0
            } else if (imageHeight > 450) {
                imageWidth = 300.0
                imageHeight = 450.0
            } else if (imageWidth < 50 && imageWidth > 20 || imageHeight < 50 && imageHeight > 20) {
                imageWidth = 200.0
                imageHeight = 300.0
            } else if (imageWidth < 20 || imageHeight < 20) {
                imageWidth = 100.0
                imageHeight = 150.0
            } else {
                imageWidth = 300.0
                imageHeight = 450.0
            }
        }

        val params = imageView.layoutParams
        params.width = imageWidth.toInt()
        params.height = imageHeight.toInt()
        imageView.layoutParams = params

        return imageView
    }

    private fun createImageOptions(): DisplayImageOptions {
        return DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build()
    }

    fun setAudioPlayByEarPhone(state: Int) {
        val audioManager = mContext
            .getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
        audioManager.mode = AudioManager.MODE_IN_CALL
        if (state == 0) {
            mIsEarPhoneOn = false
            audioManager.isSpeakerphoneOn = true
            audioManager.setStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                AudioManager.STREAM_VOICE_CALL
            )
        } else {
            mIsEarPhoneOn = true
            audioManager.isSpeakerphoneOn = false
            audioManager.setStreamVolume(
                AudioManager.STREAM_VOICE_CALL, currVolume,
                AudioManager.STREAM_VOICE_CALL
            )
        }
    }

    fun releaseMediaPlayer() {
        mp?.release()
    }

    fun initMediaPlayer() {
        mp.reset()
    }

    fun stopMediaPlayer() {
        if (mp.isPlaying)
            mp.stop()
    }

    private fun pauseVoice(msgDirect: MessageDirect, voice: ImageView) {
        if (msgDirect == MessageDirect.send) {
            voice.setImageResource(R.drawable.send_3)
        } else {
            voice.setImageResource(R.drawable.jmui_receive_3)
        }
        mp.pause()
        mSetData = true
    }


    private fun addToListAndSort(position: Int) {
        mIndexList.add(position)
        Collections.sort(mIndexList)
    }

    private fun browseDocument(fileName: String, path: String) {
        try {
            val ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()
            val mimeTypeMap = MimeTypeMap.getSingleton()
            val mime = mimeTypeMap.getMimeTypeFromExtension(ext)
            val file = File(path)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(file), mime)
            mContext.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(mContext, R.string.file_not_support_hint, Toast.LENGTH_SHORT).show()
        }

    }

}
