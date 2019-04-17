package ims.chat.utils

import android.text.TextUtils
import java.util.*
import java.util.regex.Pattern

/**
 * 字符串工具
 * @author yangchen
 * on 2019/3/1 3:46
 */
object StringUtil {
    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    fun isEmpty(str: String?): Boolean {
        return str == null || str.trim { it <= ' ' }.isEmpty()
    }

    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    fun isNotEmpty(str: String): Boolean {
        return !isEmpty(str)
    }

    /**
     * 生成UUID
     */
    fun getUUID():String{
        return UUID.randomUUID().toString().replace("-","");
    }

    fun isContainChinese(str:String):Boolean{
        val p =  Pattern.compile("[\u4e00-\u9fa5]")
        val m = p.matcher(str)
        if (m.find()){
            return true
        }
        return false
    }

    fun whatStartWith(str:String):Boolean{
        val p =  Pattern.compile("^([A-Za-z]|[0-9])");
        val m = p.matcher(str)
        if (m.find()){
            return true
        }
        return false
    }

    fun whatContain(str:String):Boolean{
        val p =  Pattern.compile("^[0-9a-zA-Z][a-zA-Z0-9_\\-@\\.]{3,127}$");
        val m = p.matcher(str)
        if (m.find()){
            return true
        }
        return false
    }

    fun getPercentString(percent: Float): String {
        return String.format(Locale.US, "%d%%", (percent * 100).toInt())
    }

    /**
     * 删除字符串中的空白符
     *
     * @param content
     * @return String
     */
    fun removeBlanks(content: String?): String? {
        if (content == null) {
            return null
        }
        val buff = StringBuilder()
        buff.append(content)
        for (i in buff.length - 1 downTo 0) {
            if (' ' == buff[i] || '\n' == buff[i] || '\t' == buff[i]
                || '\r' == buff[i]
            ) {
                buff.deleteCharAt(i)
            }
        }
        return buff.toString()
    }

    /**
     * 获取32位uuid
     *
     * @return
     */
    fun get32UUID(): String {
        return UUID.randomUUID().toString().replace("-".toRegex(), "")
    }

    /**
     * 生成唯一号
     *
     * @return
     */
    fun get36UUID(): String {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }

    fun makeMd5(source: String): String {
        return MD5.getStringMD5(source)
    }

    fun filterUCS4(str: String): String {
        if (TextUtils.isEmpty(str)) {
            return str
        }

        if (str.codePointCount(0, str.length) == str.length) {
            return str
        }

        val sb = StringBuilder()

        var index = 0
        while (index < str.length) {
            val codePoint = str.codePointAt(index)
            index += Character.charCount(codePoint)
            if (Character.isSupplementaryCodePoint(codePoint)) {
                continue
            }

            sb.appendCodePoint(codePoint)
        }

        return sb.toString()
    }

    /**
     * counter ASCII character as one, otherwise two
     *
     * @param str
     * @return count
     */
    fun counterChars(str: String): Int {
        // return
        if (TextUtils.isEmpty(str)) {
            return 0
        }
        var count = 0
        for (i in 0 until str.length) {
            val tmp = str[i].toInt()
            if (tmp > 0 && tmp < 127) {
                count += 1
            } else {
                count += 2
            }
        }
        return count
    }



}
