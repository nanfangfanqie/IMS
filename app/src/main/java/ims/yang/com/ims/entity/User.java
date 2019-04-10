package ims.yang.com.ims.entity;

import java.io.Serializable;
import java.util.Date;

/** 用户
 * @author yangchen
 * on 2019/4/7 16:02
 */
public class User implements Serializable {
    /**
     * 主键
     */
    private String pkId;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号
     */
    private String telphone;
    /**
     * 生日
     */
    private Date birthDay;
    /**
     * 个性签名
     */
    private String personalSign;

    public String getPkId() {
        return pkId;
    }

    public void setPkId(String pkId) {
        this.pkId = pkId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public String getPersonalSign() {
        return personalSign;
    }

    public void setPersonalSign(String personalSign) {
        this.personalSign = personalSign;
    }

}
