package os.bracelets.parents.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by lishiyou on 2019/1/27.
 */

public class BaseInfo implements Serializable{

//      "tokenId":"c1ceb8b610234aacb130b0fae4ca44c8",
//              "realName":"",
//              "icon":"https://api.jixiancai.cn/0/post/pic/20190108/1901081042347239.jpg",
//              "userId":2,
//              "openId":""

    private String tokenId;
    private String nickName;
    private String name;
    private String realName;
    private String portrait;
    private String birthday;
    private int userId;
    private String openId;
    private String phone;
    private String bluetoothName;
    private String equipmentSn;
    private String macAddress;


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

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public String getEquipmentSn() {
        return equipmentSn;
    }

    public void setEquipmentSn(String equipmentSn) {
        this.equipmentSn = equipmentSn;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static BaseInfo parseBean(JSONObject object) {
        BaseInfo info = new BaseInfo();
        info.setTokenId(object.optString("tokenId"));
        info.setPortrait(object.optString("portrait"));
        info.setUserId(object.optInt("userId"));
        info.setNickName(object.optString("nickName"));
        info.setRealName(object.optString("realName"));
        info.setPhone(object.optString("phone"));
        info.setBirthday(object.optString("birthday"));
        info.setBluetoothName(object.optString("bluetoothName"));
        info.setEquipmentSn(object.optString("equipmentSn"));
        info.setMacAddress(object.optString("macAddress"));
        info.setName(object.optString("name"));
        return info;
    }
}
