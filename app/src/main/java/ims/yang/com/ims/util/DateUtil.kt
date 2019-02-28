package ims.yang.com.ims.util

import android.nfc.Tag
import android.util.Log

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * 时间工具
 * @author yangchen
 * on 2019/3/1 2:15
 */
object DateUtil {

    private const val TAG = "DateUtil"

    /**
     * 时间转字符串
     * @param format 格式
     * @param date 时间
     * @return
     */
    fun dateToStr(format: String, date: Date): String {
        return SimpleDateFormat(format).format(date)
    }

    /**
     * 字符串转时间
     * @param format 格式
     * @param text 时间字符串
     * @return
     */
    fun strToDate(format: String, text: String): Date? {
        val sdf = SimpleDateFormat(format)
        lateinit var date: Date
        try {
            date = sdf.parse(text)
        } catch (e: ParseException) {
            Log.d(TAG, "strToDate: 字符串解析异常")
            e.printStackTrace()
        }
        return date
    }

}
