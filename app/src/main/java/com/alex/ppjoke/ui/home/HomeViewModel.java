package com.alex.ppjoke.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import com.alex.ppjoke.model.Feed;
import com.alex.ppjoke.ui.AbsViewModel;
import com.alex.ppjoke.ui.MutablePageKeyedDataSource;
import com.alex.ppjoke.ui.MuteableDataSource;
import com.alibaba.fastjson.TypeReference;
import com.example.libcommon.utils.LogUtils;
import com.example.libnetwork.ApiResponse;
import com.example.libnetwork.ApiService;
import com.example.libnetwork.JsonCallback;
import com.example.libnetwork.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeViewModel extends AbsViewModel<Feed> {

    private volatile boolean witchCache = true;

    private MutableLiveData<PagedList<Feed>> cacheLiveData = new MutableLiveData<>();

    private AtomicBoolean loadAfter = new AtomicBoolean(false);


    @Override
    public DataSource createDataSource() {
        return new FeedDataSource();
    }

    public MutableLiveData<PagedList<Feed>> getCacheLiveData() {
        return cacheLiveData;
    }

    class FeedDataSource extends ItemKeyedDataSource<Integer, Feed> {
        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            //加载初始化数据的
            LogUtils.e(TAG, "dataSource-->loadInitial: ");
            loadData(0, params.requestedLoadSize, callback);
            witchCache = false;
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            //向后加载分页数据的
            LogUtils.e(TAG, "dataSource-->loadAfter: ");
            loadData(params.key, params.requestedLoadSize, callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            //能够向前加载数据的
            callback.onResult(Collections.emptyList());
            LogUtils.e(TAG, "dataSource-->loadBefore: ");

        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }
    }


    private void loadData(int key, int count, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if(key>0){
            loadAfter.set(true);
        }
        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", null)
                .addParam("userId", 0)
                .addParam("feedId", key)
                .addParam("pageCount", count)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());

        if(witchCache){
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallback<List<Feed>>() {
                @Override
                public void onCacheSuccess(ApiResponse<List<Feed>> response) {
                    LogUtils.e(TAG,"onCacheSuccess-->size:"+response.body.size());
                    MutablePageKeyedDataSource dataSource = new MutablePageKeyedDataSource<Feed>();
                    dataSource.data.addAll(response.body);

                    PagedList pagedList = dataSource.buildNewPagedList(config);
                    cacheLiveData.postValue(pagedList);
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
                ((MutableLiveData) getBoundaryPageData()).postValue(data.size() > 0);
                loadAfter.set(false);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        LogUtils.e(TAG,"loadData-->key:"+key);
    }


    public void loadAfter(int id, ItemKeyedDataSource.LoadCallback<Feed> feedLoadCallback) {
        if(loadAfter.get()){
            feedLoadCallback.onResult(Collections.emptyList());
            return;
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                loadData(id,config.pageSize,feedLoadCallback);
            }
        });
    }
}