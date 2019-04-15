@file:Suppress("DEPRECATION")

package ims.chat.ui.activity

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MotionEvent
import ims.chat.R
import ims.chat.adapter.NewsAdapter
import ims.chat.entity.News
import ims.chat.listener.Listener
import ims.chat.task.NewsTask
import ims.chat.vo.MessageResult
import kotlinx.android.synthetic.main.news_activity.*
import kotlinx.android.synthetic.main.toolbar.*

@Suppress("DEPRECATION")
class NewsActivity : AppCompatActivity() {
    private var flag = true;
    private lateinit var alert:ProgressDialog
    private var page = 0
    private val newsList =  ArrayList<News>()
    private lateinit var newsTypeAdapter: NewsAdapter
    private val listener = object : Listener<List<News>>{

        override fun onPreExecute() {
            if (flag){
                alert  = ProgressDialog(this@NewsActivity)
                alert.setTitle("Tips:")
                alert.setMessage("正在疯狂加载新闻...")
                alert.setCancelable(false)
                alert.show()
            }

        }

        override fun onSuccess(t: List<News>) {
            if(alert.isShowing){
                alert.dismiss()
            }
            for (i in t.listIterator()){
                newsList.add(i)
            }
            newsTypeAdapter.notifyDataSetChanged()
        }
        override fun onError(messageResult: MessageResult<*>) {
            if(alert.isShowing){
                alert.dismiss()
            }
        }
    }

    fun init(){
        toolbar.title = "偷来的新闻！！"
        var linearLayoutManager = LinearLayoutManager(this)
        rvNewsList.layoutManager = linearLayoutManager
        //查询新闻
        loadNews()
        newsTypeAdapter = NewsAdapter(newsList);
        rvNewsList.adapter = newsTypeAdapter
        swpNews.setOnRefreshListener {
            flag = true;
            //清空
            newsList.clear()
            page = 0;
            //加载新闻
            loadNews()
            swpNews.isRefreshing = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_activity)
        init()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (!rvNewsList.canScrollVertically(1)){
            flag = false
            loadNews()
        }
        return super.dispatchTouchEvent(ev)
    }


    private fun loadNews(){
        val api = "https://3g.163.com/touch/reconstruct/article/list/BBM54PGAwangning/$page-20.html"
        NewsTask(listener).execute(api)
        page += 20
    }

}
