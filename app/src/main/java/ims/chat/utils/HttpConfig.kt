package ims.chat.utils

/**
 * @author yangchen
 * on 2019/4/8 17:26
 */
object HttpConfig {
    const val SCHEMA = "http://"
    var ip = "192.168.20.1"
    var port = "80"
    val url: String
        get() = "$SCHEMA$ip:$port"
}
