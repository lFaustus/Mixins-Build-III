package com.fluxinated.mixins.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fluxinated.mixins.R;
import com.fluxinated.mixins.database.GenerateTiles;
import com.fluxinated.mixins.loader.ImageLoaderEX;
import com.fluxinated.mixins.model.CardInformation;
import com.fluxinated.mixins.model.Liquor;
import com.fluxinated.mixins.viewholders.BaseViewHolder;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by User on 08/10/2015.
 */
public class StaggeredRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder>
{

    private callbacks mCaller;
    private ArrayList<CardInformation> mCardInformation;
    private ImageLoaderEX mImageLoader;
    private DisplayMetrics windowMetrics;
    private static GenerateTiles mGenerateTiles;

    public StaggeredRecyclerAdapter()
    {
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if(windowMetrics == null)
        windowMetrics = parent.getContext().getResources().getDisplayMetrics();
        return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.staggered_items,parent,false),this);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position)
    {
        Liquor mLiquor =  (Liquor) mCardInformation.get(position).getLiquor();
        try
        {
            mImageLoader.DisplayImage(mLiquor.getLiquorPictureURI(),mLiquor.getLiquorName(),holder.img);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        if (mCardInformation.get(position).getTileType() == GenerateTiles.SMALL_TILE) // small tile view
        {
            ViewGroup.LayoutParams layoutparams = holder.Tile.getLayoutParams();
            layoutparams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, windowMetrics);
            layoutparams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, windowMetrics);
            holder.Tile.setLayoutParams(layoutparams);
            StaggeredGridLayoutManager.LayoutParams layoutParams1 = ((StaggeredGridLayoutManager.LayoutParams) holder.Tile.getLayoutParams());
            layoutParams1.setFullSpan(false);
        }
        else if (mCardInformation.get(position).getTileType() == GenerateTiles.BIG_TILE)// big tile view
        {
            ViewGroup.LayoutParams layoutparams = holder.Tile.getLayoutParams();
            layoutparams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, windowMetrics.heightPixels, windowMetrics);
            layoutparams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 500, windowMetrics);
            holder.Tile.setLayoutParams(layoutparams);
            StaggeredGridLayoutManager.LayoutParams layoutParams1 = ((StaggeredGridLayoutManager.LayoutParams) holder.Tile.getLayoutParams());
            layoutParams1.setFullSpan(true);
        }
        else //long tile view
        {
            ViewGroup.LayoutParams layoutparams = holder.Tile.getLayoutParams();
            layoutparams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, windowMetrics);
            layoutparams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600, windowMetrics);
            holder.Tile.setLayoutParams(layoutparams);
            StaggeredGridLayoutManager.LayoutParams layoutParams1 = ((StaggeredGridLayoutManager.LayoutParams) holder.Tile.getLayoutParams());
            layoutParams1.setFullSpan(false);
        }

        try
        {
            holder.txtview.setText(mLiquor.getLiquorName());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        holder.Tile_label.setBackgroundColor(Color.parseColor(mCardInformation.get(position).getTileColor()));
        mCardInformation.get(position).setCardPosition(position);
        holder.Tile.setTag(mCardInformation.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mCardInformation.size();
    }

    public void setListener(callbacks mCaller)
    {
        this.mCaller = mCaller;
    }

    public void setList(ArrayList<CardInformation> mCardInformation)
    {
        this.mCardInformation = mCardInformation;
    }

    public void setImageLoader(ImageLoaderEX mImageLoader)
    {
        this.mImageLoader = mImageLoader;
    }

   /* class StaggeredViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        ImageView img;
        TextView txtview,txtviewModeIndicator;
        CardView Tile;
        FrameLayout Tile_label;

        public StaggeredViewHolder(View itemView)
        {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.imageLiquor);
            txtview = (TextView) itemView.findViewById(R.id.textLiquor);
            Tile = (CardView) itemView.findViewById(R.id.card_view);
            Tile_label = (FrameLayout) itemView.findViewById(R.id.tile_label);
            txtviewModeIndicator = (TextView)itemView.findViewById(R.id.edit_mode_tag);
        }

        @Override
        public void onClick(View v)
        {

        }
    }*/
   public callbacks getCallback()
   {
        return mCaller;
   }
   public interface callbacks
    {
        void OnRemove(Object obj);
        void OnRetrieve(Objects obj);
    }
}
