package ims.yang.com.ims.constant;

/**
 * @author yangchen
 * on 2019/4/10 0:58
 */

public enum API {
    LOGIN("/user/login"),
    REGISTER("/user/register");

    private String url;

    API(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
