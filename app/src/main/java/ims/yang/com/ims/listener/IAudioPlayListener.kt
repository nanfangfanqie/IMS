package ims.yang.com.ims.listener

import android.net.Uri

interface IAudioPlayListener {

    fun onStart(var1: Uri)

    fun onStop(var1: Uri)

    fun onComplete(var1: Uri)
}