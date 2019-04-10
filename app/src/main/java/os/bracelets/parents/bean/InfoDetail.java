package os.bracelets.parents.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by lishiyou on 2019/3/21.
 */

public class InfoDetail implements Serializable {

//    "title":"最新资讯",
//            "imageUrl":"http://1234/fort/ajk.jpg",
//            "content":"习近平总书记主持召开中央全面依法治国委员会第二次会议并发表重要讲话。",
//            "author":"李三",
//            "createDate":"2019-03-05 01:26:02"

    private String title;
    private String imageUrl;
    private String content;
    private String author;
    private String createDate;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public static InfoDetail parseBean(JSONObject object) {
        InfoDetail detail = new InfoDetail();
        detail.setTitle(object.optString("title"));
        detail.setImageUrl(object.optString("imageUrl"));
        detail.setContent(object.optString("content"));
        detail.setAuthor(object.optString("author"));
        detail.setCreateDate(object.optString("createDate"));
        return detail;
    }
}
