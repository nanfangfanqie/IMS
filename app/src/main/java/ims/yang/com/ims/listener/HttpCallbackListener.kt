package ims.yang.com.ims.listener

/**
 * @author yangchen
 * on 2019/4/7 22:44
 */
interface HttpCallbackListener {
    /**
     * 结束的处理方法
     * @param response
     */
    fun onFinish(response: String)

    /**
     * 错误的处理方法
     * @param e
     */
    fun onError(e: Exception)
}
