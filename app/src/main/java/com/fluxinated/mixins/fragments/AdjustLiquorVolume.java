package com.fluxinated.mixins.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fluxinated.mixins.R;

/**
 * Created by User on 09/10/2015.
 */
public class AdjustLiquorVolume extends MixLiquor
{
    private TextView mLiquorNameText;

    public AdjustLiquorVolume(){}

    public static AdjustLiquorVolume getInstance(String param)
    {
        AdjustLiquorVolume mAdjustLiquorVolume = new AdjustLiquorVolume();
        Bundle mBundle = new Bundle();
        mBundle.putString(FRAGMENT_KEY, param);
        mAdjustLiquorVolume.setArguments(mBundle);
        return mAdjustLiquorVolume;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mLiquorNameText = (TextView)getView().findViewById(R.id.liquor_name);
        mLiquorNameText.setVisibility(View.VISIBLE);
        mLiquorNameText.setText("AdjustLiquors");
    }
}
