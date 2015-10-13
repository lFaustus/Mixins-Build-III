package com.fluxinated.mixins.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fluxinated.mixins.MainActivity;
import com.fluxinated.mixins.R;
import com.fluxinated.mixins.customviews.CircleImageView;
import com.fluxinated.mixins.customviews.CircularSeekBar;
import com.fluxinated.mixins.customviews.PlayFontTextView;
import com.fluxinated.mixins.enums.Bottle;
import com.fluxinated.mixins.filechooser.FileChooser;
import com.fluxinated.mixins.model.Liquor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by User on 08/10/2015.
 */
public class MixLiquor extends BaseLiquorFragment
{
    protected JSONArray mJSONArrayLiquorOrder;
    protected Liquor mLiquor;
    protected int mCounter = 0;
    protected AlertDialog.Builder mDialog = null;
    protected String mImageLocation,mLiquorName,mLiquorDescription;
    protected int mLiquorID;
    protected CircleImageView imgView;
    protected TextView mTextView;
    protected boolean isAvailable = true;
    protected Button mMixButton;


    public MixLiquor(){}

    public static BaseLiquorFragment getInstance(String param)
    {
        MixLiquor mMixLiquor = new MixLiquor();
        Bundle mBundle = new Bundle();
        mBundle.putString(FRAGMENT_KEY, param);
        mMixLiquor.setArguments(mBundle);
        return mMixLiquor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.mix_drinks_ui,container,false);
        imgView = (CircleImageView)view.findViewById(R.id.liquor_image);
        mTextView = (TextView)view.findViewById(R.id.liquor_name);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMixButton = ((Button)getView().findViewById(R.id.mix_button_drinks));

        /* Checks if the liquor order array is not zero(if there's one or more ingredient has choosen)
         * or when at "adjust liquor volume", if one or more ingredients doesn't match up with universal current bottle
         * settings the mix button will be disabled
         */
        if(mOrder.values().size() == 0)
            mMixButton.setEnabled(false);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {

            if (resultCode == Activity.RESULT_OK)
            {
                mImageLocation = data.getStringExtra(FileChooser.IMAGE_URL);
                Log.e("ImageLocation", mImageLocation);

                // Log.e("bytesize", data.getByteArrayExtra(FileChooser.IMAGE_BYTE).length + "");
                //decodeChoosenImage(data.getByteArrayExtra(FileChooser.IMAGE_BYTE));
                ((MainActivity) getActivity()).getImageLoader().DisplayImage(mImageLocation, data.getStringExtra(FileChooser.IMAGE_NAME), imgView);
            }
        }
    }

    @Override
    protected void initializeViews(ViewGroup vg) {
       // Log.e("From parent", "from parent");
        try {
            for (int i = 0; i < vg.getChildCount(); i++) {

                if (vg.getChildAt(i) instanceof ViewGroup) {

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
        switch(v.getId())
        {
            case R.id.liquor_image:
                Intent imgChooser = new Intent(getActivity(), FileChooser.class);
                startActivityForResult(imgChooser,1);
                break;

            case R.id.add_button_drinks:
                //Toast.makeText(getActivity(), "Add", Toast.LENGTH_SHORT).show();
                v.setEnabled(false);
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.liquor_information_register, null);
                EditText mEditText_name = (EditText) view.findViewById(R.id.liquor_name_register);
                EditText mEditText_description = (EditText) view.findViewById(R.id.liquor_description_register);
               // if(MixLiquor.this.mTextView.getText().equals(null) || MixLiquor.this.mTextView.getText().equals(""))
                    mEditText_name.setHint("Name");
               // else
                   // mEditText_name.setHint(MixLiquor.this.mTextView.getText());

                //mEditText_name.moveCursorToVisibleOffset();
                mEditText_description.setHint("Description");
                openDialog(v, view, "Cancel", "Save", null, mEditText_name, mEditText_description);
                v.setEnabled(true);
                break;

            case R.id.mix_button_drinks:
                //Toast.makeText(getActivity(), "Mix", Toast.LENGTH_SHORT).show();
                //v.setEnabled(false);
                openDialog(v, null, "Cancel","Pour","Pour?");
                break;

            default:
                if (!(v instanceof ImageView))
                {
                    View mView = LayoutInflater.from(getActivity()).inflate(R.layout.bottle_settings, null);
                    EditText mEditText = (EditText) mView.findViewById(R.id.liquor_name_settings);
                    TextView mTextView = (TextView) mView.findViewById(R.id.liquor_reminder);
                    //mTextView.setOnClickListener(v1 -> mEditText.setText(((LiquorTag) v.getTag()).getPresentLiquor()));
                    mTextView.setSelected(true);
                    mEditText.setHint("Name");
                    openDialog(v, mView,"Cancel","Save",null ,mEditText,mTextView);
                }
                break;
        }

    }


    protected void openDialog(View triggeredView,View layout,String button1_label,String button2_label,String message,View... extra)
    {
        if(mDialog == null)
        {
            mDialog = new AlertDialog.Builder(getActivity());
            AlertDialog al = mDialog.create();
            //al.setView(LayoutInflater.from(getActivity()).inflate(R.layout.liquor_information_dialog_big,null));
            al.setView(layout);
            if(triggeredView instanceof Button)
                al.setCancelable(false);
            else
                al.setCancelable(true);

            if(message != null)
                al.setMessage(message);




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
            al.setButton(DialogInterface.BUTTON_POSITIVE, button2_label, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(getActivity(), "Save", Toast.LENGTH_SHORT).show();
                    switch (triggeredView.getId()) {
                        case R.id.add_button_drinks:
                        case R.id.mix_button_drinks:

                            JSONObject mJSONObjectLiquor = new JSONObject();
                            try {

                                //sets the pictureURL into JSONObject
                                if (mImageLocation != null)
                                    mJSONObjectLiquor.put(Liquor.JSONDB_LIQUOR_PIC_URL, mImageLocation);

                                //sets the Bottle with corresponding Bottle label and Bottle volume into JSONObject
                                for (Map.Entry<String, String> map : mOrder.entrySet()) {

                                    if (!map.getKey().contains(BOTTLE_VOLUME))
                                        mJSONObjectLiquor.put(map.getKey(), mCurrentBottleSettings.get(Bottle.valueOf(map.getKey())));
                                    else
                                        mJSONObjectLiquor.put(map.getKey(), map.getValue());
                                }
                                //sets the liquor order array into JSONObject
                                mJSONArrayLiquorOrder = new JSONArray(mOrder.values());
                                mJSONObjectLiquor.put("Order", mJSONArrayLiquorOrder);

                                //sets the date added into JSONObject
                                String mDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                                mJSONObjectLiquor.put(Liquor.JSONDB_LIQUOR_DATE_ADDED, mDate);

                                if (triggeredView.getId() != R.id.mix_button_drinks) {


                                        mLiquorName = ((EditText) extra[0]).getText().toString();
                                        mLiquorDescription = ((EditText) extra[1]).getText().toString();

                                        //sets the liquor name into JSONObject
                                        mJSONObjectLiquor.put(Liquor.JSONDB_LIQUOR_NAME, mLiquorName);

                                        //sets the liquor description into JSONObject
                                        mJSONObjectLiquor.put(Liquor.JSONDB_LIQUOR_DESCRIPTION, mLiquorDescription);

                                        MixLiquor.this.mTextView.setText(mLiquorName);
                                        if (((MainActivity) getActivity()).CreateLiquor(mJSONObjectLiquor))
                                            Toast.makeText(getActivity(), "mixture successfully added", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(getActivity(), "Failed to add mixture! Please try again", Toast.LENGTH_SHORT).show();



                                }

                                Log.e("JSONADD", mJSONObjectLiquor.toString());
                            } catch (JSONException exp) {
                                Toast.makeText(getActivity(), "Opps Something went wrong", Toast.LENGTH_SHORT).show();
                                exp.printStackTrace();
                            }
                            break;


                        default:
                            TextView mTextView = (TextView) triggeredView;
                            EditText mEditText = (EditText) extra[0];
                            mTextView.setText(mEditText.getText());

                                //mTextView.setTextColor(getResources().getColor(R.color.material_gray));
                                SharedPreferences.Editor editor = ((MainActivity) getActivity()).getSharedPreferences().edit();
                                Bottle b = (Bottle) mTextView.getTag();
                                editor.putString(b.name(), mTextView.getText().toString());
                                editor.commit();
                                mCurrentBottleSettings.put(b, mTextView.getText().toString());


                            break;
                    }

                    mDialog = null;
                    dialog.dismiss();
                }
            });
            al.show();
           /* WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(al.getWindow().getAttributes());
            lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getActivity().getResources().getDisplayMetrics());
            al.getWindow().setAttributes(lp);*/

        }
    }



    protected class SeekBarListener implements  CircularSeekBar.OnCircularSeekBarChangeListener
    {
        private TextView mSeekBarTextViewValue;
        SeekBarListener(){}

        @Override
        public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
            mSeekBarTextViewValue.setText(progress + "ml");
        }

        @Override
        public void onStopTrackingTouch(CircularSeekBar seekBar) {
            Bottle bottle = (Bottle) seekBar.getTag();
            if (mOrder.get(bottle.name()) == null && seekBar.getProgress() != 0) {
                mOrder.put(bottle.name(), String.valueOf(bottle.getBottleValue()));
                mOrder.put(bottle.name() + BOTTLE_VOLUME, String.valueOf(seekBar.getProgress()));
            }
            else if (mOrder.get(bottle.name()) != null && seekBar.getProgress() >= 0) {
                if (seekBar.getProgress() == 0) {
                    mOrder.remove(bottle.name());
                    mOrder.remove(bottle.name() + BOTTLE_VOLUME);
                }
                else {
                    mOrder.put(bottle.name() + BOTTLE_VOLUME, String.valueOf(seekBar.getProgress()));
                }
            }

            /*if(MixLiquor.this.getClass().equals(AdjustLiquorVolume.class) &&
                    ((MainActivity)getActivity()).getCurrentBottleSettings()
                                                 .containsValue(bottle.name())
                    && !isAvailable)
            {
                mOrder.remove(bottle.name());
                mOrder.remove(bottle.name() + BOTTLE_VOLUME);
            }*/
            // Log.i("Order",mOrder.values()+"");
            /*
             *Checking the size of liquor order array
             * if there's an order and the mix's button state is not yet enabled,
             * the said button will become enabled, if it is already enabled and
             * the liquor order array size has changed but not zero, the button doesn't
             * need to be enabled and redraw again
             */
            if(mOrder.values().size() != 0 && !mMixButton.isEnabled()) {
                mMixButton.setEnabled(true);
            }
            if(mOrder.values().size() == 0)
                mMixButton.setEnabled(false);

            //mJSONArrayLiquorOrder = new JSONArray(mOrder.values());

        }

        @Override
        public void onStartTrackingTouch(CircularSeekBar seekBar) {
            mSeekBarTextViewValue = mTextViewSeekBarValue.get(seekBar.getTag());
            Log.e("track","track");

        }
    }
}
