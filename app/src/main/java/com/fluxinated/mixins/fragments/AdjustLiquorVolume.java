package com.fluxinated.mixins.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fluxinated.mixins.MainActivity;
import com.fluxinated.mixins.R;
import com.fluxinated.mixins.customviews.CircularSeekBar;
import com.fluxinated.mixins.customviews.PlayFontTextView;
import com.fluxinated.mixins.enums.Bottle;
import com.fluxinated.mixins.model.CardInformation;
import com.fluxinated.mixins.model.Liquor;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by User on 09/10/2015.
 */
public class AdjustLiquorVolume extends MixLiquor
{
    private CardInformation mCardInformation;
    protected Liquor mLiquor;
    private JSONArray mArray;
    protected Map<Bottle,String> mTempBottleSettings;

    public AdjustLiquorVolume(){}

    public static AdjustLiquorVolume getInstance(String param, Object extra)
    {
        AdjustLiquorVolume mAdjustLiquorVolume = new AdjustLiquorVolume();
        Bundle mBundle = new Bundle();
        mBundle.putString(FRAGMENT_KEY, param);
        if(extra instanceof Parcelable)
            mBundle.putParcelable("CardInformation", (Parcelable) extra);
        mAdjustLiquorVolume.setArguments(mBundle);
        return mAdjustLiquorVolume;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
        {
            mCardInformation = getArguments().getParcelable("CardInformation");

            if (mCardInformation != null)
            {
                mLiquor = mCardInformation.getLiquor();
                Log.e("Array",mCardInformation.getLiquor().getLiquorOrder().toString());
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        mTempBottleSettings = Collections.synchronizedMap(new LinkedHashMap<Bottle, String>());
        if(mArray == null )
            mArray =  mLiquor.getLiquorOrder();

        for(int i=0; i< mArray.length() ;i++)
        {
            if(i%2 ==0)

            for(Bottle b:Bottle.values())
            {
                try
                {
                    if(b.getBottleValue() == Integer.parseInt(mArray.get(i).toString()))
                    {
                        super.mOrder.put(b.name(), mArray.getString(i));
                        super.mOrder.put(b.name()+ BOTTLE_VOLUME, mArray.getString(i+1));
                        this.mTempBottleSettings.put(b,mCardInformation.getLiquor().getBottle(b.name()));
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        super.mJSONArrayLiquorOrder = new JSONArray(super.mOrder.values());

        try
        {

            super.mLiquorName = mLiquor.getLiquorName();
            super.mLiquorDescription = mLiquor.getLiquorDescription();
            super.mImageLocation = mLiquor.getLiquorPictureURI();

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    protected void initializeViews(ViewGroup vg)
    {
        mCurrentBottleSettings = mTempBottleSettings;
        try {
            for (int i = 0; i < vg.getChildCount(); i++) {

                if (vg.getChildAt(i) instanceof ViewGroup) {

                    initializeViews((ViewGroup) vg.getChildAt(i));
                }
                else {
                    if(vg.getChildAt(i) instanceof com.fluxinated.mixins.floatingactionbuttons.shell.fab.ActionButton || vg.getChildAt(i) instanceof Button || vg.getChildAt(i) instanceof ImageView)
                    {
                        vg.getChildAt(i).setOnClickListener(this);
                        if(vg.getChildAt(i).getId() == R.id.add_button_drinks)
                            ((Button) vg.getChildAt(i)).setText("Update");
                        else if(vg.getChildAt(i) instanceof ImageView)
                        {
                            if(super.mImageLocation != null)
                            ((MainActivity) getActivity()).getImageLoader().DisplayImage(mLiquor.getLiquorPictureURI(), mLiquor.getLiquorName(), (ImageView) vg.getChildAt(i));
                        }
                    }

                    else  if (vg.getChildAt(i) instanceof CircularSeekBar) {

                        vg.getChildAt(i).setTag(mBottle[mCounter]);

                        for(int m = 0 ;m<mArray.length();m++)
                        {
                            if(m % 2 == 0)
                            {
                                if(Integer.parseInt(mArray.getString(m)) == mBottle[mCounter].getBottleValue())
                                {
                                    ((CircularSeekBar) vg.getChildAt(i)).setProgress(Integer.parseInt(mArray.getString(m + 1)));
                                }
                            }
                        }
                        ((CircularSeekBar) vg.getChildAt(i)).setOnSeekBarChangeListener(new SeekBarListener());

                    }
                    else  if (vg.getChildAt(i) instanceof PlayFontTextView) {

                        if (vg.getChildAt(i).getTag() != null) {
                            // Log.e("Bottle: ",mBottle[mCounter]+"");
                            for(int m = 0 ;m<mArray.length();m++)
                            {
                                if(m % 2 == 0)
                                {
                                    if(Integer.parseInt(mArray.getString(m)) == mBottle[mCounter].getBottleValue())
                                    {
                                        ((TextView) vg.getChildAt(i)).setText(mArray.get(m+1).toString() + "ml");
                                    }
                                }
                            }
                            mTextViewSeekBarValue.put(mBottle[mCounter], (PlayFontTextView) vg.getChildAt(i));

                        }
                        else
                        {
                            if(vg.getChildAt(i).getId() != R.id.liquor_name)
                            {
                                vg.getChildAt(i).setOnClickListener(this);
                                vg.getChildAt(i).setTag(mBottle[mCounter]);
                                //((TextView) vg.getChildAt(i)).setText(mCurrentBottleSettings.get(mBottle[mCounter]));
                                String mTempString = mCurrentBottleSettings.get(mBottle[mCounter]) == null ?
                                          getResources().getString(R.string.liquor_label_default_value) : mCurrentBottleSettings.get(mBottle[mCounter]);
                                if(mTempString == getResources().getString(R.string.liquor_label_default_value))
                                    ((TextView) vg.getChildAt(i)).setTextColor(getResources().getColor(R.color.fab_material_red_500));
                                ((TextView) vg.getChildAt(i)).setText(mTempString);

                               /* if(mTempBottleSettings.get(mBottle[mCounter]) == null)
                                    ((TextView) vg.getChildAt(i)).setText(getActivity().getResources().getString(R.string.liquor_label_default_value));
                                else
                                    ((TextView) vg.getChildAt(i)).setText(mTempBottleSettings.get(mBottle[mCounter]));*/

                                mCounter++;
                            }
                            else
                                ((PlayFontTextView) vg.getChildAt(i)).setText(super.mLiquorName);

                        }

                    }
                    else
                    {
                        vg.getChildAt(i).setOnClickListener(this);
                    }

                }

            }
        } catch (NullPointerException exp) {
            Log.e("Null", "ViewGroup is Null");
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void onClick(View v)
    {
       /* switch (v.getId())
        {
            case R.id.liquor_image:
                Intent imgChooser = new Intent(getActivity(), FileChooser.class);
                startActivityForResult(imgChooser, 1);
                break;

            default:
                break;
        }*/
       // Log.e("array",new JSONArray(super.mOrder.values()).toString()+"");
        super.onClick(v);
    }
}
