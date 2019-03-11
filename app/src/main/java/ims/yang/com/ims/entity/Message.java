package ims.yang.com.ims.entity;

import java.util.Date;

/**
 * 消息实体类
 * @author yangchen
 * on 2019/3/3 1:49
 */
public class Message {

    /**
     * 发送人
     */
    public String sendFrom;

    /**
     * 接收人
     */
    public String sendTo;

    /**
     * 消息内容
     */
    public String content;

    /**
     * 消息时间
     */
    public Date sendTime;


}
