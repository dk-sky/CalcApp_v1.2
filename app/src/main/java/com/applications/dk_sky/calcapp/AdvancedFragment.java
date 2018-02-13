package com.applications.dk_sky.calcapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by D.Karabetskiy on 1/18/18.
 */

public class AdvancedFragment extends Fragment {

    public AdvancedFragment() {
        //Empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.advanced_fragment, container, false);
    }

}
