package os.bracelets.parents.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 健康资讯数据模型
 * Created by lishiyou on 2019/2/24.
 */

public class HealthInfo implements Serializable {

    private String title;
    private String informationId;
    private String imageUrl;
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

    public String getCreateDate() {
        return createDate;
    }

    public String getInformationId() {
        return informationId;
    }

    public void setInformationId(String informationId) {
        this.informationId = informationId;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public static HealthInfo parseBean(JSONObject object) {
        HealthInfo info = new HealthInfo();
        info.setTitle(object.optString("title"));
        info.setImageUrl(object.optString("imageUrl"));
        info.setCreateDate(object.optString("createDate"));
        info.setInformationId(object.optString("informationId"));
        return info;
    }
}
