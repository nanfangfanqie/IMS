package ims.chat.utils

import android.content.Context
import android.support.v4.content.ContextCompat

/**
 * 资源工具
 * @author yangchen
 * on 2019/3/1 3:28
 */
object ResourceUtil {

    fun getString(context: Context, id: Int): String {
        return context.resources.getString(id)
    }

    fun getColor(context: Context, id: Int): Int {
        return ContextCompat.getColor(context, id)
    }

}
