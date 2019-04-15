package ims.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import ims.chat.R;
import ims.chat.application.ImsApplication;
import ims.chat.entity.News;
import ims.chat.ui.activity.WebActivity;

import java.util.List;

/**新闻适配器
 * @author yangchen
 * on 3/15/2019 3:01 PM
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    List<News> newsList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        public ViewHolder(@NonNull View view) {
            super(view);
            textView = view.findViewById(R.id.newsTitle);
            imageView = view.findViewById(R.id.newsImg);
        }
    }

    public NewsAdapter(List<News> msgList) {
        this.newsList = msgList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final News news = newsList.get(position);
        holder.textView.setText(news.getTitle());
        String time= news.getPtime();
        if (news.getHasImg()==1){
            Glide.with(ImsApplication.getContext()).load(news.getImgsrc()).into(holder.imageView);
        }else {
            holder.imageView.setVisibility(View.GONE);
        }

        holder.textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                WebActivity.Companion.actionStart(v.getContext(),news.getUrl());
            }
        });
    }



    @Override
    public int getItemCount() {
        return newsList.size();
    }
}
