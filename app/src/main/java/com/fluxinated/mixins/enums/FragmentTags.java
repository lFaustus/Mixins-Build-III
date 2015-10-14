package com.fluxinated.mixins.enums;

/**
 * Created by User on 08/10/2015.
 */
public enum FragmentTags {

    LiquorList("LiquorList"),
    MixLiquor("MixLiquor"),
    ADJUSTLIQUOR("AdjustLiquor"),
    SETTINGS("Settings");
    private String TAG;

    FragmentTags(String t)
    {
        TAG = t;
    }
    public String getTAG()
    {
        return TAG;
    }
}
