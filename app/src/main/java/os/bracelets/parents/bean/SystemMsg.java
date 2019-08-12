package os.bracelets.parents.bean;

import com.iflytek.msc.MSC;

import org.json.JSONObject;

import java.io.Serializable;

public class SystemMsg implements Serializable {

//     "id":168,
//             "title":"积分到账提示",
//             "content":"积分到账提示，赠送30积分已到账，请注意查收！###30",
//             "noticeType":"4",
//             "date":"2018-05-22 22:43:16",
//             "bizNo":"",
//             "isRead":0,
//             "sysNotice":false

    private int id;
    private int isRead;

    private String title;
    private String content;
    private String noticeType;
    private String date;


    public static SystemMsg parseBean(JSONObject object) {
        SystemMsg msg = new SystemMsg();

        msg.setId(object.optInt("id"));
        msg.setIsRead(object.optInt("isRead"));

        msg.setTitle(object.optString("title"));
        msg.setContent(object.optString("content"));
        msg.setNoticeType(object.optString("noticeType"));
        msg.setDate(object.optString("date"));

        return msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
