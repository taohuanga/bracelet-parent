package os.bracelets.parents.bean;

import org.json.JSONObject;

/**
 * Created by lishiyou on 2019/1/27.
 */

public class BaseInfo {

//      "tokenId":"c1ceb8b610234aacb130b0fae4ca44c8",
//              "realName":"",
//              "icon":"https://api.jixiancai.cn/0/post/pic/20190108/1901081042347239.jpg",
//              "userId":2,
//              "openId":""

    private String tokenId;
    private String nickName;
    private String realName;
    private String icon;
    private int userId;
    private String openId;


    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public static BaseInfo parseBean(JSONObject object) {
        BaseInfo info = new BaseInfo();
        info.setTokenId(object.optString("tokenId"));
        info.setIcon(object.optString("icon"));
        info.setUserId(object.optInt("userId"));
        info.setNickName(object.optString("nickName"));
        info.setRealName(object.optString("realName"));
        return info;
    }
}
