package ims.chat.entity;

/**
 * @author yangchen
 * on 2019/4/13 2:38
 */
public class News {
    /**
     * liveInfo : null
     * docid : ECJSLVCP0001899N
     * source :  深圳应急管理
     * title : 深圳暴雨已致10人死亡 广东紧急部署汛期安全防范
     * priority : 131
     * hasImg : 1
     * url : http://3g.163.com/news/19/0413/01/ECJSLVCP0001899N.html
     * skipURL : http://3g.163.com/ntes/special/00340EPA/wapSpecialModule.html?sid=S1555025021880
     * specialID : S1555025021880
     * commentCount : 91
     * imgsrc3gtype : 1
     * stitle : S1555025021880
     * digest : 4月11日晚，受冷暖气流交汇影响，深圳市出现冰雹、大风、雷暴
     * skipType : special
     * imgsrc : http://cms-bucket.ws.126.net/2019/04/13/0b403419170747139c3d5ab96428f7ea.png
     * ptime : 2019-04-13 01:07:33
     */

    private Object liveInfo;
    private String docid;
    private String source;
    private String title;
    private int priority;
    private int hasImg;
    private String url;
    private String skipURL;
    private String specialID;
    private int commentCount;
    private String imgsrc3gtype;
    private String stitle;
    private String digest;
    private String skipType;
    private String imgsrc;
    private String ptime;

    public Object getLiveInfo() {
        return liveInfo;
    }

    public void setLiveInfo(Object liveInfo) {
        this.liveInfo = liveInfo;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getHasImg() {
        return hasImg;
    }

    public void setHasImg(int hasImg) {
        this.hasImg = hasImg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSkipURL() {
        return skipURL;
    }

    public void setSkipURL(String skipURL) {
        this.skipURL = skipURL;
    }

    public String getSpecialID() {
        return specialID;
    }

    public void setSpecialID(String specialID) {
        this.specialID = specialID;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getImgsrc3gtype() {
        return imgsrc3gtype;
    }

    public void setImgsrc3gtype(String imgsrc3gtype) {
        this.imgsrc3gtype = imgsrc3gtype;
    }

    public String getStitle() {
        return stitle;
    }

    public void setStitle(String stitle) {
        this.stitle = stitle;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getSkipType() {
        return skipType;
    }

    public void setSkipType(String skipType) {
        this.skipType = skipType;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }
}
