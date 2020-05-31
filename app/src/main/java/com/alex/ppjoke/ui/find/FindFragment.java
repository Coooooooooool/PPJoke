package com.alex.ppjoke.ui.find;


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
@FragmentDestination(pageUrl = "main/tabs/find",asStarter = false)
public class FindFragment extends Fragment {


    public FindFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("FindFragment","onCreateView");
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

}
