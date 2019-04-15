package ims.chat.vo;

import java.io.Serializable;

/**
 * @author yangchen
 * on 2019/4/7 16:10
 */
public class MessageResult<T> implements Serializable{
    private boolean status;
    private T data;

    public MessageResult(boolean status) {
        this.status = status;
    }

    public MessageResult(boolean status, T data) {
        this.status = status;
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
