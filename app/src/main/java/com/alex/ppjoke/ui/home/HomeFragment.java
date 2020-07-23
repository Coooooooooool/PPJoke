package com.alex.ppjoke.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.alex.libnavannotation.FragmentDestination;
import com.alex.ppjoke.R;
import com.alex.ppjoke.model.Feed;
import com.alex.ppjoke.ui.AbsListFragment;
import com.alex.ppjoke.ui.MutablePageKeyedDataSource;
import com.alex.ppjoke.ui.MuteableDataSource;
import com.example.libcommon.utils.LogUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends AbsListFragment<Feed,HomeViewModel> {


    @Override
    protected void afterCreateView() {
//        viewModel.getCacheLiveData().observe(this, new Observer<PagedList<Feed>>() {
//            @Override
//            public void onChanged(PagedList<Feed> feeds) {
//                submitList(feeds);
//            }
//        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel.getCacheLiveData().observe(this, new Observer<PagedList<Feed>>() {
            @Override
            public void onChanged(PagedList<Feed> feeds) {
                submitList(feeds);
            }
        });
    }

    @Override
    public PagedListAdapter getAdapter() {
        String feedType = getArguments() == null ? "all" : getArguments().getString("feedType");
        return new FeedAdapter(getContext(),feedType);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        final PagedList<Feed> currentList = adapter.getCurrentList();
        if (currentList == null || currentList.size() <= 0) {
            finishRefresh(false);
            return;
        }

        Feed feed = adapter.getCurrentList().get(adapter.getItemCount() - 1);
        viewModel.loadAfter(feed.id,new ItemKeyedDataSource.LoadCallback<Feed>(){

            @Override
            public void onResult(@NonNull List<Feed> data) {
                PagedList.Config config = adapter.getCurrentList().getConfig();
                if(data!=null&& data.size()>0){
                    MutablePageKeyedDataSource dataSource = new MutablePageKeyedDataSource();
                    dataSource.data.addAll(currentList);
                    dataSource.data.addAll(data);
                    PagedList pagedList = dataSource.buildNewPagedList(config);
                    submitList(pagedList);
                }
            }
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        LogUtils.e(TAG,"下拉刷新");
        viewModel.getDataSource().invalidate();
    }
}