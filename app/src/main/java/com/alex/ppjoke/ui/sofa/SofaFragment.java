package com.alex.ppjoke.ui.sofa;


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
@FragmentDestination(pageUrl = "main/tabs/sofa",asStarter = false)
public class SofaFragment extends Fragment {


    public SofaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("SofaFragment","onCreateView");
        return inflater.inflate(R.layout.fragment_sofa, container, false);
    }

}
