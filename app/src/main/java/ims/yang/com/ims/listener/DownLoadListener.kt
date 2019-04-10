package ims.yang.com.ims.listener

import ims.yang.com.ims.vo.MessageResult

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
