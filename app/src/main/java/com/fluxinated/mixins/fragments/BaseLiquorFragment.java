package com.fluxinated.mixins.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fluxinated.mixins.MainActivity;
import com.fluxinated.mixins.R;
import com.fluxinated.mixins.enums.Bottle;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public abstract class BaseLiquorFragment extends BaseFragment
{

    //public static final String FRAGMENT_KEY = "FragmentKey";
    //protected final static String BOTTLE_VOLUME = "VOLUME";
    //protected String mParam;
    //protected AlertDialog.Builder mDialog = null;

    protected Map<Bottle, TextView> mTextViewSeekBarValue = Collections.synchronizedMap(new HashMap<>());
    protected Map<String, String> mOrder = Collections.synchronizedMap(new LinkedHashMap<>());
    protected Map<Bottle,String> mCurrentBottleSettings;
    protected Bottle[] mBottle;


    public BaseLiquorFragment() {
    }

   /* public static BaseLiquorFragment getInstance(String param)
    {
        BaseLiquorFragment mBaseLiquorFragment = new BaseLiquorFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(FRAGMENT_KEY,param);
        mBaseLiquorFragment.setArguments(mBundle);
        return mBaseLiquorFragment;
    }*/



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBottle = ((MainActivity)getActivity()).getBottles();
        mCurrentBottleSettings = ((MainActivity)getActivity()).getCurrentBottleSettings();
        initializeViews((ViewGroup) getView().getRootView());
    }

    protected abstract void initializeViews(ViewGroup vg);
        // Log.e("From parent", "from parent");


    @Override
    public void onClick(View v)
    {

    }
}
