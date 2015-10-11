package com.fluxinated.mixins.model;

import com.fluxinated.mixins.enums.Bottle;

/**
 * Created by User on 12/10/2015.
 */
public class LiquorTag {

    private Bottle mBottle;
    private boolean isAvailable;

    public LiquorTag(Bottle mBottle, boolean isAvailable) {
        this.mBottle = mBottle;
        this.isAvailable = isAvailable;
    }



    public Bottle getBottle() {
        return mBottle;
    }

    public void setBottle(Bottle mBottle) {
        this.mBottle = mBottle;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailability(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
