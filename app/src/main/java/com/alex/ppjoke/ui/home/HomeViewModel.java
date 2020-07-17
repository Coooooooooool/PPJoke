package com.alex.ppjoke.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

import com.alex.ppjoke.model.Feed;
import com.alex.ppjoke.ui.AbsViewModel;
import com.alibaba.fastjson.TypeReference;
import com.example.libcommon.utils.LogUtils;
import com.example.libnetwork.ApiResponse;
import com.example.libnetwork.ApiService;
import com.example.libnetwork.JsonCallback;
import com.example.libnetwork.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeViewModel extends AbsViewModel<Feed> {

    private volatile boolean witchCache = true;

    @Override
    public DataSource createDataSource() {
        return dataSource;
    }


    ItemKeyedDataSource<Integer,Feed> dataSource = new ItemKeyedDataSource<Integer, Feed>() {
        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            //加载初始化数据
            loadData(0,callback);
            witchCache = false;
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            //加载分页数据
            loadData(params.key,callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            //向前加载
            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }
    };

    private void loadData(int key, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", null)
                .addParam("userId", 0)
                .addParam("feedId", key)
                .addParam("pageCount", 10)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());

        if(witchCache){
            request.cacheStrategy(Request.CACHE_ONLY);
            request.excute(new JsonCallback<List<Feed>>() {
                @Override
                public void onCacheSuccess(ApiResponse<List<Feed>> response) {
                    if(response.body!=null)
                        LogUtils.e("HomeViewModel","onCacheSuccess:"+response.body.size());
                }
            });
        }

        try {
            Request netRequest = witchCache?request.clone():request;
            netRequest.cacheStrategy(key==0?Request.NET_CACHE:Request.NET_ONLY);
            ApiResponse<List<Feed>> response = netRequest.execute();
            List<Feed> data = response.body == null ? Collections.emptyList() : response.body;
            callback.onResult(data);
            if(key>0){
                //通过livedata发送数据 告诉UI层 是否要关闭上拉加载分页的动画
                getBoundarbPageData().postValue(data.size()>0);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }


}