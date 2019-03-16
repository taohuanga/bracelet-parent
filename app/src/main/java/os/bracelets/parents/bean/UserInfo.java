package os.bracelets.parents.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by lishiyou on 2019/2/23.
 */

public class UserInfo implements Serializable {
    private String name;
    private String nickName;
    private String portrait;
    private String appType;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public static UserInfo parseBean(JSONObject object) {
        UserInfo info = new UserInfo();
        info.setName(object.optString("name", ""));
        info.setNickName(object.optString("nickName", ""));
        info.setPortrait(object.optString("portrait", ""));
        info.setAppType(object.optString("appType", ""));
        return info;
    }
}
