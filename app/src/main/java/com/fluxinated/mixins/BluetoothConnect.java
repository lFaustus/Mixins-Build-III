package com.fluxinated.mixins;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.fluxinated.mixins.database.MyApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by proxmaccoy25 on 10/16/2015.
 */
public class BluetoothConnect implements Parcelable {

    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter = null;
    private OutputStream outStream = null;
    private InputStream inputStream = null;
    private BluetoothSocket btSocket = null;
    private static BluetoothConnect mBluetoothConnect;
    private ArrayList<BluetoothDevice> mDetectedDevices;
    private static final String TAG = "BlueToothConnect";

    public static BluetoothConnect getInstance()
    {
        if(mBluetoothConnect == null)
            mBluetoothConnect = new BluetoothConnect();
        return mBluetoothConnect;
    }

    protected BluetoothConnect(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mDetectedDevices = new ArrayList<>();
        try {
            outStream = btSocket.getOutputStream();
            inputStream = btSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
            throws IOException
    {
        if (Build.VERSION.SDK_INT >= 10)
        {
            try
            {
                final Method m = device.getClass().getMethod(
                        "createInsecureRfcommSocketToServiceRecord",
                        new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e)
            {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    public boolean removeBond(BluetoothDevice btDevice)
            throws Exception
    {
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


    public boolean createBond(BluetoothDevice btDevice)
            throws Exception
    {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    private void startSearching() {
        Log.i("Log", "in the start searching method");
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        MyApplication.getAppContext().registerReceiver(myReceiver, intentFilter);
        bluetoothAdapter.startDiscovery();
    }

    private void onBluetooth() {
        if(!bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.enable();
            Log.i("Log", "Bluetooth is Enabled");
        }
    }
    private void offBluetooth() {
        if(bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.disable();
        }
    }



    private void makeDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        MyApplication.getAppContext().startActivity(discoverableIntent);
        Log.i("Log", "Discoverable ");
    }


    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           // Message msg = Message.obtain();
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                Toast.makeText(context, "ACTION_FOUND", Toast.LENGTH_SHORT).show();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try
                {
                    //device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
                    //device.getClass().getMethod("cancelPairingUserInput", boolean.class).invoke(device);
                }
                catch (Exception e) {
                    Log.i("Log", "Inside the exception: ");
                    e.printStackTrace();
                }

                if(mDetectedDevices.size() == 0) // this checks if the size of bluetooth device is 0,then add the
                {                                           // device to the arraylist.
                    /*detectedAdapter.add(device.getName()+"\n"+device.getAddress());
                    arrayListBluetoothDevices.add(device);
                    detectedAdapter.notifyDataSetChanged();*/
                    mDetectedDevices.add(device);
                }
                else
                {
                    boolean flag = true;    // flag to indicate that particular device is already in the arlist or not
                    for(int i = 0; i<mDetectedDevices.size();i++)
                    {
                        if(device.getAddress().equals(mDetectedDevices.get(i).getAddress()))
                        {
                            flag = false;
                        }
                    }
                    if(flag == true)
                    {
                        //detectedAdapter.add(device.getName()+"\n"+device.getAddress());
                        mDetectedDevices.add(device);
                        //detectedAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

    };




    protected BluetoothConnect(Parcel in) {
    }

    public static final Creator<BluetoothConnect> CREATOR = new Creator<BluetoothConnect>() {
        @Override
        public BluetoothConnect createFromParcel(Parcel in) {
            return new BluetoothConnect(in);
        }

        @Override
        public BluetoothConnect[] newArray(int size) {
            return new BluetoothConnect[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
