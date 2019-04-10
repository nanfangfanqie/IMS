package ims.yang.com.ims.util

import java.util.*

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

}
