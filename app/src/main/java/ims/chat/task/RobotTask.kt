package ims.chat.task

import android.os.AsyncTask

import com.google.gson.Gson
import ims.chat.entity.Msg
import ims.chat.entity.RobotResponse
import ims.chat.listener.Listener

import ims.chat.vo.MessageResult
import okhttp3.OkHttpClient
import okhttp3.Request


/**机器人任务
 * @author yangchen
 * on 2019/4/8 18:43
 */
class RobotTask(private val listener: Listener<Msg>) : AsyncTask<String, Int, MessageResult<*>>() {

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
            val robotResponse = gson.fromJson(responseData, RobotResponse::class.java);
            if (robotResponse.code.equals("40004")){
                robotResponse.text = """我需要休息啦~~~///(^v^)\\\~~~"""
            }
            MessageResult(true, robotResponse.text.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            MessageResult(false, e)
        }
    }

    override fun onPostExecute(messageResult: MessageResult<*>) {
        if (messageResult.isStatus) {
                listener.onSuccess(Msg(messageResult.data.toString(), Msg.RECEIVED))
        } else {
            listener.onError(messageResult)
        }
    }
}

