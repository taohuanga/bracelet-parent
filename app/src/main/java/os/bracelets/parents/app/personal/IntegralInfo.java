package os.bracelets.parents.app.personal;

import org.json.JSONObject;

import java.io.Serializable;

public class IntegralInfo implements Serializable {

//     "id":2,
//             "accountId":228,
//             "changeType":0,
//             "integral":10,
//             "balance":10,
//             "createDate":"2019-05-15 02:09:08",
//             "remark":""


    private int id;
    private int accountId;
    private int changeType;
    private int integral;
    private int balance;
    private String createDate;
    private String remark;


    public static IntegralInfo parseBean(JSONObject object) {
        IntegralInfo info = new IntegralInfo();
        info.setId(object.optInt("id"));
        info.setAccountId(object.optInt("accountId"));
        info.setChangeType(object.optInt("changeType"));
        info.setIntegral(object.optInt("integral"));
        info.setBalance(object.optInt("balance"));
        info.setCreateDate(object.optString("createDate"));
        info.setRemark(object.optString("remark"));
        return info;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getChangeType() {
        return changeType;
    }

    public void setChangeType(int changeType) {
        this.changeType = changeType;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
