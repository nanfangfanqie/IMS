package ims.chat.entity;

import java.io.Serializable;

/**
 * @author yangchen
 * on 2019/4/11 20:51
 */
public class Msg implements Serializable {

    public static final int RECEIVED = 1;
    public static final int SEND = 0;

    private String content;
    private int type;

    public Msg(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
