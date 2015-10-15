package com.fluxinated.mixins.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fluxinated.mixins.R;
import com.fluxinated.mixins.customviews.CircularSeekBar;
import com.fluxinated.mixins.customviews.PlayFontTextView;


public class BottleSettings extends MixLiquor {


    public static BottleSettings getInstance(String param) {
        BottleSettings fragment = new BottleSettings();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_KEY, param);
        fragment.setArguments(args);
        return fragment;
    }

    public BottleSettings() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.bottle_settings,container,false);
    }

    @Override
    protected void initializeViews(ViewGroup vg) {
// Log.e("From parent", "from parent");
        try {
            for (int i = 0; i < vg.getChildCount(); i++) {

                if (vg.getChildAt(i) instanceof ViewGroup) {
                    if(vg.getChildAt(i).getId() == R.id.mix_liquor_sidebar) {
                        vg.getChildAt(i).setVisibility(View.GONE);
                        //continue;
                    }

                    initializeViews((ViewGroup) vg.getChildAt(i));
                }
                else {
                   /* if(vg.getChildAt(i) instanceof com.software.shell.fab.ActionButton || vg.getChildAt(i) instanceof Button || vg.getChildAt(i) instanceof ImageView)
                    {
                        vg.getChildAt(i).setOnClickListener(this);
                    }

                    else*/  if (vg.getChildAt(i) instanceof CircularSeekBar) {

                        vg.getChildAt(i).setTag(mBottle[mCounter]);
                        //vg.getChildAt(i).setTag(new LiquorTag(mBottle[mCounter], isAvailable));
                        ((CircularSeekBar) vg.getChildAt(i)).setOnSeekBarChangeListener(new SeekBarListener());

                    }
                    else  if (vg.getChildAt(i) instanceof PlayFontTextView) {

                        if (vg.getChildAt(i).getTag() != null) {
                            // Log.e("Bottle: ",mBottle[mCounter]+"");
                            mTextViewSeekBarValue.put(mBottle[mCounter], (PlayFontTextView) vg.getChildAt(i));
                            vg.getChildAt(i).setActivated(true);

                        }
                        else
                        {
                            if(vg.getChildAt(i).getId() != R.id.liquor_name)
                            {
                                vg.getChildAt(i).setOnClickListener(this);
                                ((TextView)vg.getChildAt(i)).setSelected(true);
                                vg.getChildAt(i).setTag(mBottle[mCounter]);
                                vg.getChildAt(i).setActivated(true);
                                ((TextView) vg.getChildAt(i)).setText(mCurrentBottleSettings.get(mBottle[mCounter]));
                                if(((TextView) vg.getChildAt(i)).getText().equals(getResources().getString(R.string.liquor_label_default_value)))
                                    ((TextView) vg.getChildAt(i)).setTextColor(getResources().getColor(R.color.fab_material_red_500));

                                mCounter++;
                            }
                        }

                    }
                    else if(vg.getChildAt(i) instanceof Button)
                    {
                        if(vg.getChildAt(i).getId() == R.id.mix_button_drinks)
                        {
                            mMixButton = (Button) vg.getChildAt(i);
                            mMixButton.setOnClickListener(this);
                        }
                        else if(vg.getChildAt(i).getId() == R.id.add_button_drinks)
                        {
                            mAddButton = (Button) vg.getChildAt(i);
                            mAddButton.setOnClickListener(this);
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


}
