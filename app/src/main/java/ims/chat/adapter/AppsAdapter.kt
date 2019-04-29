package ims.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import cn.jpush.im.android.eventbus.EventBus
import ims.chat.R
import ims.chat.application.ImsApplication
import ims.chat.model.AppBean
import ims.chat.utils.event.ImageEvent

import java.util.ArrayList

class AppsAdapter(private val mContext: Context, data: ArrayList<AppBean>?) : BaseAdapter() {

    private val inflater: LayoutInflater
    private var mDdata = ArrayList<AppBean>()

    init {
        this.inflater = LayoutInflater.from(mContext)
        if (data != null) {
            this.mDdata = data
        }
    }

    override fun getCount(): Int {
        return mDdata.size
    }

    override fun getItem(position: Int): Any {
        return mDdata[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = inflater.inflate(R.layout.item_app, null)
            viewHolder.iv_icon = convertView!!.findViewById<View>(R.id.iv_icon) as ImageView
            viewHolder.tv_name = convertView.findViewById<View>(R.id.tv_name) as TextView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        val appBean = mDdata[position]
        viewHolder.iv_icon!!.setBackgroundResource(appBean.icon)
        viewHolder.tv_name!!.text = appBean.funcName
        convertView.setOnClickListener {
            if (appBean.funcName == "图片") {
                EventBus.getDefault().post(ImageEvent(ImsApplication.IMAGE_MESSAGE))
            } else if (appBean.funcName == "拍摄") {
                EventBus.getDefault().post(ImageEvent(ImsApplication.TAKE_PHOTO_MESSAGE))
            } else if (appBean.funcName == "位置") {
                EventBus.getDefault().post(ImageEvent(ImsApplication.TAKE_LOCATION))
            } else if (appBean.funcName == "文件") {
                EventBus.getDefault().post(ImageEvent(ImsApplication.FILE_MESSAGE))
            } else if (appBean.funcName == "视频") {
                EventBus.getDefault().post(ImageEvent(ImsApplication.TACK_VIDEO))
            } else if (appBean.funcName == "语音") {
                EventBus.getDefault().post(ImageEvent(ImsApplication.TACK_VOICE))
            } else if (appBean.funcName == "名片") {
                EventBus.getDefault().post(ImageEvent(ImsApplication.BUSINESS_CARD))
            }
        }
        return convertView
    }

    internal inner class ViewHolder {
        var iv_icon: ImageView? = null
        var tv_name: TextView? = null
    }
}