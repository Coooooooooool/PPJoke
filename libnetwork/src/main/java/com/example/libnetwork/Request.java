package com.example.libnetwork;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.arch.core.executor.ArchTaskExecutor;

import com.example.libnetwork.cache.CacheManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class Request<T,R extends Request> implements Cloneable{

    protected String url;
    protected HashMap<String,String> headers = new HashMap<>();
    protected HashMap<String,Object> params = new HashMap<>();

    //仅仅访问本地缓存，即便本地没有缓存，也不会发起网络请求
    public static final int CACHE_ONLY = 1;
    //先访问缓存，同时发起网络请求，成功后缓存到本地
    public static final int CACHE_FIRST = 2;
    //仅仅访问服务器，不做任何存储
    public static final int NET_ONLY = 3;
    //先访问网络，成功后缓存到本地
    public static final int NET_CACHE = 4;

    private String cacheKey;
    private Type type;
    private Class claz;
    private int cacheStrategy;

    @IntDef({CACHE_ONLY,CACHE_FIRST,NET_ONLY,NET_CACHE})
    public @interface CacheStrategy{

    }

    public Request(String url){
        this.url = url;
    }

    public R addHeader(String key,String value){
        headers.put(key,value);
        return (R) this;
    }

    public R addParams(String key,Object value){
        try {
            Field field = value.getClass().getField("TYPE");
            Class claz = (Class) field.get(null);
            if(claz.isPrimitive()){
                params.put(key,value);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (R) this;
    }

    public R cacheStrategy(@CacheStrategy int cacheStrategy){
        this.cacheStrategy = cacheStrategy;
        return (R) this;
    }


    public R cacheKey(String key){
        this.cacheKey = key;
        return (R) this;
    }

    public ApiResponse<T> execute() {
        if (type == null) {
            throw new RuntimeException("同步方法,response 返回值 类型必须设置");
        }

        if (cacheStrategy == CACHE_ONLY) {
            return readCache();
        }

        if (cacheStrategy != CACHE_ONLY) {
            ApiResponse<T> result = null;
            try {
                Response response = getCall().execute();
                result = parseResponse(response, null);
            } catch (IOException e) {
                e.printStackTrace();
                if (result == null) {
                    result = new ApiResponse<>();
                    result.message = e.getMessage();
                }
            }
            return result;
        }
        return null;
    }

    public void excute(final JsonCallback<T> callback){
        if(cacheStrategy != NET_ONLY){
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    ApiResponse<T> apiResponse = readCache();
                    if(callback != null){
                        callback.onCacheSuccess(apiResponse);
                    }
                }
            });
        }
        if(cacheStrategy != CACHE_ONLY){
            getCall().enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ApiResponse<T> response = new ApiResponse<>();
                    response.message = e.getMessage();
                    callback.onError(response);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ApiResponse<T> apiResponse  = parseResponse(response,callback);
                    if(apiResponse.success)
                        callback.onError(apiResponse);
                    else
                        callback.onSuccess(apiResponse);
                }
            });
        }
    }

    private ApiResponse<T> readCache() {
        String key = TextUtils.isEmpty(cacheKey)?generateCacheKey():cacheKey;
        Object cache = CacheManager.getCache(key);
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.status = 304;
        apiResponse.message = "缓存获取成功";
        apiResponse.body = (T) cache;
        apiResponse.success = true;
        return apiResponse;
    }

    private  ApiResponse<T> parseResponse(Response response, JsonCallback<T> callback) {
        String message = null;
        int status = response.code();
        boolean success = response.isSuccessful();
        ApiResponse<T> apiResponse = new ApiResponse<>();
        Convert convert = ApiService.convert;
            try {
                String content = response.body().string();
                if(success){
                    if(callback!= null){
                        ParameterizedType type = (ParameterizedType) callback.getClass().getGenericSuperclass();
                        Type argument = type.getActualTypeArguments()[0];
                        apiResponse.body = (T) convert.convert(content, argument);
                    }else if(type !=null){
                        apiResponse.body = (T) convert.convert(content, type);
                    }else if(claz!= null){
                        apiResponse.body = (T) convert.convert(content, claz);
                    }else{
                        Log.e("request","response:无法解析");
                    }
                }else{
                    message = content;
                }
            } catch (IOException e) {
                message = e.getMessage();
                e.printStackTrace();
                success = false;
            }
            apiResponse.success = success;
            apiResponse.status = status;
            apiResponse.message = message;

            if(cacheStrategy != NET_ONLY && apiResponse.success && apiResponse.body != null && apiResponse.body instanceof Serializable){
                saveCache(apiResponse.body);
            }

            return apiResponse;
    }

    private void saveCache(T body) {
        String key = TextUtils.isEmpty(cacheKey)?generateCacheKey():cacheKey;
        CacheManager.save(key,body);
    }

    private String generateCacheKey() {
        cacheKey = UrlCreator.createUrlFromParams(url,params);
        return cacheKey;
    }

    public R responseType(Type type){
        this.type = type;
        return (R) this;
    }

    public R responseType(Class claz){
        this.claz = claz;
        return (R) this;
    }

    private Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders(builder);
        okhttp3.Request request =  generateRequest(builder);
        return ApiService.okHttpClient.newCall(request);
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);

    private void addHeaders(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(),entry.getValue());
        }
    }


}
