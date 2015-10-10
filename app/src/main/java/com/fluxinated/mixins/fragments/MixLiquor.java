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

    protected int mCounter = 0;
    protected AlertDialog.Builder mDialog = null;
    protected String mImageLocation,mLiquorName,mLiquorDescription;
    protected CircleImageView imgView;
    protected TextView mTextView;

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
                        ((CircularSeekBar) vg.getChildAt(i)).setOnSeekBarChangeListener(new SeekBarListener());

                    }
                    else  if (vg.getChildAt(i) instanceof PlayFontTextView) {

                        if (vg.getChildAt(i).getTag() != null) {
                           // Log.e("Bottle: ",mBottle[mCounter]+"");
                            mTextViewSeekBarValue.put(mBottle[mCounter], (PlayFontTextView) vg.getChildAt(i));

                        }
                        else
                        {
                            if(vg.getChildAt(i).getId() != R.id.liquor_name)
                            {
                                vg.getChildAt(i).setOnClickListener(this);
                                vg.getChildAt(i).setTag(mBottle[mCounter]);
                                ((TextView) vg.getChildAt(i)).setText(mCurrentBottleSettings.get(mBottle[mCounter]));
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
                openDialog(v, null, "Cancel","Pour","Pour?");
                break;

            default:
                if (!(v instanceof ImageView))
                {
                    View mView = LayoutInflater.from(getActivity()).inflate(R.layout.bottle_settings, null);
                    EditText mEditText = (EditText) mView.findViewById(R.id.liquor_name_settings);
                    mEditText.setHint("Name");
                    openDialog(v, mView,"Cancel","Save",null ,mEditText);
                }
                break;
        }

    }


    private void openDialog(View triggeredView,View layout,String button1_label,String button2_label,String message,View... extra)
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


            if (MixLiquor.this.getClass().equals(AdjustLiquorVolume.class))
            {
                ((EditText) extra[0]).setText(mLiquorName);
                 extra[0].setEnabled(false);
                ((EditText) extra[1]).setText(mLiquorDescription);
                ((EditText) extra[1]).requestFocus();
            }

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
                                if(mImageLocation != null)
                                mJSONObjectLiquor.put(Liquor.JSONDB_LIQUOR_PIC_URL, mImageLocation);

                                //sets the Bottle with corresponding Bottle label and Bottle volume into JSONObject
                                for (Map.Entry<String, String> map : mOrder.entrySet())
                                {

                                    if (!map.getKey().contains(BOTTLE_VOLUME))
                                        mJSONObjectLiquor.put(map.getKey(), mCurrentBottleSettings.get(Bottle.valueOf(map.getKey())));
                                    else
                                        mJSONObjectLiquor.put(map.getKey(), map.getValue());
                                }
                                //sets the liquor order array into JSONObject
                                mJSONObjectLiquor.put("Order", mJSONArrayLiquorOrder);

                                //sets the date added into JSONObject
                                String mDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                                mJSONObjectLiquor.put(Liquor.JSONDB_LIQUOR_DATE_ADDED, mDate);

                                if (triggeredView.getId() != R.id.mix_button_drinks)
                                {

                                    if (!MixLiquor.this.getClass().equals(AdjustLiquorVolume.class))
                                    {
                                        mLiquorName = ((EditText) extra[0]).getText().toString();
                                        mLiquorDescription = ((EditText) extra[1]).getText().toString();

                                        //sets the liquor name into JSONObject
                                        mJSONObjectLiquor.put(Liquor.JSONDB_LIQUOR_NAME, mLiquorName);

                                        //sets the liquor description into JSONObject
                                        mJSONObjectLiquor.put(Liquor.JSONDB_LIQUOR_DESCRIPTION, mLiquorDescription);
                                        MixLiquor.this.mTextView.setText(mLiquorName);
                                        ((MainActivity) getActivity()).CreateLiquor(mJSONObjectLiquor);
                                    }

                                    else
                                    {
                                        Log.e("mLiquorName",mLiquorName);
                                        //sets the liquor name into JSONObject
                                        mJSONObjectLiquor.put(Liquor.JSONDB_LIQUOR_NAME, mLiquorName);

                                        //sets the liquor description into JSONObject
                                        mJSONObjectLiquor.put(Liquor.JSONDB_LIQUOR_DESCRIPTION, mLiquorDescription);
                                    }

                                }
                                //if(mParam.equals(FragmentTags.MixLiquor.getTAG()))
                                //((MainActivity)getActivity()).CreateLiquor(mJSONObjectLiquor);
                                //else if(mParam1.equals(Fragments.UPDATELIQUOR.getTAG()))
                                //  ((MainActivity)getActivity()).UpdateLiquor(mJSONObjectLiquor,id);
                                //RecyclerStaggeredAdapter.mGenerateLiquors.LoadMore();
                                Log.e("JSONADD", mJSONObjectLiquor.toString());
                            } catch (JSONException exp)
                            {
                                Toast.makeText(getActivity(), "Opps Something went wrong", Toast.LENGTH_SHORT).show();
                                exp.printStackTrace();
                            }
                            //break;


                            //Toast.makeText(getActivity(), "Poured", Toast.LENGTH_SHORT).show();
                            break;


                        default:
                            TextView mTextView = (TextView) triggeredView;
                            EditText mEditText = (EditText) extra[0];
                            mTextView.setText(mEditText.getText());
                            if (!MixLiquor.this.getClass().equals(AdjustLiquorVolume.class))
                            {
                                SharedPreferences.Editor editor = ((MainActivity) getActivity()).getSharedPreferences().edit();
                                Bottle b = (Bottle) mTextView.getTag();
                                editor.putString(b.name(), mTextView.getText().toString());
                                editor.commit();
                                mCurrentBottleSettings.put(b, mTextView.getText().toString());
                            } else
                            {
                                Bottle b = (Bottle) mTextView.getTag();
                                mTextView.setTextColor(getResources().getColor(R.color.material_gray));
                                mCurrentBottleSettings.put(b, mTextView.getText().toString());
                            }

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
            // Log.i("Order",mOrder.values()+"");
            mJSONArrayLiquorOrder = new JSONArray(mOrder.values());

        }

        @Override
        public void onStartTrackingTouch(CircularSeekBar seekBar) {
            mSeekBarTextViewValue = mTextViewSeekBarValue.get(seekBar.getTag());
        }
    }
}
