package ims.chat.entity

import java.util.Date

/**
 * 消息实体类
 * @author yangchen
 * on 2019/3/3 1:49
 */
class Message(

    /**
     * 消息内容
     */
    var content: String
) {

    /**
     * 发送人
     */
    lateinit var sendFrom: String

    /**
     * 接收人
     */
    lateinit var sendTo: String

    /**
     * 消息时间
     */
    lateinit var sendTime: Date
}
