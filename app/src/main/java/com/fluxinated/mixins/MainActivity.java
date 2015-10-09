package com.fluxinated.mixins;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.fluxinated.mixins.adapters.StaggeredRecyclerAdapter;
import com.fluxinated.mixins.database.DB;
import com.fluxinated.mixins.enums.Bottle;
import com.fluxinated.mixins.enums.FragmentTags;
import com.fluxinated.mixins.fragments.AdjustLiquorVolume;
import com.fluxinated.mixins.fragments.LiquorList;
import com.fluxinated.mixins.fragments.MixLiquor;
import com.fluxinated.mixins.loader.ImageLoaderEX;
import com.fluxinated.mixins.model.CardInformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements StaggeredRecyclerAdapter.callbacks,OnFragmentChangeListener
{
    private Bottle[] mBottles;
    private Map<Bottle,String> mCurrentBottleSettings = Collections.synchronizedMap(new LinkedHashMap<Bottle, String>());
    private SharedPreferences mSharedPreferences;
    private DB mDB;
    private ImageLoaderEX mImageLoaderEX;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        mSharedPreferences.edit().apply();
        mBottles = Bottle.values();
        checkCurrentBottle();
        mDB = new DB();
        mImageLoaderEX = new ImageLoaderEX(this);

        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.root_view, LiquorList.getInstance(FragmentTags.LiquorList.getTAG()))
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkCurrentBottle()
    {
        for(Bottle b: mBottles)
        {
            mCurrentBottleSettings.put(b,mSharedPreferences.getString(b.name(),getResources().getString(R.string.liquor_label_default_value)));
        }
    }

    public Map<Bottle, String> getCurrentBottleSettings()
    {
        return mCurrentBottleSettings;
    }

    public SharedPreferences getSharedPreferences()
    {
        return mSharedPreferences;
    }

    public Bottle[] getBottles()
    {
        return mBottles;
    }

    public void CreateLiquor(Object obj)
    {
        mDB.insert(obj);
    }

    public void DeleteLiquor(Object obj)
    {
        mDB.delete(obj);
    }

    public void RetrieveLiquor(int offset, ArrayList<CardInformation> mCardInformation)
    {
        mDB.select(offset,mCardInformation);
    }

   /* public void LoadImage(String ImageURI,String name ,ImageView img)
    {
        mImageLoaderEX.DisplayImage(ImageURI,name,img);
    }*/

    public ImageLoaderEX getImageLoader()
    {
        return mImageLoaderEX;
    }

    public void UpdateLiquor(Object... obj)
    {
        mDB.update(obj);
    }

    @Override
    public void OnRemove(Object obj)
    {
        DeleteLiquor(obj);
    }

    @Override
    public void OnRetrieve(Objects obj)
    {

    }

    @Override
    public void OnFragmentChange(FragmentTags fragment, Object... extra)
    {
            if(fragment == FragmentTags.MixLiquor)
            {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_view, MixLiquor.getInstance(FragmentTags.MixLiquor.getTAG()))
                        .addToBackStack(null).commit();
            }
            else if(fragment == FragmentTags.ADJUSTLIQUOR)
            {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_view, AdjustLiquorVolume.getInstance(FragmentTags.ADJUSTLIQUOR.getTAG(),extra[0]))
                        .addToBackStack(null).commit();
            }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        //if(getSupportFragmentManager().getBackStackEntryCount() > 0)
        //{
            //getSupportFragmentManager().popBackStack();
            //Log.e("pop", "backstack");

          //  return;
        //}
        //finish();
    }
}
