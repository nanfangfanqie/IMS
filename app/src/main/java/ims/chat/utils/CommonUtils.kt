/*
    ShengDao Android Client, CommonUtils
    Copyright (c) 2014 ShengDao Tech Company Limited
 */

package ims.chat.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Environment
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.inputmethod.InputMethodManager

import java.util.Locale


/**
 * [公共工具类，与Android API相关的辅助类]
 *
 *
 */
object CommonUtils {

    private val tag = CommonUtils::class.java.simpleName

    /** 网络类型  */
    val NETTYPE_WIFI = 0x01
    val NETTYPE_CMWAP = 0x02
    val NETTYPE_CMNET = 0x03

    /**
     * 检测网络是否可用
     *
     * @return
     */
    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo
        return ni != null && ni.isConnectedOrConnecting
    }


    /**
     * 判断SDCard是否存在,并可写
     *
     * @return
     */
    public fun checkSDCard(): Boolean {
        val flag = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == flag
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    fun getScreenWidth(context: Context): Int {
        val dm = context.resources.displayMetrics
        return dm.widthPixels
    }

    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    fun getScreenHeight(context: Context): Int {
        val dm = context.resources.displayMetrics
        return dm.heightPixels
    }

    /**
     * 获取屏幕显示信息对象
     * @param context
     * @return
     */
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        return context.resources.displayMetrics
    }

    /**
     * dp转pixel
     */
    fun dpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi / 160f)
    }

    /**
     * pixel转dp
     */
    fun pixelsToDp(px: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi / 160f)
    }


    /**
     * 隐藏软键盘
     * @param activity
     */
    fun hideKeyboard(activity: Activity?) {
        if (activity != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive) {
                imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }
    }

    /**
     * 显示软键盘
     * @param activity
     */
    fun showKeyboard(activity: Activity?) {
        if (activity != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (!imm.isActive) {
                imm.showSoftInputFromInputMethod(activity.currentFocus!!.windowToken, 0)
            }
        }
    }

    /**
     * 是否横屏
     * @param context
     * @return true为横屏，false为竖屏
     */
    fun isLandscape(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    /**
     * 判断是否是平板
     * 这个方法是从 Google I/O App for Android 的源码里找来的，非常准确。
     * @param context
     * @return
     */
    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }
}
