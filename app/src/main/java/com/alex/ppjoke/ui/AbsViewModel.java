package com.alex.ppjoke.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public abstract class AbsViewModel<T> extends ViewModel {

    protected String TAG = this.getClass().getSimpleName();
    private DataSource dataSource;
    private LiveData<PagedList<T>> pageData;
    protected PagedList.Config config;
    private MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();

    public AbsViewModel(){
        config = new PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(12)
//                .setMaxSize(100)
//                .setEnablePlaceholders(false)
//                .setPrefetchDistance()
                .build();


        pageData = new LivePagedListBuilder(factory, config)
                .setInitialLoadKey(0)
//                .setFetchExecutor()
                .setBoundaryCallback(callback)
                .build();

    }

    DataSource.Factory factory = new DataSource.Factory() {
        @NonNull
        @Override
        public DataSource create() {
            if(dataSource == null || dataSource.isInvalid())
                dataSource = createDataSource();
            return dataSource;
        }
    };


    PagedList.BoundaryCallback<T> callback = new PagedList.BoundaryCallback<T>() {
        @Override
        public void onZeroItemsLoaded() {
            boundaryPageData.postValue(false);
        }

        @Override
        public void onItemAtFrontLoaded(@NonNull T itemAtFront) {
            boundaryPageData.postValue(true);
        }

        @Override
        public void onItemAtEndLoaded(@NonNull T itemAtEnd) {
//            super.onItemAtEndLoaded(itemAtEnd);
        }
    };

    public abstract DataSource createDataSource();

    public LiveData<PagedList<T>> getPageData() {
        return pageData;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public MutableLiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }
}
