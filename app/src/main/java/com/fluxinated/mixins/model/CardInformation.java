package com.fluxinated.mixins.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Fluxi on 8/15/2015.
 */
public class CardInformation implements Parcelable
{
    Liquor mLiquor;
    private int TileType;
    private String TileColor;
    private String mRibbonLabel;
    private String mRibbonColor;
    private int mRibbonColorResource;
    private int mCardPosition;
    private boolean mCardAvailable;


    public CardInformation(Liquor mLiquor)
    {
        this.mLiquor = mLiquor;
    }

    public Liquor getLiquor()
    {
        return mLiquor;
    }
/*public Object[] getmObjectArray()
    {
        return mObjectArray;
    }*/


    public String getTileColor()
    {
        return TileColor;
    }

    public void setTileColor(String tileColor)
    {
        TileColor = tileColor;
    }

    public void setTileType(int tileType)
    {
        TileType = tileType;
    }

    public int getTileType()
    {
        return TileType;
    }

    public String getRibbonLabel()
    {
        return mRibbonLabel;
    }

    public void setRibbonLabel(String mRibbonLabel)
    {
        this.mRibbonLabel = mRibbonLabel;
    }

    public String getRibbonColor()
    {
        return mRibbonColor;
    }

    public void setRibbonColor(String mRibbonColor)
    {
        this.mRibbonColor = mRibbonColor;
    }

    public int getRibbonColorResource()
    {
        return mRibbonColorResource;
    }

    public void setRibbonColorResource(int mRibbonColorResource)
    {
        this.mRibbonColorResource = mRibbonColorResource;
    }

    public int getCardPosition()
    {
        return mCardPosition;
    }

    public void setCardPosition(int cardPosition)
    {
        mCardPosition = cardPosition;
    }

    public boolean isCardAvailable()
    {
        return mCardAvailable;
    }

    public void setCardAvailable(boolean mCardAvailable)
    {
        this.mCardAvailable = mCardAvailable;
    }

    private CardInformation(Parcel in)
    {
        //this.mObjectArray = in.readArray(ClassLoader.getSystemClassLoader());
        this.mLiquor = (Liquor)in.readValue(ClassLoader.getSystemClassLoader());
        this.TileType = in.readInt();
        this.TileColor =in.readString();
        this.mRibbonLabel = in.readString();
        this.mRibbonColor = in.readString();
        this.mRibbonColorResource = in.readInt();
        this.mCardPosition = in.readInt();
        this.mCardAvailable = (boolean)in.readValue(ClassLoader.getSystemClassLoader());
    }

    public static final Creator<CardInformation> CREATOR = new Creator<CardInformation>()
    {
        @Override
        public CardInformation createFromParcel(Parcel in)
        {
            return new CardInformation(in);
        }

        @Override
        public CardInformation[] newArray(int size)
        {
            return new CardInformation[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        //dest.writeArray(this.mObjectArray);
        dest.writeValue(this.mLiquor);
        dest.writeInt(TileType);
        dest.writeString(TileColor);
        dest.writeString(mRibbonLabel);
        dest.writeString(mRibbonColor);
        dest.writeInt(mRibbonColorResource);
        dest.writeInt(mCardPosition);
        dest.writeValue(mCardAvailable);
    }
}
