package com.alex.ppjoke.ui.publish;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alex.libnavannotation.FragmentDestination;
import com.alex.ppjoke.R;

/**
 * A simple {@link Fragment} subclass.
 */
@FragmentDestination(pageUrl = "main/tabs/publish",asStarter = false)
public class PublishFragment extends Fragment {


    public PublishFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("PublishFragment","onCreateView");
        return inflater.inflate(R.layout.fragment_publish, container, false);
    }

}
