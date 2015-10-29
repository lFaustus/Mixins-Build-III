package com.fluxinated.mixins.viewholders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fluxinated.mixins.MainActivity;
import com.fluxinated.mixins.R;
import com.fluxinated.mixins.adapters.StaggeredRecyclerAdapter;
import com.fluxinated.mixins.database.MyApplication;
import com.fluxinated.mixins.enums.FragmentTags;
import com.fluxinated.mixins.model.CardInformation;
import com.fluxinated.mixins.model.Liquor;

import org.json.JSONException;

import java.lang.ref.WeakReference;

/**
 * Created by User on 09/10/2015.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    private final StaggeredRecyclerAdapter mStaggeredRecyclerAdapter;
    public ImageView img;
    public TextView txtview, txtviewModeIndicator;
    public CardView Tile;
    public FrameLayout Tile_label;
    protected AlertDialog.Builder mDialog = null;
    private WeakReference<Activity> mActivity;

    public BaseViewHolder(View itemView,StaggeredRecyclerAdapter staggeredRecyclerAdapter)
    {
        super(itemView);
        img = (ImageView) itemView.findViewById(R.id.imageLiquor);
        txtview = (TextView) itemView.findViewById(R.id.textLiquor);
        Tile = (CardView) itemView.findViewById(R.id.card_view);
        Tile_label = (FrameLayout) itemView.findViewById(R.id.tile_label);
        txtviewModeIndicator = (TextView) itemView.findViewById(R.id.edit_mode_tag);
        mStaggeredRecyclerAdapter = staggeredRecyclerAdapter;
        mActivity = new WeakReference<Activity>((MainActivity)staggeredRecyclerAdapter.getCallback());

        Tile.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.card_view:
                View view = (LayoutInflater.from(MyApplication.getAppContext()).inflate(R.layout.liquor_information_dialog_big, null));
                TextView mTextView_name = (TextView)view.findViewById(R.id.liquor_name),
                mTextView_description = (TextView)view.findViewById(R.id.liquor_description);
                ImageView mImageView_picture = (ImageView)view.findViewById(R.id.info_picture);
                openDialog(v,view,"Adjust Mixture Volume","Pour",null,mTextView_name,mTextView_description,mImageView_picture);
                break;
        }
    }

    private void openDialog(View triggeredView,View layout,String button1_label,String button2_label,String message,View... extra)
    {
        WeakReference<CardInformation> mCardInformation = new WeakReference<>((CardInformation) triggeredView.getTag());
        if(mDialog == null)
        {
            mDialog = new AlertDialog.Builder(triggeredView.getContext());
            AlertDialog al = mDialog.create();
            al.setView(layout);
            al.setCancelable(true);
            try
            {
                ((TextView) extra[0]).setText(mCardInformation.get().getLiquor().getLiquorName());
                ((TextView) extra[1]).setText(mCardInformation.get().getLiquor().getLiquorDescription());
                ((MainActivity)mActivity.get()).getImageLoader().DisplayImage(mCardInformation.get().getLiquor().getLiquorPictureURI(),mCardInformation.get().getLiquor().getLiquorName(),(ImageView) extra[2]);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }


            if(message != null)
                al.setMessage(message);


            al.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    mDialog = null;
                    dialog.dismiss();
                }
            });


            al.setButton(DialogInterface.BUTTON_NEGATIVE, "Remove Cocktail", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    mDialog = null;
                    //((MainActivity) mActivity.get()).OnFragmentChange(FragmentTags.ADJUSTLIQUOR, mCardInformation.get());
                    mStaggeredRecyclerAdapter.notifyItemRemoved(mCardInformation.get().getCardPosition());

                    if(((MainActivity) mActivity.get()).DeleteLiquor(mCardInformation.get().getLiquor().getLiquorId()))


                        try
                        {
                            Toast.makeText(MyApplication.getAppContext(),mCardInformation.get().getLiquor().getLiquorName().toString() + " has successfully removed from menu", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    else
                        Toast.makeText(MyApplication.getAppContext(),"Failed to remove cocktail, Please try again", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                }
            });

            al.setButton(DialogInterface.BUTTON_NEUTRAL, button1_label, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    mDialog = null;
                    ((MainActivity)mActivity.get()).OnFragmentChange(FragmentTags.ADJUSTLIQUOR,mCardInformation.get());
                    dialog.dismiss();
                }
            });
            al.setButton(DialogInterface.BUTTON_POSITIVE, button2_label, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    mDialog = null;
                    // CardInformation mCardInformation = (CardInformation)triggeredView.getTag();
                    Liquor mLiquor = mCardInformation.get().getLiquor();
                    Log.e("Pour", mLiquor.getLiquorOrder().toString());


                    /*Message msg = Message.obtain();
                    JSONArray arr = null;
                    ByteArrayOutputStream outstream = null;
                    DataOutputStream dataOutstream = null;
                    try {
                        arr = mLiquor.getLiquorOrder();
                        outstream = new ByteArrayOutputStream();
                        dataOutstream = new DataOutputStream(outstream);

                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                dataOutstream.writeUTF(arr.get(i).toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        msg.obj = outstream.toByteArray();
                        ((MainActivity)mActivity).sendMessage(msg);
                        outstream.close();
                        dataOutstream.close();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        arr = null;
                        outstream = null;
                        dataOutstream = null;
                        msg = null;
                    }*/
                    Toast.makeText(MyApplication.getAppContext(), mLiquor.getLiquorOrder().toString(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            al.show();
            /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(al.getWindow().getAttributes());
            lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, mActivity.getResources().getDisplayMetrics());
            al.getWindow().setAttributes(lp);*/

        }
    }
}
