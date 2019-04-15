package ims.chat.listener

/**
 * @author yangchen
 * on 2019/4/7 22:44
 */
interface DownLoadListener {

    fun onProgress(progress: Int)

    fun onSuccess()

    fun onFailed()

    fun onPaused()

    fun onCanceled()
}
