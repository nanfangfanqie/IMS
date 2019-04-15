package ims.chat.entity

import java.util.Date

/**
 * @author yangchen
 * on 3/15/2019 11:59 PM
 */
class Friend(
    /**
     * 好友昵称
     */
    var nickName: String?
) {
    /**
     * 好友Id
     */
    var friendId: String? = null
    /**
     * 添加时间
     */
    var addTime: Date? = null
    /**
     * 备注
     */
    var remark: String? = null
}
