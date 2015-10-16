package com.fluxinated.mixins.fragments;

import android.os.Bundle;

/**
 * Created by proxmaccoy25 on 10/16/2015.
 */
public class BluetoothFragment extends BaseFragment {

    public BluetoothFragment() {
    }

    public static BaseFragment getInstance(String params)
    {
        BluetoothFragment mBluetoothFragment = new BluetoothFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(FRAGMENT_KEY,params);
        mBluetoothFragment.setArguments(mBundle);
        return mBluetoothFragment;
    }

}
