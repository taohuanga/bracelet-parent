package os.bracelets.parents.bean;

import org.json.JSONObject;

/**天气数据
 * Created by lishiyou on 2019/2/28.
 */

public class WeatherInfo {

    private String province;

    private String city;

    private String adcode;

    private String weather;

    private String temperature;

    private String winddirection;

    private String windpower;

    private String humidity;


    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWinddirection() {
        return winddirection;
    }

    public void setWinddirection(String winddirection) {
        this.winddirection = winddirection;
    }

    public String getWindpower() {
        return windpower;
    }

    public void setWindpower(String windpower) {
        this.windpower = windpower;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }



    public static WeatherInfo parseBean(JSONObject object){
        WeatherInfo info = new WeatherInfo();
        info.setProvince(object.optString("province",""));
        info.setCity(object.optString("city",""));
        info.setAdcode(object.optString("adcode",""));
        info.setWeather(object.optString("weather",""));
        info.setTemperature(object.optString("temperature",""));
        info.setWinddirection(object.optString("winddirection",""));
        info.setWindpower(object.optString("windpower",""));
        info.setHumidity(object.optString("humidity",""));
        return info;
    }
}
