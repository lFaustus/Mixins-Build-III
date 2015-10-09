package com.fluxinated.mixins.model;

/**
 * Created by Fluxi on 9/7/2015.
 */
public class LiquorInformationItem
{
    String Label;
    Liquor mLiquor;

    public LiquorInformationItem()
    {
    }
    public void setLiquor(Liquor mLiquor)
    {
        this.mLiquor = mLiquor;
    }

    public  void setLabel(String label)
    {
        this.Label = label;
    }

    public String getLabel()
    {
        return Label;
    }

    public Liquor getLiquor()
    {
        return mLiquor;
    }
}
