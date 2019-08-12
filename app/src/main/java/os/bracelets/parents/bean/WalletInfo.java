package os.bracelets.parents.bean;

import org.json.JSONObject;

import java.io.Serializable;

public class WalletInfo implements Serializable {

    private int id;

    private int accountId;

    private double balance;

    private double freezeAmount;

    private double integral;

    private double tradePassword;



    public static WalletInfo parseBean(JSONObject object){
        WalletInfo info = new WalletInfo();

        info.setAccountId(object.optInt("accountId"));
        info.setId(object.optInt("id"));
        info.setBalance(object.optDouble("balance"));
        info.setFreezeAmount(object.optDouble("freezeAmount"));
        info.setIntegral(object.optDouble("integral"));
        info.setTradePassword(object.optDouble("tradePassword"));

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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getFreezeAmount() {
        return freezeAmount;
    }

    public void setFreezeAmount(double freezeAmount) {
        this.freezeAmount = freezeAmount;
    }

    public double getIntegral() {
        return integral;
    }

    public void setIntegral(double integral) {
        this.integral = integral;
    }

    public double getTradePassword() {
        return tradePassword;
    }

    public void setTradePassword(double tradePassword) {
        this.tradePassword = tradePassword;
    }
}
