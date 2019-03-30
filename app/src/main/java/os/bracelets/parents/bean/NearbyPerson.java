package os.bracelets.parents.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 附近的人数据模型
 * Created by lishiyou on 2019/2/24.
 */

public class NearbyPerson implements Serializable {

    private int accountId;
    //    头像
    private String profile;
    //            昵称
    private String nickName;
    //    年龄
    private int age;
    //            性别
    private int sex;
    //    性别描述0 未知 1 男 2 女
    private String sexDesc;

    //    距离（m）
    private int distance;
    //电话
    private String phone;
    private int height;
    private int weight;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public static NearbyPerson parseBean(JSONObject object) {
        NearbyPerson person = new NearbyPerson();
        person.setAccountId(object.optInt("accountId"));
        person.setProfile(object.optString("portrait", ""));
        person.setNickName(object.optString("nickName", ""));
        person.setAge(object.optInt("age", 0));
        person.setSex(object.optInt("sex", 0));
        person.setSexDesc(object.optString("sexDesc", ""));
        person.setDistance(object.optInt("distance", 0));
        person.setPhone(object.optString("phone", ""));
        person.setHeight(object.optInt("height", 0));
        person.setWeight(object.optInt("weight", 0));
        return person;
    }
}
