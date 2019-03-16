package os.bracelets.parents.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 联系人数据模型
 * Created by lishiyou on 2019/3/4.
 */

public class ContactBean implements Serializable {
    //    头像
    private String profile;
    //            昵称
    private String nickName;
    //    手机号码
    private String phone;
    //            性别
    private int sex;
    //    性别描述0 未知 1 男 2 女
    private String sexDesc;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSexDesc() {
        return sexDesc;
    }

    public void setSexDesc(String sexDesc) {
        this.sexDesc = sexDesc;
    }

    public static ContactBean parseBean(JSONObject object) {
        ContactBean contact = new ContactBean();
        contact.setProfile(object.optString("profile", ""));
        contact.setNickName(object.optString("nickName", ""));
        contact.setPhone(object.optString("phone", ""));
        contact.setSex(object.optInt("sex", 0));
        contact.setSexDesc(object.optString("sexDesc", ""));
        return contact;
    }
}
