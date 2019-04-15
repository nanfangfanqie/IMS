package ims.chat.entity

import java.io.Serializable
import java.util.Date

/** 用户
 * @author yangchen
 * on 2019/4/7 16:02
 */
class User : Serializable {
    /**
     * 主键
     */
    var pkId: String? = null
    /**
     * 昵称
     */
    var nickName: String? = null
    /**
     * 密码
     */
    var password: String? = null
    /**
     * 手机号
     */
    var telphone: String? = null
    /**
     * 生日
     */
    var birthDay: Date? = null
    /**
     * 个性签名
     */
    var personalSign: String? = null

}
