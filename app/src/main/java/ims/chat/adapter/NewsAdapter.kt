package ims.chat.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import ims.chat.R
import ims.chat.application.ImsApplication
import ims.chat.entity.News
import ims.chat.ui.activity.WebActivity

/**新闻适配器
 * @author yangchen
 * on 3/15/2019 3:01 PM
 */
class NewsAdapter(internal var newsList: List<News>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: TextView
        var imageView: ImageView

        init {
            textView = view.findViewById(R.id.newsTitle)
            imageView = view.findViewById(R.id.newsImg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = newsList[position]
        holder.textView.text = news.title
        val time = news.ptime
        if (news.hasImg == 1) {
            Glide.with(ImsApplication.getContext()).load(news.imgsrc).into(holder.imageView)
        } else {
            holder.imageView.visibility = View.GONE
        }

        holder.textView.setOnClickListener { v -> WebActivity.actionStart(v.context, news.url) }
    }


    override fun getItemCount(): Int {
        return newsList.size
    }
}
