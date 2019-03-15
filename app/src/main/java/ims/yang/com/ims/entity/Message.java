package ims.yang.com.ims.entity;

import java.util.Date;

/**
 * 消息实体类
 * @author yangchen
 * on 2019/3/3 1:49
 */
public class Message {

    public Message(String content) {
        this.content = content;
    }

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


    public String getSendFrom() {
        return sendFrom;
    }

    public void setSendFrom(String sendFrom) {
        this.sendFrom = sendFrom;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
}
