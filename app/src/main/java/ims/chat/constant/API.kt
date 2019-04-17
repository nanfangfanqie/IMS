package ims.chat.constant

/**
 * @author yangchen
 * on 2019/4/10 0:58
 */

enum class API(val url: String) {
    TURNING("http://www.tuling123.com/openapi/api?"),
    LOGIN("/user/login"),
    REGISTER("/user/register"),
    JMKEY("561589b93039fc4d77ebd73d")
}
