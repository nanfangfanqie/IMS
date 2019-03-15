package ims.yang.com.ims.entity;

import java.util.Date;

/**
 * 用户实体类
 * @author yangchen
 * on 2019/3/1 2:14
 */
public class User {
    /**
     * 主键
     */
    private String pkid;
    /**
     * 身份证号码
     */
    private String id;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 年龄
     */
    private String age;
    /**
     * 姓名
     */
    private String name;
    /**
     * 地址
     */
    private String address;
    /**
     * 出生日期
     */
    private Date birthday;
    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 个性签名
     */
    private String personalSign;

    public User(String nickName) {
        this.nickName = nickName;
    }

    public String getPkid() {
        return pkid;
    }

    public void setPkid(String pkid) {
        this.pkid = pkid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPersonalSign() {
        return personalSign;
    }

    public void setPersonalSign(String personalSign) {
        this.personalSign = personalSign;
    }
}
