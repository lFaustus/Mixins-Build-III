package com.fluxinated.mixins.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by proxmaccoy25 on 10/16/2015.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener{

    public static final String FRAGMENT_KEY = "FragmentKey";
    protected final static String BOTTLE_VOLUME = "VOLUME";
    protected String mParam;
    protected AlertDialog.Builder mDialog = null;


    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
            mParam = getArguments().getString(FRAGMENT_KEY);
    }

    @Override
    public void onClick(View v) {

    }
}
