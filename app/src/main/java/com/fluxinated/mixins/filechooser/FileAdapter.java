package com.fluxinated.mixins.filechooser;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fluxinated.mixins.R;
import com.fluxinated.mixins.loader.ImageLoaderEX;

import java.util.List;


//import com.maurycyw.lazylist.staggeredgridviewlib.loader.ImageLoader;

public class FileAdapter extends ArrayAdapter<Items>
{
	private static Context context;
	private int layoutid;
	private List<Items> mItems;
	ImageLoaderEX mImageLoader;
	private int scroll_State = -1;
	private final int SCROLL_STATE_ONLOAD = -1;
	public FileAdapter(Context context, int resource, List<Items> objects)
	{
		super(context, resource, objects);
		FileAdapter.context = context;
		mItems = objects;
		layoutid = resource;
		mImageLoader = new ImageLoaderEX(context);
	}
	
	public void scrollState(int state)
	{
		this.scroll_State = state;
	}
	
	public int getScrollState()
	{
		return this.scroll_State;
	}
	
	@Override
	public Items getItem(int position)
	{
		return mItems.get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		ViewHolder viewholder;
		
		final Items item = mItems.get(position);
			if(view == null)
			{
				view = LayoutInflater.from(context).inflate(layoutid, parent,false);
				viewholder = new ViewHolder();
				
				//viewholder.customFont = Typeface.createFromAsset(context.getAssets(), "danielabold.ttf");
				viewholder.imgview = (ImageView) view.findViewById(R.id.imageView1_newDrink);
				viewholder.txtname = (TextView) view.findViewById(R.id.imgname);
				viewholder.txtpath = (TextView) view.findViewById(R.id.imgpath);
				view.setTag(viewholder);
			}
			else
				viewholder = (ViewHolder)view.getTag();
			
			viewholder.txtname.setText(item.getName());
			viewholder.txtpath.setText(item.getImgPath());
			
			if(getScrollState() == AbsListView.OnScrollListener.SCROLL_STATE_IDLE || getScrollState() == SCROLL_STATE_ONLOAD)

				//viewholder.imgview.setImageBitmap(BitmapFactory.decodeFile(item.getImgPath()));
				//mImageLoader.DisplayImage(item.getImgPath(), viewholder.imgview, item.getName());
				mImageLoader.DisplayImage(item.getImgPath(), item.getName(), viewholder.imgview);
			
		return view;
	}
	

	static class ViewHolder
	{
		ImageView imgview;
		TextView txtname,txtpath;
		Typeface customFont;
	}

}
