package ims.chat.utils

/**网络配置
 * @author yangchen
 * on 2019/4/8 17:26
 */
object HttpConfig {
    const val SCHEMA = "http://"
    var ip = "10.0.55.18"
    var port = "8080"
    val url: String
        get() = "$SCHEMA$ip:$port"
}
