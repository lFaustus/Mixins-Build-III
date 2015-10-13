package com.fluxinated.mixins.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fluxinated.mixins.MainActivity;
import com.fluxinated.mixins.R;
import com.fluxinated.mixins.customviews.CircularSeekBar;
import com.fluxinated.mixins.customviews.PlayFontTextView;
import com.fluxinated.mixins.enums.Bottle;
import com.fluxinated.mixins.model.CardInformation;
import com.fluxinated.mixins.model.Liquor;
import com.fluxinated.mixins.model.LiquorTag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by User on 09/10/2015.
 */
public class AdjustLiquorVolume extends MixLiquor
{
    private CardInformation mCardInformation;
    private JSONArray mLiquorOrderArray;
    protected Map<Bottle, String> mTempBottleSettings;
    protected Map<Bottle, CircularSeekBar> mCircularSeekBar;
    Map<String,String> mInActivatedSeekbars_sanitation;
    Map<String,String> mOrderCopy;
    //Map<Bottle,String> mCurrentBottleSettingsCopy = Collections.synchronizedMap(new LinkedHashMap<>(mCurrentBottleSettings));

    public AdjustLiquorVolume()
    {
    }

    public static AdjustLiquorVolume getInstance(String param, Object extra)
    {
        AdjustLiquorVolume mAdjustLiquorVolume = new AdjustLiquorVolume();
        Bundle mBundle = new Bundle();
        mBundle.putString(FRAGMENT_KEY, param);
        if (extra instanceof Parcelable)
            mBundle.putParcelable("CardInformation", (Parcelable) extra);
        mAdjustLiquorVolume.setArguments(mBundle);
        return mAdjustLiquorVolume;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mCardInformation = getArguments().getParcelable("CardInformation");

            if (mCardInformation != null)
            {
                super.mLiquor = mCardInformation.getLiquor();
                Log.e("Array", mCardInformation.getLiquor().getLiquorOrder().toString());
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        this.mTempBottleSettings = Collections.synchronizedMap(new LinkedHashMap<Bottle, String>());
        this.mCircularSeekBar = Collections.synchronizedMap(new HashMap<>());
        this.mInActivatedSeekbars_sanitation = new HashMap<>();

        if (this.mLiquorOrderArray == null)
            this.mLiquorOrderArray = super.mLiquor.getLiquorOrder();

        for (int i = 0; i < this.mLiquorOrderArray.length(); i++)
        {
            if (i % 2 == 0)

                for (Bottle b : Bottle.values())
                {
                    try
                    {
                        if (b.getBottleValue() == Integer.parseInt(this.mLiquorOrderArray.get(i).toString()))
                        {
                            String name = this.mCardInformation.getLiquor().getBottle(b.name());
                           // if (((MainActivity) getActivity()).getCurrentBottleSettings().containsValue(name))
                            //{
                                super.mOrder.put(b.name(), this.mLiquorOrderArray.getString(i));
                                super.mOrder.put(b.name() + BOTTLE_VOLUME, this.mLiquorOrderArray.getString(i + 1));
                            //}
                            this.mTempBottleSettings.put(b, name);
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
        try
        {
            for (int i = 0; i < vg.getChildCount(); i++)
            {

                if (vg.getChildAt(i) instanceof ViewGroup)
                {

                    initializeViews((ViewGroup) vg.getChildAt(i));
                } else
                {
                    if (vg.getChildAt(i) instanceof com.fluxinated.mixins.floatingactionbuttons.shell.fab.ActionButton || vg.getChildAt(i) instanceof Button || vg.getChildAt(i) instanceof ImageView)
                    {
                        vg.getChildAt(i).setOnClickListener(this);
                        if (vg.getChildAt(i).getId() == R.id.add_button_drinks)
                            ((Button) vg.getChildAt(i)).setText("Update");
                        else if (vg.getChildAt(i) instanceof ImageView)
                        {
                            if (super.mImageLocation != null)
                                ((MainActivity) getActivity()).getImageLoader().DisplayImage(super.mLiquor.getLiquorPictureURI(), super.mLiquor.getLiquorName(), (ImageView) vg.getChildAt(i));
                        }
                    } else if (vg.getChildAt(i) instanceof CircularSeekBar)
                    {

                        //vg.getChildAt(i).setTag(new LiquorTag(mBottle[mCounter], isAvailable));
                        vg.getChildAt(i).setTag(mBottle[mCounter]);


                        for (int m = 0; m < mLiquorOrderArray.length(); m++)
                        {
                            if (m % 2 == 0)
                            {
                                if (Integer.parseInt(mLiquorOrderArray.getString(m)) == mBottle[mCounter].getBottleValue())
                                {
                                    ((CircularSeekBar) vg.getChildAt(i)).setProgress(Integer.parseInt(mLiquorOrderArray.getString(m + 1)));
                                }
                            }
                        }
                        ((CircularSeekBar) vg.getChildAt(i)).setOnSeekBarChangeListener(new SeekBarListener());
                        ((CircularSeekBar) vg.getChildAt(i)).setForceProgressUponSeekbarDisability(true);
                        this.mCircularSeekBar.put(mBottle[mCounter], (CircularSeekBar) vg.getChildAt(i));

                    } else if (vg.getChildAt(i) instanceof PlayFontTextView)
                    {

                        if (vg.getChildAt(i).getTag() != null)
                        {
                            // Log.e("Bottle: ",mBottle[mCounter]+"");
                            for (int m = 0; m < mLiquorOrderArray.length(); m++)
                            {
                                if (m % 2 == 0)
                                {
                                    if (Integer.parseInt(mLiquorOrderArray.getString(m)) == mBottle[mCounter].getBottleValue())
                                    {
                                        ((TextView) vg.getChildAt(i)).setText(mLiquorOrderArray.get(m + 1).toString() + "ml");
                                        //((TextView) vg.getChildAt(i)).setActivated(true);
                                    }
                                }
                            }
                            mTextViewSeekBarValue.put(mBottle[mCounter], (PlayFontTextView) vg.getChildAt(i));

                        } else
                        {
                            if (vg.getChildAt(i).getId() != R.id.liquor_name)
                            {
                                //mCurrentBottle is the bottle setting of the selected mixture
                                //not the universal bottle setting
                                vg.getChildAt(i).setOnClickListener(this);
                                ((TextView) vg.getChildAt(i)).setSelected(true);
                                LiquorTag mLiquorTag = new LiquorTag();
                                mLiquorTag.setBottle(mBottle[mCounter]);
                                vg.getChildAt(i).setTag(mLiquorTag);
                                //((TextView) vg.getChildAt(i)).setText(mCurrentBottleSettings.get(mBottle[mCounter]));
                                String mTempString = mCurrentBottleSettings.get(mBottle[mCounter]) == null ?
                                        getResources().getString(R.string.liquor_label_default_value) : mCurrentBottleSettings.get(mBottle[mCounter]);
                                //if(mTempString == getResources().getString(R.string.liquor_label_default_value))
                                // ((TextView) vg.getChildAt(i)).setTextColor(getResources().getColor(R.color.fab_material_red_500));
                                ((TextView) vg.getChildAt(i)).setText(mTempString);
                                //super.mTextViewSeekBarName.put(mBottle[mCounter], (TextView) vg.getChildAt(i));
                                this.filter(((MainActivity) getActivity()).getCurrentBottleSettings(), ((TextView) vg.getChildAt(i)), this.mCircularSeekBar.get(mBottle[mCounter]));
                               /* if(mTempBottleSettings.get(mBottle[mCounter]) == null)
                                    ((TextView) vg.getChildAt(i)).setText(getActivity().getResources().getString(R.string.liquor_label_default_value));
                                else
                                    ((TextView) vg.getChildAt(i)).setText(mTempBottleSettings.get(mBottle[mCounter]));*/

                                mCounter++;
                            } else
                                ((PlayFontTextView) vg.getChildAt(i)).setText(super.mLiquorName);

                        }

                    } else
                    {
                        vg.getChildAt(i).setOnClickListener(this);
                    }

                }

            }
        } catch (NullPointerException exp)
        {
            Log.e("Null", "ViewGroup is Null");
        } catch (Exception exp)
        {
            exp.printStackTrace();
        }
    }

    /*@Override
    protected void filter(Collection<String> list, Object... obj) {
        TextView mTempTextView =((TextView)obj[0]);
        CircularSeekBar mTempCircularSeekBar = ((CircularSeekBar)obj[1]);
                mTempTextView.setTextColor(getResources().getColor(R.color.fab_material_red_500));

        for(String s: list)
        {
            if(!mTempTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.liquor_label_default_value))) {
                if (!s.equalsIgnoreCase(mTempTextView.getText().toString())) {

                    //mCurrentBottleSettings.entrySet().remove(((TextView)obj[0]).getText().toString());
                    isAvailable = false;
                    continue;
                } else {
                    mTempTextView.setTextColor(getResources().getColor(R.color.material_lightpurple));
                    isAvailable = true;
                    break;
                }
            }else
                isAvailable = false;
        }
        mTempCircularSeekBar.setEnabled(isAvailable);

    }*/

    /*for (Iterator<Map.Entry<Bottle, String>> it = super.mCurrentBottleSettings.entrySet().iterator(); it.hasNext(); )
                    {
                        Map.Entry<Bottle, String> entry = it.next();

                        Log.e("iterator", entry.getValue().toString() + " == " + ((TextView)obj[0]).getText().toString());
                        if (!entry.getValue().equals(((TextView)obj[0]).getText().toString()))
                        {

                            it.remove();
                        }
                    }*/

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

    @Override
    protected void openDialog(View triggeredView, View layout, String button1_label, String button2_label, String message, View... extra)
    {
        //Log.e("overriden", "overriden");
        if (mDialog == null)
        {
            mDialog = new AlertDialog.Builder(getActivity());
            AlertDialog al = mDialog.create();
            //al.setView(LayoutInflater.from(getActivity()).inflate(R.layout.liquor_information_dialog_big,null));
            al.setView(layout);
            if (triggeredView instanceof Button)
                al.setCancelable(false);
            else
                al.setCancelable(true);

            if (message != null)
                al.setMessage(message);

            if (triggeredView.getId() == R.id.add_button_drinks)
            {
                ((EditText) extra[0]).setText(mLiquorName);
                extra[0].setEnabled(false);
                ((EditText) extra[1]).setText(mLiquorDescription);
                extra[1].requestFocus();
            }
            else if(triggeredView.getId() == View.NO_ID)
                al.setButton(DialogInterface.BUTTON_NEUTRAL, "Set to " + ((LiquorTag) triggeredView.getTag()).getPresentLiquor(), (dialog, which) -> {
                });

            //setting up buttons
            al.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    mDialog = null;
                }
            });

            al.setButton(DialogInterface.BUTTON_NEGATIVE, button1_label, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
                    mDialog = null;
                    dialog.dismiss();
                }
            });
            al.setButton(DialogInterface.BUTTON_POSITIVE, button2_label, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    //Toast.makeText(getActivity(), "Save", Toast.LENGTH_SHORT).show();
                    switch (triggeredView.getId())
                    {
                        case R.id.add_button_drinks:
                        case R.id.mix_button_drinks:

                            JSONObject mJSONObjectLiquor = new JSONObject();

                            try
                            {

                                //sets the pictureURL into JSONObject
                                if (mImageLocation != null)
                                    mJSONObjectLiquor.put(Liquor.JSONDB_LIQUOR_PIC_URL, mImageLocation);

                                //sets the Bottle with corresponding Bottle label and Bottle volume into JSONObject
                                for (Map.Entry<String, String> map : mOrder.entrySet())
                                {
                                    /*if (mCircularSeekBar.get(Bottle.valueOf(map.getKey().replace(BOTTLE_VOLUME, ""))).isEnabled())
                                    {
                                        if (!map.getKey().contains(BOTTLE_VOLUME))
                                        {
                                            mJSONObjectLiquor.put(map.getKey(), mCurrentBottleSettings.get(Bottle.valueOf(map.getKey())));
                                            mOrder.put(map.getKey(), String.valueOf(Bottle.valueOf(map.getKey()).getBottleValue()));
                                        } else
                                        {
                                            mJSONObjectLiquor.put(map.getKey(), map.getValue());
                                            mOrder.put(map.getKey(), map.getValue());
                                        }

                                    } else
                                    {
                                        if (!map.getKey().contains(BOTTLE_VOLUME))
                                        {
                                            mJSONObjectLiquor.put(map.getKey(), mCurrentBottleSettings.get(Bottle.valueOf(map.getKey())));
                                            mOrder.put(map.getKey(), String.valueOf(Bottle.valueOf(map.getKey()).getBottleValue()));
                                        } else
                                        {
                                            mJSONObjectLiquor.put(map.getKey(), "0");
                                            mOrder.put(map.getKey(), "0");
                                        }
                                    }*/

                                    if (!map.getKey().contains(BOTTLE_VOLUME))//BOTTLE
                                    {
                                        mJSONObjectLiquor.put(map.getKey(), mCurrentBottleSettings.get(Bottle.valueOf(map.getKey())));
                                        // mOrder.put(map.getKey(), String.valueOf(Bottle.valueOf(map.getKey()).getBottleValue()));
                                    } else //BOTTLEVOLUME
                                    {
                                        if (triggeredView.getId() == R.id.mix_button_drinks)
                                        {
                                            if (mCircularSeekBar.get(Bottle.valueOf(map.getKey().replace(BOTTLE_VOLUME, ""))).isEnabled())
                                            {
                                                mJSONObjectLiquor.put(map.getKey(), map.getValue());
                                                // mOrder.put(map.getKey(), String.valueOf(mCircularSeekBar.get(Bottle.valueOf(map.getKey().replace(BOTTLE_VOLUME, ""))).getProgress()));
                                            } else
                                            {

                                                mJSONObjectLiquor.put(map.getKey(), map.getValue());
                                                //mOrder.put(map.getKey(), getResources().getString(R.string.liquor_availability));
                                                mInActivatedSeekbars_sanitation.put(map.getKey(), map.getKey());
                                            }
                                        } else
                                        {
                                            Log.e("getValue", map.getValue());
                                            mJSONObjectLiquor.put(map.getKey(), map.getValue());
                                            /*if (!map.getKey().contains(BOTTLE_VOLUME))
                                            {
                                                mJSONObjectLiquor.put(map.getKey(), mCurrentBottleSettings.get(Bottle.valueOf(map.getKey())));
                                                mOrder.put(map.getKey(), String.valueOf(Bottle.valueOf(map.getKey()).getBottleValue()));
                                            } else
                                            {
                                                mJSONObjectLiquor.put(map.getKey(), map.getValue());
                                                mOrder.put(map.getKey(), map.getValue());
                                            }*/
                                        }
                                    }

                                }



                                /* sanitizes order array list, removes object with value 0
                                 * meaning the its circular seekbar has been disabled/inactivated
                                 */
                                mOrderCopy = Collections.synchronizedMap(new LinkedHashMap<>(mOrder));
                                if (triggeredView.getId() == R.id.mix_button_drinks)
                                {
                                    for (Iterator<Map.Entry<String, String>> it = mOrder.entrySet().iterator(); it.hasNext(); )
                                    {
                                        Map.Entry<String, String> entry = it.next();

                                        //Log.e("iterator", entry.getValue().toString() + " == " + ((TextView) obj[0]).getText().toString());
                                        // if (entry.getValue().equals(getResources().getString(R.string.liquor_availability))) //value of BOTTLEVOLUME
                                        if (entry.getKey().equals(mInActivatedSeekbars_sanitation.get(entry.getKey())))
                                        {
                                            //BOTTLE
                                            mOrderCopy.remove(mInActivatedSeekbars_sanitation.get(entry.getKey()).replace(BOTTLE_VOLUME, ""));
                                            //BOTTLEVOLUME
                                            mOrderCopy.remove(mInActivatedSeekbars_sanitation.get(entry.getKey()));

                                        }
                                    }
                                }


                                //sets the liquor order array into JSONObject
                                mJSONArrayLiquorOrder = new JSONArray(mOrderCopy.values());
                                mJSONObjectLiquor.put("Order", mJSONArrayLiquorOrder);

                                //sets the date added into JSONObject
                                String mDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                                mJSONObjectLiquor.put(Liquor.JSONDB_LIQUOR_DATE_ADDED, mDate);

                                if (triggeredView.getId() != R.id.mix_button_drinks)
                                {


                                    mLiquorDescription = ((EditText) extra[1]).getText().toString();
                                    //sets the liquor name into JSONObject
                                    mJSONObjectLiquor.put(Liquor.JSONDB_LIQUOR_NAME, mLiquorName);

                                    //sets the liquor description into JSONObject
                                    mJSONObjectLiquor.put(Liquor.JSONDB_LIQUOR_DESCRIPTION, mLiquorDescription);


                                    if (((MainActivity) getActivity()).UpdateLiquor(Integer.valueOf(mLiquorID),mLiquorName,mLiquorDescription,mJSONObjectLiquor ))
                                    {
                                        mLiquor.setJSONLiquor(mJSONObjectLiquor);
                                        Toast.makeText(getActivity(), "updated successfully", Toast.LENGTH_SHORT).show();
                                    } else
                                    {
                                        Toast.makeText(getActivity(), "updated failed! Please try again", Toast.LENGTH_SHORT).show();
                                    }


                                }

                                //Log.e("mLiquorName", mLiquorName);
                                //Log.e("mLiquorDesc", mLiquorDescription);

                                Log.e("JSONADD", mJSONObjectLiquor.toString());
                            } catch (JSONException exp)
                            {
                                Toast.makeText(getActivity(), "Opps Something went wrong", Toast.LENGTH_SHORT).show();
                                exp.printStackTrace();
                            }
                            break;


                        default:
                            TextView mTextView = (TextView) triggeredView;
                            EditText mEditText = (EditText) extra[0];
                            mTextView.setText(mEditText.getText());


                            Bottle b = ((LiquorTag) mTextView.getTag()).getBottle();
                            //mTextView.setTextColor(getResources().getColor(R.color.fab_material_red_500));
                            //triggeredView.setEnabled(false);

                            filter(((MainActivity) getActivity()).getCurrentBottleSettings(), mTextView, mCircularSeekBar.get(b));
                            if (!mCircularSeekBar.get(b).isEnabled())
                                mInActivatedSeekbars_sanitation.put(b.name(), b.name());
                            else
                            {
                                mInActivatedSeekbars_sanitation.remove(b.name());
                                if (mCircularSeekBar.get(b).getProgress() != 0)
                                    mOrder.put(b.name() + BOTTLE_VOLUME, String.valueOf(mCircularSeekBar.get(b).getProgress()));
                            }

                            //Log.e("isEnabled?",mCircularSeekBar.get(b).isEnabled()+"");

                            /*Predicate<Integer> n = new Predicate<Integer>()
                            {
                                @Override
                                public boolean apply(Integer integer)
                                {
                                    return false;
                                }
                            }*/
                            /*if (mCircularSeekBar.get(b).isEnabled())
                            {
                                if (mCircularSeekBar.get(b).getProgress() != 0)
                                {

                                }

                            } else
                            {
                                mOrder.remove(b.name());
                                mCurrentBottleSettings.remove(b);
                                mJSONArrayLiquorOrder = new JSONArray(mOrder.values());
                            }*/


                            mCurrentBottleSettings.put(b, mTextView.getText().toString());
                            //Log.e("isAvailable", isAvailable + "");
                            //mMixButton.setEnabled(isAvailable);
                            mDialog = null;


                    }
                    mDialog = null;
                    dialog.dismiss();


                }
            });

            al.show();

            if(triggeredView.getId() == View.NO_ID)
            {
                //((EditText) extra[0]).setHint("Enter Name - (currently available Liquor - "+((LiquorTag) triggeredView.getTag()).getPresentLiquor() + " )");

                extra[1].setVisibility(View.VISIBLE);
                ((TextView) extra[1]).setText("available liquor for this " + numbersuffix(Integer.parseInt(((LiquorTag) triggeredView.getTag()).getBottle().name().replace("BOTTLE", ""))) + " dispenser  - " + ((LiquorTag) triggeredView.getTag()).getPresentLiquor());



                Button b = al.getButton(DialogInterface.BUTTON_NEUTRAL);
                b.setText("Set to " + ((LiquorTag) triggeredView.getTag()).getPresentLiquor());
                b.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ((EditText) extra[0]).setText(((LiquorTag) triggeredView.getTag()).getPresentLiquor());
                    }
                });
            }


           /* WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(al.getWindow().getAttributes());
            lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getActivity().getResources().getDisplayMetrics());
            al.getWindow().setAttributes(lp);*/

        }
    }

    protected void filter(Map<Bottle,String> list, Object... obj)
    {
        TextView mTempTextView = ((TextView) obj[0]);
        CircularSeekBar mTempCircularSeekBar = ((CircularSeekBar) obj[1]);
        Bottle b = (Bottle) mTempCircularSeekBar.getTag(); //same as mTempTextView
        //mTempTextView.setTextColor(getResources().getColor(R.color.fab_material_red_500));
        mTempTextView.setActivated(false);
        mTextViewSeekBarValue.get(b).setActivated(false);



        for(Iterator<Map.Entry<Bottle,String>> it = list.entrySet().iterator();it.hasNext();)
        {
            Map.Entry<Bottle,String> entry = it.next();
            if(entry.getKey().name().equals(b.name()))
            {
                ((LiquorTag)mTempTextView.getTag()).setPresentLiquor(entry.getValue());
            }

            if(!mTempTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.liquor_label_default_value)))
            {
                if(!entry.getValue().equalsIgnoreCase(mTempTextView.getText().toString()))
                {

                    isAvailable = false;
                    continue;
                }
                else
                {
                    //Log.e("entry key",entry.getKey() + " == " + b.name());
                   // Log.e("entry value", entry.getValue() + "==" + mTempTextView.getText().toString());

                    //if()
                    if(entry.getKey().name().equals(b.name()))
                    {
                        Log.e("filter",entry.getValue());
                        //mTempTextView.setTextColor(getResources().getColor(R.color.material_lightpurple));
                        isAvailable = true;
                        mTempTextView.setActivated(isAvailable);
                       // ((LiquorTag)mTempTextView.getTag()).setPresentLiquor(entry.getValue());
                        mTextViewSeekBarValue.get(b).setActivated(isAvailable);

                        break;
                    }
                    else
                    {
                        //Log.e("false","false");
                        isAvailable = false;
                    }
                    // triggeredView.setEnabled(true);
                }
            }
            else
            {
                isAvailable = false;
            }


        }
        mTempCircularSeekBar.setEnabled(isAvailable);

        /*for (String s : list)
        {
            //Log.e("string", s);
            if (!mTempTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.liquor_label_default_value))
                    )
            {
                if (!s.equalsIgnoreCase(mTempTextView.getText().toString()))
                {
                    //((TextView)obj[0]).setTextColor(getResources().getColor(R.color.fab_material_red_500));
                    isAvailable = false;
                    continue;
                } else
                {
                    mTempTextView.setTextColor(getResources().getColor(R.color.material_lightpurple));
                    // triggeredView.setEnabled(true);
                    isAvailable = true;
                    break;
                }
            } else
            {
                isAvailable = false;
            }
        }
        mTempCircularSeekBar.setEnabled(isAvailable);*/

    }

    private String numbersuffix(int number)
    {
        switch (number % 10) {
            case 1:  return String.valueOf(number)+"st";
            case 2:  return String.valueOf(number)+"nd";
            case 3:  return String.valueOf(number)+"rd";
            default: return String.valueOf(number)+"th";
        }
    }

   /* protected void setJSONLiquorOrder(JSONObject mJSONObjectLiquor,Predicate<Object> condition) throws JSONException
    {
        for (Map.Entry<String, String> map : mOrder.entrySet())
        {
            if (!map.getKey().contains(BOTTLE_VOLUME) && condition.apply(null))

                mJSONObjectLiquor.put(map.getKey(), mCurrentBottleSettings.get(Bottle.valueOf(map.getKey())));
            else
                mJSONObjectLiquor.put(map.getKey(), map.getValue());
        }
    }*/
}
