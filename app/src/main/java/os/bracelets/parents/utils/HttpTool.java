//package os.bracelets.parents.utils;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.google.gson.Gson;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.FormBody;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
///**
// * Created by zhouyezi on 16/6/25.
// */
//public abstract class HttpTool {
//
//    OkHttpClient client = new OkHttpClient();
//
//    private ResultState resultState = null;
//
//    private Context mConext;
//
//    private Gson gson;
//
//    public HttpTool(Context context) {
//        this.mConext = context;
//    }
//
//    public void get(String url, Map<String, String> params, Map<String, String> heads) {
//        //处理不需要发送请求来决定界面的情况
//        resultState = new ResultState();
//        StringBuffer urlGet = new StringBuffer();
//        urlGet.append(url);
//        if (TextUtils.isEmpty(url)) {
//            resultState.setContent("");
//        } else {
//            if (params!=null) {
//                int i = 0;
//                for (Map.Entry<String, String> entry : params.entrySet()) {
//                    if (i == 0) {
//                        urlGet.append("?" + entry.getKey() + "=" + entry.getValue());
//                    } else {
//                        urlGet.append("&" + entry.getKey() + "=" + entry.getValue());
//                    }
//                    i++;
//                }
//            }
//
//            Request.Builder builder = new Request.Builder();
//            for (Map.Entry<String, String> head : heads.entrySet()) {
//                builder.addHeader(head.getKey(), head.getValue());
//            }
//            builder.url(urlGet.toString());
//            Request request = builder.build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    String content = null;
//                    resultState.setContent(e.toString());
//                    e.printStackTrace();
//                    if (TextUtils.isEmpty(content)) {
//                        resultState.setContent("");
//                    } else {
//                        resultState.setContent(content);
//                    }
//                    OnFailure(resultState);
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    final String body = response.body().string();
//                    if (response.isSuccessful()) {
//                        resultState.setContent(body);
//                        OnSuccess(resultState);
//                        Log.i("okhttp_get", "成功" + body);
//                    } else {
//                        resultState.setContent(body);
//                        OnFailure(resultState);
//                        Log.i("okhttp_get", "失败：" + body);
//                    }
//                }
//            });
//        }
//    }
//
//    public void post(String url, Map<String, String> params) {
//        //处理不需要发送请求来决定界面的情况
//        resultState = new ResultState();
//        RequestBody requestBody = new FormBody.Builder().build();
//        if (TextUtils.isEmpty(url)) {
//            resultState.setContent("");
//        } else {
//            if (!params.isEmpty()) {
//                FormBody.Builder formBody = new FormBody.Builder();
//                for (Map.Entry<String, String> entry : params.entrySet()) {
//                    formBody.add(entry.getKey(), entry.getValue());
//                }
//                requestBody = formBody.build();
//            }
//
//            Request request = new Request.Builder()
////                    .addHeader("token", "asdlfjkasdljfaskdjfalsjkljalk")  //请求头中增加參数
//                    .url(url) //携带參数
//                    .post(requestBody)
//                    .build();
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    String content = null;
//                    resultState.setContent(e.toString());
//                    e.printStackTrace();
//                    if (TextUtils.isEmpty(content)) {
//                        resultState.setContent("");
//                    } else {
//                        resultState.setContent(content);
//                    }
//                    OnFailure(resultState);
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    final String body = response.body().string();
//                    if (response.isSuccessful()) {
//                        resultState.setContent(response.message());
//                        OnSuccess(resultState);
//                        Log.i("okhttp_post", "成功" + body);
//                    } else {
//                        resultState.setContent(body);
//                        OnFailure(resultState);
//                        Log.i("okhttp_post", "失败：" + body);
//                    }
//                }
//            });
//        }
//    }
//
//    public void post(String url, Map<String, Object> params, Map<String, String> heads) {
//        gson = new Gson();
//        String body = gson.toJson(params);
//
//        //处理不需要发送请求来决定界面的情况
//        resultState = new ResultState();
//        if (TextUtils.isEmpty(url)) {
//            resultState.setContent("");
//        } else {
//            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),body);
//
//            Request.Builder builder = new Request.Builder();
//            for (Map.Entry<String, String> head : heads.entrySet()) {
//                builder.addHeader(head.getKey(), head.getValue());
//            }
//            builder.url(url);
//            builder.post(requestBody);
//            Request request = builder.build();
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    String content = null;
//                    resultState.setContent(e.toString());
//                    e.printStackTrace();
//                    if (TextUtils.isEmpty(content)) {
//                        resultState.setContent("");
//                    } else {
//                        resultState.setContent(content);
//                    }
//                    OnFailure(resultState);
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    final String body = response.body().string();
//                    if (response.isSuccessful()) {
//                        resultState.setContent(body);
//                        OnSuccess(resultState);
//                        Log.i("okhttp_put", "成功" + body);
//                    } else {
//                        resultState.setContent(body);
//                        OnFailure(resultState);
//                        Log.i("okhttp_put", "失败：" + body);
//                    }
//                }
//            });
//        }
//    }
//
//    public void post(String uploadUrl, List<File> files, Map<String, String> params, Map<String, String> heads) throws IOException {
//        resultState = new ResultState();
//        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM);
//        if(files!=null){
//            for(int i=0;i<files.size();i++){
//                multipartBuilder.addFormDataPart("file"+(i+1), files.get(i).getName(), RequestBody.create(null, files.get(i)));
//            }
//        }
//        if (params!=null) {
//            for (Map.Entry<String, String> entry : params.entrySet()) {
//                multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
//            }
//        }
//        MultipartBody multipartBody = multipartBuilder.build();
//
//        Request.Builder builder = new Request.Builder();
//        for (Map.Entry<String, String> head : heads.entrySet()) {
//            builder.addHeader(head.getKey(), head.getValue());
//        }
//
//        builder.url(uploadUrl);
//        builder.post(multipartBody);
//        Request request = builder.build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                String content = null;
//                resultState.setContent(e.toString());
//                e.printStackTrace();
//                if (TextUtils.isEmpty(content)) {
//                    resultState.setContent("");
//                } else {
//                    resultState.setContent(content);
//                }
//                OnFailure(resultState);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String body = response.body().string();
//                if (response.isSuccessful()) {
//                    resultState.setContent(body);
//                    OnSuccess(resultState);
//                    Log.i("okhttp_post", "成功" + body);
//                } else {
//                    resultState.setContent(body);
//                    OnFailure(resultState);
//                    Log.i("okhttp_post", "失败：" + body);
//                }
//            }
//        });
//    }
//
//    public void post(String uploadUrl, String fileName, List<File> files, Map<String, String> params, Map<String, String> heads) throws IOException {
//        resultState = new ResultState();
//        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM);
//        if(files!=null){
//            if(files.size()==1){
//                multipartBuilder.addFormDataPart(fileName, files.get(0).getName(), RequestBody.create(null, files.get(0)));
//            }else {
//                for(int i=0;i<files.size();i++){
//                    multipartBuilder.addFormDataPart(fileName+(i+1), files.get(i).getName(), RequestBody.create(null, files.get(i)));
//                }
//
//            }
//        }
//        if (params!=null) {
//            for (Map.Entry<String, String> entry : params.entrySet()) {
//                multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
//            }
//        }
//        MultipartBody multipartBody = multipartBuilder.build();
//
//        Request.Builder builder = new Request.Builder();
//        for (Map.Entry<String, String> head : heads.entrySet()) {
//            builder.addHeader(head.getKey(), head.getValue());
//        }
//
//        builder.url(uploadUrl);
//        builder.post(multipartBody);
//        Request request = builder.build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                String content = null;
//                resultState.setContent(e.toString());
//                e.printStackTrace();
//                if (TextUtils.isEmpty(content)) {
//                    resultState.setContent("");
//                } else {
//                    resultState.setContent(content);
//                }
//                OnFailure(resultState);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String body = response.body().string();
//                if (response.isSuccessful()) {
//                    resultState.setContent(body);
//                    OnSuccess(resultState);
//                    Log.i("okhttp_post", "成功" + body);
//                } else {
//                    resultState.setContent(body);
//                    OnFailure(resultState);
//                    Log.i("okhttp_post", "失败：" + body);
//                }
//            }
//        });
//    }
//
//    public void put(String url, Map<String, String> params) {
//        //处理不需要发送请求来决定界面的情况
//        resultState = new ResultState();
//        RequestBody requestBody = new FormBody.Builder().build();
//        if (TextUtils.isEmpty(url)) {
//            resultState.setContent("");
//        } else {
//            if (!params.isEmpty()) {
//                FormBody.Builder formBody = new FormBody.Builder();
//                for (Map.Entry<String, String> entry : params.entrySet()) {
//                    formBody.add(entry.getKey(), entry.getValue());
//                }
//                requestBody = formBody.build();
//            }
//
//            Request request = new Request.Builder()
////                    .addHeader("token", "asdlfjkasdljfaskdjfalsjkljalk")  //请求头中增加參数
//                    .url(url) //携带參数
//                    .put(requestBody)
//                    .build();
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    String content = null;
//                    resultState.setContent(e.toString());
//                    e.printStackTrace();
//                    if (TextUtils.isEmpty(content)) {
//                        resultState.setContent("");
//                    } else {
//                        resultState.setContent(content);
//                    }
//                    OnFailure(resultState);
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    final String body = response.body().string();
//                    if (response.isSuccessful()) {
//                        resultState.setContent(response.message());
//                        OnSuccess(resultState);
//                        Log.i("okhttp_put", "成功" + body);
//                    } else {
//                        resultState.setContent(body);
//                        OnFailure(resultState);
//                        Log.i("okhttp_put", "失败：" + body);
//                    }
//                }
//            });
//        }
//    }
//
//    public void put(String url, String body, Map<String, String> heads) {
//        //处理不需要发送请求来决定界面的情况
//        resultState = new ResultState();
////        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
//
////        RequestBody requestBody = new FormBody.Builder().build();
//        if (TextUtils.isEmpty(url)) {
//            resultState.setContent("");
//        } else {
////            if (!params.isEmpty()) {
////                FormBody.Builder formBody = new FormBody.Builder();
////                for (Map.Entry<String, String> entry : params.entrySet()) {
////                    formBody.add(entry.getKey(), entry.getValue());
////                }
////                requestBody = formBody.build();
////            }
//
////            RequestBody requestBody = new MultipartBody.Builder()
////                    .setType(MultipartBody.FORM);
//            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),body);
//
//            Request.Builder builder = new Request.Builder();
//            for (Map.Entry<String, String> head : heads.entrySet()) {
//                builder.addHeader(head.getKey(), head.getValue());
//            }
//            builder.url(url);
//            builder.put(requestBody);
//            Request request = builder.build();
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    String content = null;
//                    resultState.setContent(e.toString());
//                    e.printStackTrace();
//                    if (TextUtils.isEmpty(content)) {
//                        resultState.setContent("");
//                    } else {
//                        resultState.setContent(content);
//                    }
//                    OnFailure(resultState);
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    final String body = response.body().string();
//                    if (response.isSuccessful()) {
//                        resultState.setContent(body);
//                        OnSuccess(resultState);
//                        Log.i("okhttp_put", "成功" + body);
//                    } else {
//                        resultState.setContent(body);
//                        OnFailure(resultState);
//                        Log.i("okhttp_put", "失败：" + body);
//                    }
//                }
//            });
//        }
//    }
//
//    public void put(String url, String json) throws IOException {
//        //处理不需要发送请求来决定界面的情况
//        resultState = new ResultState();
//        if (TextUtils.isEmpty(url)) {
//            resultState.setContent("");
//        } else {
//            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
//
//            Request request = new Request.Builder()
//                    .url(url)
//                    .addHeader("Content-Type", "application/json")
//                    .put(body)
//                    .build();
//            //修改各种 Timeout
//            OkHttpClient client = new OkHttpClient.Builder()
////                    .connectTimeout(600, TimeUnit.SECONDS)
////                    .readTimeout(200, TimeUnit.SECONDS)
////                    .writeTimeout(600, TimeUnit.SECONDS)
//                    .build();
//            //如果不需要可以直接写成 OkHttpClient client = new OkHttpClient.Builder().build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    resultState.setContent(e.toString());
//                    OnFailure(resultState);
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    final String body = response.body().string();
//                    if (response.isSuccessful()) {
//                        resultState.setContent(response.message());
//                        OnSuccess(resultState);
//                        Log.i("okhttp_put", "成功" + body);
//                    } else {
//                        resultState.setContent(body);
//                        OnFailure(resultState);
//                        Log.i("okhttp_put", "失败：" + body);
//                    }
////                            runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
////                                    tvShow.setText(string);
////                                }
////                            });
//                }
//            });
//        }
//    }
//
//    public void put(String uploadUrl, List<File> files, Map<String, String> params, Map<String, String> heads) throws IOException {
//        resultState = new ResultState();
//        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM);
//        if(files!=null){
//            for(int i=0;i<files.size();i++){
//                multipartBuilder.addFormDataPart("file"+(i+1), files.get(i).getName(), RequestBody.create(null, files.get(i)));
//            }
//        }
//        if (params!=null) {
//            for (Map.Entry<String, String> entry : params.entrySet()) {
//                multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
//            }
//        }
//        MultipartBody multipartBody = multipartBuilder.build();
//
//        Request.Builder builder = new Request.Builder();
//        for (Map.Entry<String, String> head : heads.entrySet()) {
//            builder.addHeader(head.getKey(), head.getValue());
//        }
//
//        builder.url(uploadUrl);
//        builder.put(multipartBody);
//        Request request = builder.build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                String content = null;
//                resultState.setContent(e.toString());
//                e.printStackTrace();
//                if (TextUtils.isEmpty(content)) {
//                    resultState.setContent("");
//                } else {
//                    resultState.setContent(content);
//                }
//                OnFailure(resultState);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String body = response.body().string();
//                if (response.isSuccessful()) {
//                    resultState.setContent(body);
//                    OnSuccess(resultState);
//                    Log.i("okhttp_post", "成功" + body);
//                } else {
//                    resultState.setContent(body);
//                    OnFailure(resultState);
//                    Log.i("okhttp_post", "失败：" + body);
//                }
//            }
//        });
//    }
//
//
//    public void cancelAll() {
//        client.dispatcher().cancelAll();
//    }
//
//    protected abstract void OnSuccess(ResultState resultState);
//
//    protected abstract void OnFailure(ResultState resultState);
//
//    public class ResultState {
//
//        private String content;
//
//        public String getContent() {
//            return content;
//        }
//
//        public void setContent(String content) {
//            this.content = content;
//        }
//    }
//}
