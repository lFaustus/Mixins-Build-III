package com.fluxinated.mixins.model;

import com.fluxinated.mixins.enums.Bottle;

/**
 * Created by User on 12/10/2015.
 */
public class LiquorTag {

    private Bottle mBottle;
    private String mPresentLiquor;

    public LiquorTag(Bottle mBottle, String mPresentLiquor)
    {
        this.mBottle = mBottle;
        this.mPresentLiquor = mPresentLiquor;
    }

    public LiquorTag()
    {
    }

    public Bottle getBottle()
    {
        return mBottle;
    }

    public void setBottle(Bottle mBottle)
    {
        this.mBottle = mBottle;
    }

    public String getPresentLiquor()
    {
        return mPresentLiquor;
    }

    public void setPresentLiquor(String mPresentLiquor)
    {
        this.mPresentLiquor = mPresentLiquor;
    }
}
