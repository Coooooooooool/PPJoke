package com.alex.ppjoke.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.alex.ppjoke.ui.AbsListFragment;

public abstract class AbsViewModel<T> extends ViewModel {

    private DataSource dataSource;
    private LiveData<PagedList<T>> pageData;

    private MutableLiveData<Boolean> boundarbPageData = new MutableLiveData<>();

    public AbsViewModel(){
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(12)
//                .setMaxSize(100)
//                .setEnablePlaceholders(false)
//                .setPrefetchDistance()
                .build();

        DataSource.Factory factory = new DataSource.Factory() {
            @NonNull
            @Override
            public DataSource create() {
                dataSource = createDataSource();
                return dataSource;
            }
        };

        PagedList.BoundaryCallback<T> callback = new PagedList.BoundaryCallback<T>() {
            @Override
            public void onZeroItemsLoaded() {
                boundarbPageData.postValue(false);
            }

            @Override
            public void onItemAtFrontLoaded(@NonNull T itemAtFront) {
                boundarbPageData.postValue(true);
            }

            @Override
            public void onItemAtEndLoaded(@NonNull T itemAtEnd) {
                super.onItemAtEndLoaded(itemAtEnd);
            }
        };

        pageData = new LivePagedListBuilder(factory, config)
                .setInitialLoadKey(0)
//                .setFetchExecutor()
                .setBoundaryCallback(callback)
                .build();

    }

    public abstract DataSource createDataSource();

    public LiveData<PagedList<T>> getPageData() {
        return pageData;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public MutableLiveData<Boolean> getBoundarbPageData() {
        return boundarbPageData;
    }
}
