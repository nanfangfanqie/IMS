package ims.chat.listener

import ims.chat.vo.MessageResult
import java.lang.Exception

/**监听
 * @author yangchen
 * on 2019/4/7 22:44
 */
interface Listener<T> {

    fun onSuccess() {
        throw Exception("必须实现自己的方法")
    }

    fun onSuccess(t: T) {
        throw Exception("必须实现自己的方法")
    }

    fun onPreExecute(){}

    fun onError(messageResult: MessageResult<*>){
        throw Exception("必须实现自己的方法")
    }

}
