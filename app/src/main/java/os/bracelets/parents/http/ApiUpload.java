package os.bracelets.parents.http;


import android.util.Base64;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.utils.FileUtils;

/**
 * 文件上传的类
 * Created by lishiyou on 2017/6/30.
 */

public class ApiUpload {

    public static final String URL = "sys/filel/upload";

    public static final ApiUpload apiUpload = new ApiUpload();

    public static ApiUpload getInstance() {
        return apiUpload;
    }

    public ApiUpload() {
    }

    /**
     * @param imageType 图片类型
     * @param filePath  图片本地路径
     * @param callback  回调
     */
    public void uploadFile(String imageType, String filePath, Callback callback) {
        File file = new File(filePath);
        String fileName = file.getName();
//        RequestBody fileBody = RequestBody.create(MediaType.parse("app/octet-stream"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("fileName", fileName)
                .addFormDataPart("fileType", imageType)
                .addFormDataPart("fileData", FileUtils.file2Base64(filePath))
                .build();
        Request request = new Request.Builder()
                .url(MyApplication.getInstance().getServerUrl() + URL)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newBuilder().connectTimeout(20, TimeUnit.SECONDS);
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
