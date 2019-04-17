package ims.chat.utils


import java.util.TreeMap

/**
 * @author yangchen
 * on 2019/4/8 23:11
 */
object UrlUtil {
    /**
     * 对象转Url
     * @param obj
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun parseURLPair(obj: Any): String {
        val c = obj.javaClass
        val fields = c.declaredFields
        val map = TreeMap<String, Any>()
        for (field in fields) {
            field.isAccessible = true
            val name = field.name
            val value = field.get(obj)
            if (value != null)
                map[name] = value
        }
        val set = map.entries
        val it = set.iterator()
        val sb = StringBuffer()
        sb.append("?")
        while (it.hasNext()) {
            val e = it.next()
            sb.append(e.key).append("=").append(e.value).append("&")
        }
        return sb.deleteCharAt(sb.length - 1).toString()
    }
}
