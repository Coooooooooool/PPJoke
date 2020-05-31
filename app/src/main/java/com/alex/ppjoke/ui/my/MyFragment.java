package com.alex.ppjoke.ui.my;


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
@FragmentDestination(pageUrl = "main/tabs/my",asStarter = false)
public class MyFragment extends Fragment {


    public MyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("MyFragment","onCreateView");
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

}
