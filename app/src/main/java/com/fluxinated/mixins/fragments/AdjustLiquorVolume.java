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
import com.fluxinated.mixins.model.LiquorTag;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by User on 09/10/2015.
 */
public class AdjustLiquorVolume extends MixLiquor
{
    private CardInformation mCardInformation;
    private JSONArray mLiquorOrderArray;
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
                super.mLiquor = mCardInformation.getLiquor();
                Log.e("Array",mCardInformation.getLiquor().getLiquorOrder().toString());
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        this.mTempBottleSettings = Collections.synchronizedMap(new LinkedHashMap<Bottle, String>());
        super.mCircularSeekBar = Collections.synchronizedMap(new HashMap<>());
        if(this.mLiquorOrderArray == null )
            this.mLiquorOrderArray =  super.mLiquor.getLiquorOrder();

        for(int i=0; i< this.mLiquorOrderArray.length() ;i++)
        {
            if(i%2 ==0)

            for(Bottle b:Bottle.values())
            {
                try
                {
                    if(b.getBottleValue() == Integer.parseInt(this.mLiquorOrderArray.get(i).toString()))
                    {
                        String name = this.mCardInformation.getLiquor().getBottle(b.name());
                        if(((MainActivity)getActivity()).getCurrentBottleSettings().containsValue(name)) {
                            super.mOrder.put(b.name(), this.mLiquorOrderArray.getString(i));
                            super.mOrder.put(b.name() + BOTTLE_VOLUME, this.mLiquorOrderArray.getString(i + 1));
                        }
                            this.mTempBottleSettings.put(b,name);
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        //super.mLiquorOrderArrayLiquorOrder = new JSONArray(super.mOrder.values());

        try
        {

            super.mLiquorID = super.mLiquor.getLiquorId();
            super.mLiquorName = super.mLiquor.getLiquorName();
            super.mLiquorDescription = super.mLiquor.getLiquorDescription();
            super.mImageLocation = super.mLiquor.getLiquorPictureURI();

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        super.onActivityCreated(savedInstanceState);
        super.mJSONArrayLiquorOrder = new JSONArray(super.mOrder.values());
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
                            ((MainActivity) getActivity()).getImageLoader().DisplayImage(super.mLiquor.getLiquorPictureURI(), super.mLiquor.getLiquorName(), (ImageView) vg.getChildAt(i));
                        }
                    }

                    else  if (vg.getChildAt(i) instanceof CircularSeekBar) {

                        vg.getChildAt(i).setTag(new LiquorTag(mBottle[mCounter], isAvailable));


                        for(int m = 0 ;m<mLiquorOrderArray.length();m++)
                        {
                            if(m % 2 == 0)
                            {
                                if(Integer.parseInt(mLiquorOrderArray.getString(m)) == mBottle[mCounter].getBottleValue())
                                {
                                    ((CircularSeekBar) vg.getChildAt(i)).setProgress(Integer.parseInt(mLiquorOrderArray.getString(m + 1)));
                                }
                            }
                        }
                        ((CircularSeekBar) vg.getChildAt(i)).setOnSeekBarChangeListener(new SeekBarListener());
                        super.mCircularSeekBar.put(mBottle[mCounter], (CircularSeekBar) vg.getChildAt(i));

                    }
                    else  if (vg.getChildAt(i) instanceof PlayFontTextView) {

                        if (vg.getChildAt(i).getTag() != null) {
                            // Log.e("Bottle: ",mBottle[mCounter]+"");
                            for(int m = 0 ;m<mLiquorOrderArray.length();m++)
                            {
                                if(m % 2 == 0)
                                {
                                    if(Integer.parseInt(mLiquorOrderArray.getString(m)) == mBottle[mCounter].getBottleValue())
                                    {
                                        ((TextView) vg.getChildAt(i)).setText(mLiquorOrderArray.get(m+1).toString() + "ml");
                                    }
                                }
                            }
                            mTextViewSeekBarValue.put(mBottle[mCounter], (PlayFontTextView) vg.getChildAt(i));

                        }
                        else
                        {
                            if(vg.getChildAt(i).getId() != R.id.liquor_name)
                            {
                                //mCurrentBottle is the bottle setting of the selected mixture
                                //not the universal bottle setting
                                vg.getChildAt(i).setOnClickListener(this);
                                ((TextView)vg.getChildAt(i)).setSelected(true);
                                vg.getChildAt(i).setTag(mBottle[mCounter]);
                                //((TextView) vg.getChildAt(i)).setText(mCurrentBottleSettings.get(mBottle[mCounter]));
                                String mTempString = mCurrentBottleSettings.get(mBottle[mCounter]) == null ?
                                          getResources().getString(R.string.liquor_label_default_value) : mCurrentBottleSettings.get(mBottle[mCounter]);
                                //if(mTempString == getResources().getString(R.string.liquor_label_default_value))
                                   // ((TextView) vg.getChildAt(i)).setTextColor(getResources().getColor(R.color.fab_material_red_500));
                                ((TextView) vg.getChildAt(i)).setText(mTempString);

                                    filter(((MainActivity) getActivity()).getCurrentBottleSettings().values(), ((TextView) vg.getChildAt(i)),super.mCircularSeekBar.get(mBottle[mCounter]));
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
    protected void filter(Collection<String> list, Object... obj) {
        TextView mTempTextView =((TextView)obj[0]);
        CircularSeekBar mTempCircularSeekBar = ((CircularSeekBar)obj[1]);
                mTempTextView.setTextColor(getResources().getColor(R.color.fab_material_red_500));
        /*for(String s:list)
        {

            Log.e("string ",s + " == " + ((TextView)obj[0]).getText().toString());
            if(!((TextView)obj[0]).getText().toString().equalsIgnoreCase(getResources().getString(R.string.liquor_label_default_value)))
            if(!s.equalsIgnoreCase(((TextView)obj[0]).getText().toString()))
            {
                //((TextView)obj[0]).setTextColor(getResources().getColor(R.color.fab_material_red_500));
                isAvailable = false;
                //super.mJSONArrayLiquorOrder = new JSONArray(super.mOrder.values());

                //super.mOrder.remove(((TextView)obj[0]).getText().toString());
                super.mOrder.entrySet().remove(((TextView)obj[0]).getText().toString());
                continue;
            } else {
                ((TextView)obj[0]).setTextColor(getResources().getColor(R.color.material_lightpurple));
                // triggeredView.setEnabled(true);
                isAvailable = true;
                break;
            }
        }*/

        for(String s: list)
        {
            if(!((TextView)obj[0]).getText().toString().equalsIgnoreCase(getResources().getString(R.string.liquor_label_default_value)))
                if(!s.equalsIgnoreCase(((TextView)obj[0]).getText().toString()))
                {
                    /*for (Iterator<Map.Entry<Bottle, String>> it = super.mCurrentBottleSettings.entrySet().iterator(); it.hasNext(); )
                    {
                        Map.Entry<Bottle, String> entry = it.next();

                        Log.e("iterator", entry.getValue().toString() + " == " + ((TextView)obj[0]).getText().toString());
                        if (!entry.getValue().equals(((TextView)obj[0]).getText().toString()))
                        {

                            it.remove();
                        }
                    }*/
                    //mCurrentBottleSettings.entrySet().remove(((TextView)obj[0]).getText().toString());
                    isAvailable = false;
                    continue;
                }
                else
                {
                    mTempTextView.setTextColor(getResources().getColor(R.color.material_lightpurple));
                    isAvailable = true;
                    break;
                }
        }
        mTempCircularSeekBar.setEnabled(isAvailable);

    }

    protected void filter2(Collection<String> list, Object... obj) {
        ((TextView)obj[0]).setTextColor(getResources().getColor(R.color.fab_material_red_500));
        for(String s:list)
        {

            Log.e("string ",s + " == " + ((TextView)obj[0]).getText().toString());
            if(!((TextView)obj[0]).getText().toString().equalsIgnoreCase(getResources().getString(R.string.liquor_label_default_value)))
                if(!s.equalsIgnoreCase(((TextView)obj[0]).getText().toString()))
                {
                    //((TextView)obj[0]).setTextColor(getResources().getColor(R.color.fab_material_red_500));
                    isAvailable = false;
                    //super.mJSONArrayLiquorOrder = new JSONArray(super.mOrder.values());

                    //super.mOrder.remove(((TextView)obj[0]).getText().toString());
                    super.mOrder.entrySet().remove(((TextView)obj[0]).getText().toString());
                    continue;
                } else {
                    ((TextView)obj[0]).setTextColor(getResources().getColor(R.color.material_lightpurple));
                    // triggeredView.setEnabled(true);
                    isAvailable = true;
                    break;
                }
        }


            for(String s:list)
            {
                if(!((TextView)obj[0]).getText().toString().equalsIgnoreCase(getResources().getString(R.string.liquor_label_default_value)))
                    if(!s.equalsIgnoreCase(((TextView)obj[0]).getText().toString()))
                    {
                        //((TextView)obj[0]).setTextColor(getResources().getColor(R.color.fab_material_red_500));
                        isAvailable = false;
                        //super.mJSONArrayLiquorOrder = new JSONArray(super.mOrder.values());

                        super.mOrder.entrySet().remove(((TextView)obj[0]).getText().toString());
                        continue;
                    } else {
                        ((TextView)obj[0]).setTextColor(getResources().getColor(R.color.material_lightpurple));
                        // triggeredView.setEnabled(true);
                        isAvailable = true;
                        break;
                    }
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
