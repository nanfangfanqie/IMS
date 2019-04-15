package ims.chat.task

import android.os.AsyncTask

import com.google.gson.Gson
import ims.chat.entity.GsonNews
import ims.chat.entity.News
import ims.chat.listener.Listener

import ims.chat.vo.MessageResult
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.ArrayList


/**机器人任务
 * @author yangchen
 * on 2019/4/8 18:43
 */
class NewsTask(private val listener: Listener<List<News>>) : AsyncTask<String, Int, MessageResult<*>>() {

    override fun onPreExecute() {
        listener.onPreExecute()
    }

    override fun doInBackground(vararg params: String): MessageResult<*> {
        return try {
            val url = params[0]
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            var responseData = response.body()!!.string()
            responseData = responseData.substring(9,responseData.length-1)
            val gson = Gson()
            val GsonNews = gson.fromJson(responseData, GsonNews::class.java);
            MessageResult(true, GsonNews)
        } catch (e: Exception) {
            e.printStackTrace()
            MessageResult(false, e)
        }
    }

    override fun onPostExecute(messageResult: MessageResult<*>) {
        if (messageResult.isStatus) {
            if (null != messageResult.data) {
                val list = (messageResult.data as GsonNews).news
                listener.onSuccess(list)
            }
        } else {
            listener.onError(messageResult)
        }
    }

    fun parseJson(data: String): List<News> {
        val newsList = ArrayList<News>();
        val gson = Gson()
        val jsonArray: JSONArray = JSONArray(data)
        for (i in 0..(jsonArray.length() - 1)) {
            val jsonObject = jsonArray.getJSONObject(i);
            val news = gson.fromJson(jsonObject.toString(), News::class.java)
            newsList.add(news)
        }
        return newsList
    }


}

