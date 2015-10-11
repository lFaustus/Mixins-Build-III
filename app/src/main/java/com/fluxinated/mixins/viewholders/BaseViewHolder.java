package com.fluxinated.mixins.viewholders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fluxinated.mixins.MainActivity;
import com.fluxinated.mixins.R;
import com.fluxinated.mixins.adapters.StaggeredRecyclerAdapter;
import com.fluxinated.mixins.enums.FragmentTags;
import com.fluxinated.mixins.model.CardInformation;
import com.fluxinated.mixins.model.Liquor;

import org.json.JSONException;

/**
 * Created by User on 09/10/2015.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    public ImageView img;
    public TextView txtview, txtviewModeIndicator;
    public CardView Tile;
    public FrameLayout Tile_label;
    private StaggeredRecyclerAdapter mStaggeredRecyclerAdapter;
    protected AlertDialog.Builder mDialog = null;
    private Activity mActivity;

    public BaseViewHolder(View itemView,StaggeredRecyclerAdapter staggeredRecyclerAdapter)
    {
        super(itemView);
        img = (ImageView) itemView.findViewById(R.id.imageLiquor);
        txtview = (TextView) itemView.findViewById(R.id.textLiquor);
        Tile = (CardView) itemView.findViewById(R.id.card_view);
        Tile_label = (FrameLayout) itemView.findViewById(R.id.tile_label);
        txtviewModeIndicator = (TextView) itemView.findViewById(R.id.edit_mode_tag);
        mStaggeredRecyclerAdapter = staggeredRecyclerAdapter;
        mActivity = (MainActivity)mStaggeredRecyclerAdapter.getCallback();

        Tile.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.card_view:
                View view = LayoutInflater.from(mActivity).inflate(R.layout.liquor_information_dialog_big, null);
                TextView mTextView_name = (TextView)view.findViewById(R.id.liquor_name),
                mTextView_description = (TextView)view.findViewById(R.id.liquor_description);
                ImageView mImageView_picture = (ImageView)view.findViewById(R.id.info_picture);
                openDialog(v,view,"Adjust Mixture Volume","Pour",null,mTextView_name,mTextView_description,mImageView_picture);
                break;
        }
    }

    private void openDialog(View triggeredView,View layout,String button1_label,String button2_label,String message,View... extra)
    {
        if(mDialog == null)
        {
            mDialog = new AlertDialog.Builder(mActivity.getWindow().getContext());
            AlertDialog al = mDialog.create();
            //al.setView(LayoutInflater.from(getActivity()).inflate(R.layout.liquor_information_dialog_big,null));
            al.setView(layout);
            al.setCancelable(true);

            /*
             *Setting up values
             */
            CardInformation mCardInformation = (CardInformation)triggeredView.getTag();
            //Log.e("Array",mCardInformation.getLiquor().getLiquorOrder().toString());
            try
            {
                ((TextView) extra[0]).setText(mCardInformation.getLiquor().getLiquorName());
                ((TextView) extra[1]).setText(mCardInformation.getLiquor().getLiquorDescription());
                ((MainActivity)mActivity).getImageLoader().DisplayImage(mCardInformation.getLiquor().getLiquorPictureURI(),mCardInformation.getLiquor().getLiquorName(),(ImageView) extra[2]);
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

            al.setButton(DialogInterface.BUTTON_NEUTRAL, button1_label, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    mDialog = null;
                    ((MainActivity)mActivity).OnFragmentChange(FragmentTags.ADJUSTLIQUOR,mCardInformation);
                    dialog.dismiss();
                }
            });
            al.setButton(DialogInterface.BUTTON_POSITIVE, button2_label, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    mDialog = null;
                    CardInformation mCardInformation = (CardInformation)triggeredView.getTag();
                    Liquor mLiquor = mCardInformation.getLiquor();
                    Log.e("Pour",mLiquor.getLiquorOrder().toString());
                    dialog.dismiss();
                }
            });
            al.show();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(al.getWindow().getAttributes());
            lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, mActivity.getResources().getDisplayMetrics());
            al.getWindow().setAttributes(lp);

        }
    }
}
