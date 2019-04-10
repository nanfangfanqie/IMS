package ims.yang.com.ims.listener

import ims.yang.com.ims.entity.User
import ims.yang.com.ims.vo.MessageResult

/**
 * @author yangchen
 * on 2019/4/7 22:44
 */
interface UserListener {

    fun onSuccess(user: User)

    fun onPreExecute()

    fun onError(messageResult: MessageResult<*>)
}
