package ims.yang.com.ims.task

import android.os.AsyncTask

import com.google.gson.Gson

import ims.yang.com.ims.entity.User
import ims.yang.com.ims.listener.UserListener
import ims.yang.com.ims.vo.MessageResult
import okhttp3.OkHttpClient
import okhttp3.Request


/**登录任务
 * @author yangchen
 * on 2019/4/8 18:43
 */
class UserTask(private val listener: UserListener) : AsyncTask<String, Int, MessageResult<*>>() {

    override fun onPreExecute() {
        listener.onPreExecute()
    }

    override fun doInBackground(vararg params: String): MessageResult<*> {
        return try {
            val url = params[0]
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val responseData = response.body()!!.string()
            val gson = Gson()
            val messageResult = gson.fromJson(responseData, MessageResult::class.java);
            messageResult!!
        } catch (e: Exception) {
            e.printStackTrace()
            MessageResult(false, e)
        }
    }

    override fun onPostExecute(messageResult: MessageResult<*>) {
        if (messageResult.isStatus) {
            val gson = Gson()
            val user = gson.fromJson(messageResult.data!!.toString(), User::class.java)
            listener.onSuccess(user)
        } else {
            listener.onError(messageResult)
        }
    }
}

