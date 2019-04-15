package ims.chat.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yangchen
 * on 2019/4/12 22:30
 */
public class GsonNews {



    @SerializedName("BBM54PGAwangning")
    private List<News> news;

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

}
