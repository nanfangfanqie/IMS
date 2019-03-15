package ims.yang.com.ims.entity;

import java.util.Date;

/**
 * @author yangchen
 * on 3/15/2019 11:59 PM
 */
public class Friend {
    /**
     * 好友Id
     */
    private String friendId;

    /**
     * 好友昵称
     */
    private String nickName;
    /**
     * 添加时间
     */
    private Date addTime;
    /**
     * 备注
     */
    private String remark;

    public Friend(String nickName) {
        this.nickName = nickName;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
