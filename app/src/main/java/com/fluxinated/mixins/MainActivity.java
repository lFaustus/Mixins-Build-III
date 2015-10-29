package com.fluxinated.mixins;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fluxinated.mixins.adapters.StaggeredRecyclerAdapter;
import com.fluxinated.mixins.database.DB;
import com.fluxinated.mixins.database.MyApplication;
import com.fluxinated.mixins.enums.Bottle;
import com.fluxinated.mixins.enums.FragmentTags;
import com.fluxinated.mixins.fragments.AdjustLiquorVolume;
import com.fluxinated.mixins.fragments.BottleSettings;
import com.fluxinated.mixins.fragments.LiquorList;
import com.fluxinated.mixins.fragments.MixLiquor;
import com.fluxinated.mixins.loader.ImageLoaderEX;
import com.fluxinated.mixins.model.CardInformation;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements StaggeredRecyclerAdapter.callbacks,OnFragmentChangeListener,Handler.Callback
{
    private Bottle[] mBottles;
    private Map<Bottle,String> mCurrentBottleSettings = Collections.synchronizedMap(new LinkedHashMap<Bottle, String>());
    private SharedPreferences mSharedPreferences,mSharedPreferencesBluetoothDevice;
    private DB mDB;
    private ImageLoaderEX mImageLoaderEX;
    private BluetoothConnect mBluetoothConnect;
    private Handler mHandler;
    private static SimpleDateFormat mDate;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler(this);
        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        mSharedPreferences.edit().apply();
        mSharedPreferencesBluetoothDevice = getSharedPreferences(getResources().getString(R.string.shared_preference_bluetooth_address),Context.MODE_PRIVATE);
        mSharedPreferencesBluetoothDevice.edit().apply();
        mBottles = Bottle.values();
        checkCurrentBottle();
        mDB = new DB();
        mImageLoaderEX = new ImageLoaderEX(new WeakReference<Context>(this));
        mDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.root_view, LiquorList.getInstance(FragmentTags.LiquorList.getTAG()))
                    .commit();
           // mBluetoothConnect = new BluetoothConnect(this);

           // mBluetoothConnect.execute();
        }
        //else
            //mBluetoothConnect = (BluetoothConnect) getLastCustomNonConfigurationInstance();
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

    protected String getLastConnectedBluetoothDevice()
    {
        return mSharedPreferencesBluetoothDevice.getString(getResources().getString(R.string.shared_preference_bluetooth_address),null);
    }

    public Map<Bottle, String> getCurrentBottleSettings()
    {
        return mCurrentBottleSettings;
    }

    public SharedPreferences getSharedPreferences()
    {
        return mSharedPreferences;
    }

    public SharedPreferences getSharedPreferencesBluetoothDevice()
    {
        return mSharedPreferencesBluetoothDevice;
    }

    public Bottle[] getBottles()
    {
        return mBottles;
    }

    public boolean CreateLiquor(Object... obj)
    {
        return mDB.insert(obj);
    }

    public boolean DeleteLiquor(Object obj)
    {
        return mDB.delete(obj);
    }

    public void RetrieveLiquor(int offset, ArrayList<CardInformation> mCardInformation,String args)
    {
        mDB.select(offset,mCardInformation,args);
    }

   /* public void LoadImage(String ImageURI,String name ,ImageView img)
    {
        mImageLoaderEX.DisplayImage(ImageURI,name,img);
    }*/

    public static String getStringDate()
    {
        return mDate.format(new Date());
    }

    public static SimpleDateFormat getDateFormat()
    {
        return mDate;
    }

    public ImageLoaderEX getImageLoader()
    {
        return mImageLoaderEX;
    }

    public boolean UpdateLiquor(Object... obj)
    {
        return mDB.update(obj);
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

    public void sendMessage(Message msg)
    {
        mHandler.sendMessage(msg);
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
            else if(fragment == FragmentTags.SETTINGS)
            {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_view, BottleSettings.getInstance(FragmentTags.SETTINGS.getTAG()))
                        .addToBackStack(null).commit();
            }
            else if(fragment == FragmentTags.LiquorList)
            {
                if(getSupportFragmentManager().getBackStackEntryCount() <= 0)
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.root_view, LiquorList.getInstance(FragmentTags.LiquorList.getTAG()))
                        .commit();
            }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       // outState.putParcelable("Bluetooth",mBluetoothConnect);
    }


    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mBluetoothConnect;
    }


    @Override
    protected void onStop()
    {
       // mBluetoothConnect.unregisterReceiver();
        super.onStop();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onBackPressed()
    {

        if(getSupportFragmentManager().getBackStackEntryCount()==0) {
           // mBluetoothConnect.offBluetooth();
        }
        super.onBackPressed();

        //if(getSupportFragmentManager().getBackStackEntryCount() > 0)
        //{
            //getSupportFragmentManager().popBackStack();
            //Log.e("pop", "backstack");

          //  return;
        //}
        //finish();

    }


    @Override
    public boolean handleMessage(Message msg) {
        sendData((byte[])msg.obj);
        ByteArrayInputStream bais = new ByteArrayInputStream((byte[])msg.obj);
        DataInputStream in = new DataInputStream(bais);
        try
        {
            while (in.available() > 0) {
                System.out.println(in.available());
                String element = in.readUTF();
                System.out.print(element);
                Log.i("message",element);

            }
            in.close();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    private void sendData(byte[] msgBuffer)
    {
        try
        {
            mBluetoothConnect.getOutputStream().write(msgBuffer);
            mBluetoothConnect.new DispenseTask(MainActivity.this).execute();
            mBluetoothConnect.getOutputStream().flush();
        } catch (IOException e)
        {
            String msg = "In onResume() and an exception occurred during write: "
                    + e.getMessage();
            if (getLastConnectedBluetoothDevice().equals("00:00:00:00:00:00"))
                msg = msg
                        + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
            msg = msg + ".\n\nCheck that the SPP UUID: " + BluetoothConnect.MY_UUID.toString()
                    + " exists on server.\n\n";

            errorExit("Fatal Error", msg);
        }
    }

    private void errorExit(String title, String message)
    {
        Toast.makeText(MyApplication.getAppContext(), title + " - " + message,
                Toast.LENGTH_LONG).show();
        finish();
    }
}
