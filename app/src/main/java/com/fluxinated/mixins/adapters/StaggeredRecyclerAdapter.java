package com.fluxinated.mixins.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fluxinated.mixins.MainActivity;
import com.fluxinated.mixins.R;
import com.fluxinated.mixins.database.GenerateTiles;
import com.fluxinated.mixins.loader.ImageLoaderEX;
import com.fluxinated.mixins.model.CardInformation;
import com.fluxinated.mixins.model.Liquor;
import com.fluxinated.mixins.viewholders.BaseViewHolder;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
    private long mDateDifference;
    private static int RibbonDuration = 4;


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
        WeakReference<Liquor> mLiquor = new WeakReference<>(mCardInformation.get(position).getLiquor());
        try
        {
            mImageLoader.DisplayImage(mLiquor.get().getLiquorPictureURI(),mLiquor.get().getLiquorName(),holder.img);
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
            holder.txtview.setText(mLiquor.get().getLiquorName());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        holder.Tile_label.setBackgroundColor(Color.parseColor(mCardInformation.get(position).getTileColor()));

        mCardInformation.get(position).setCardPosition(position);
        holder.Tile.setTag(mCardInformation.get(position));

        try
        {
            Date mCurrentDate = MainActivity.getDateFormat().parse(MainActivity.getStringDate());
            Date mLiquorDateAdded = MainActivity.getDateFormat().parse(mLiquor.get().getDateAdded());
            mDateDifference = ((mCurrentDate.getTime() - mLiquorDateAdded.getTime()) / (1000 * 60 * 60 * 24));
            //Log.e("date diff","mCurrentDate " + mCurrentDate.get().getTime() +" - mLiquorDate "+mLiquorDateAdded.get().getTime() );
        } catch (ParseException e)
        {
            e.printStackTrace();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        if(mDateDifference > RibbonDuration)
            holder.txtviewModeIndicator.setVisibility(View.GONE);
        else
            holder.txtviewModeIndicator.setVisibility(View.VISIBLE);


    }

    @Override
    public void onViewRecycled(BaseViewHolder holder)
    {
        super.onViewRecycled(holder);
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
