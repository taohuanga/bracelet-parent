package os.bracelets.parents.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by lishiyou on 2019/3/5.
 */
@DatabaseTable(tableName = "t_remind")
public class RemindBean implements Serializable {
    //提醒ID
    @DatabaseField(id = true, dataType = DataType.INTEGER)
    private int remindId;
    //提醒时间
    @DatabaseField(dataType = DataType.STRING)
    private String remindTime;
    //提醒标题
    @DatabaseField(dataType = DataType.STRING)
    private String remindTitle;
    //提醒内容
    @DatabaseField(dataType = DataType.STRING)
    private String remind;
    @DatabaseField(dataType = DataType.BOOLEAN)
    private boolean isStale = false;

    public int getRemindId() {
        return remindId;
    }

    public void setRemindId(int remindId) {
        this.remindId = remindId;
    }

    public String getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }

    public String getRemindTitle() {
        return remindTitle;
    }

    public void setRemindTitle(String remindTitle) {
        this.remindTitle = remindTitle;
    }

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }

    public static RemindBean parseBean(JSONObject object) {
        RemindBean remind = new RemindBean();
        remind.setRemindId(object.optInt("remindId"));
        remind.setRemind(object.optString("remind"));
        remind.setRemindTime(object.optString("remindTime"));
        remind.setRemindTitle(object.optString("remindTitle"));
        return remind;
    }
}
