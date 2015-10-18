package com.fluxinated.mixins;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fluxinated.mixins.database.MyApplication;
import com.fluxinated.mixins.enums.FragmentTags;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Created by proxmaccoy25 on 10/16/2015.
 */
public class BluetoothConnect implements Parcelable
{

    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket mBluetoothSocket = null;
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;
    private ArrayList<BluetoothDevice> mBluetoothDetectedDevices,mBluetoothPairedDevices;
    private ArrayList<String>mBluetoothDetectedDevicesListViewItems,mBluetoothPairedDevicesListViewItems;
    private ArrayAdapter<String> mBluetoothDetectedDevicesAdapter,mBluetoothPairedDevicesAdapter;
    private Activity mActivity;
    private static final String TAG = "BlueToothConnect";
    private boolean hasFailedToConnectMustRetry = false;
    private AlertDialog.Builder mAlertDialog = null;
    private ListView mListViewDetectedDevices,mListViewPairedDevices;
    private AdapterView.OnItemClickListener mListViewItemClickedOnDetectedDevices,mListViewItemClickedOnPairedDevices;
    private View mBluetoothDevicesLayout;


    protected BluetoothConnect(Activity activity)
    {
        mActivity = activity;
        mBluetoothDevicesLayout = LayoutInflater.from(mActivity).inflate(R.layout.list_bluetoothdevices, null);
        mListViewDetectedDevices = (ListView)mBluetoothDevicesLayout.findViewById(R.id.list_available_devices);
        mListViewPairedDevices = (ListView)mBluetoothDevicesLayout.findViewById(R.id.list_paired_devices);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothDetectedDevices = new ArrayList<>();
        mBluetoothPairedDevices = new ArrayList<>();
        mBluetoothDetectedDevicesListViewItems = new ArrayList<>();
        mBluetoothPairedDevicesListViewItems = new ArrayList<>();
        mBluetoothDetectedDevicesAdapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_list_item_1, mBluetoothPairedDevicesListViewItems);
        mBluetoothPairedDevicesAdapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_list_item_1,mBluetoothPairedDevicesListViewItems);
        mListViewDetectedDevices.setAdapter(mBluetoothDetectedDevicesAdapter);
        mListViewPairedDevices.setAdapter(mBluetoothPairedDevicesAdapter);


        mListViewItemClickedOnDetectedDevices = new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                BluetoothDevice mBluetoothDevice = mBluetoothDetectedDevices.get(position);
                Boolean isBonded = false;
                try {
                  isBonded = createBond(mBluetoothDevice);
                    if(isBonded)
                    {
                        //arrayListpaired.add(bdDevice.getName()+"\n"+bdDevice.getAddress());
                        //adapter.notifyDataSetChanged();
                        getPairedDevices();
                        //mBluetoothPairedDevicesAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }//connect(bdDevice);
                Log.i("Log", "The bond is created: " + isBonded);
            }


        };

        mListViewItemClickedOnPairedDevices = new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                BluetoothDevice mDevice = mBluetoothPairedDevices.get(position);
                try {
                    Boolean removeBonding = removeBond(mDevice);
                    if(removeBonding)
                    {
                       // mBluetoothPairedDevicesListViewItems.remove(position);
                       // mBluetoothPairedDevicesAdapter.notifyDataSetChanged();
                        ;
                    }


                    Log.i("Log", "Removed"+removeBonding);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };

        /*try {
            mOutputStream = mBluetoothSocket.getOutputStream();
            mInputStream = mBluetoothSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    private void getPairedDevices()
    {
        mBluetoothPairedDevicesListViewItems = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            Iterator<BluetoothDevice> bluetoothDevice = pairedDevices.iterator();
            while(bluetoothDevice.hasNext())
            {
                BluetoothDevice mDevice = bluetoothDevice.next();
                mBluetoothPairedDevices.add(mDevice);
                mBluetoothPairedDevicesListViewItems.add(mDevice.getName() + "\n" + mDevice.getAddress());
            }
        }
        mBluetoothPairedDevicesAdapter.notifyDataSetChanged();
    }

    /*private void createBluetoothSocket(BluetoothDevice device)
            throws IOException
    {
        if (Build.VERSION.SDK_INT >= 10)
        {
            try
            {
                final Method m = device.getClass().getMethod(
                        "createInsecureRfcommSocketToServiceRecord",
                        UUID.class);
                mBluetoothSocket = (BluetoothSocket) m.invoke(device, MY_UUID);

            } catch (Exception e)
            {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        if (mBluetoothSocket == null)
            mBluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);

        mOutputStream = mBluetoothSocket.getOutputStream();
        mInputStream = mBluetoothSocket.getInputStream();
    }*/

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
            throws IOException
    {

        if (Build.VERSION.SDK_INT >= 10)
        {
            try
            {
                final Method m = device.getClass().getMethod(
                        "createInsecureRfcommSocketToServiceRecord",
                        UUID.class);
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

    private void startSearching()
    {
        Log.i("Log", "in the start searching method");
        mBluetoothDetectedDevices = new ArrayList<>();
        getPairedDevices();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        MyApplication.getAppContext().registerReceiver(myReceiver, intentFilter);
        mBluetoothAdapter.startDiscovery();
    }

    private void onBluetooth()
    {
        if (!mBluetoothAdapter.isEnabled())
        {
            mBluetoothAdapter.enable();
            Log.i("Log", "Bluetooth is Enabled");
        }
    }

    private void offBluetooth()
    {
        if (mBluetoothAdapter.isEnabled())
        {
            mBluetoothAdapter.disable();
        }
    }

    public String getRecentConnectedDevice()
    {
        return ((MainActivity) mActivity).getLastConnectedBluetoothDevice();
    }


    private void makeDiscoverable()
    {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        MyApplication.getAppContext().startActivity(discoverableIntent);
        Log.i("Log", "Discoverable ");
    }


    private BroadcastReceiver myReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // Message msg = Message.obtain();
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
            {
                Toast.makeText(context, "ACTION_FOUND_STATE_CHANGED", Toast.LENGTH_SHORT).show();
                if (device.getBondState() == BluetoothDevice.BOND_BONDED)
                {

                    if (Connect(device))
                    {
                        //hasFailedToConnectMustRetry = false;
                        SharedPreferences.Editor editor = ((MainActivity) mActivity).getSharedPreferencesBluetoothDevice().edit();
                        editor.putString(mActivity.getResources().getString(R.string.shared_preference_bluetooth_address), device.getAddress());
                        editor.commit();
                        mBluetoothDetectedDevices = null;
                        ((MainActivity) mActivity).OnFragmentChange(FragmentTags.LiquorList, null);
                    } else
                    {
                        //hasFailedToConnectMustRetry = true;
                        abortBroadcast();
                        MyApplication.getAppContext().unregisterReceiver(this);
                        startSearching();
                    }
                }
            } else if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                Toast.makeText(context, "ACTION_FOUND", Toast.LENGTH_SHORT).show();
                if (device.getBondState() == BluetoothDevice.BOND_BONDED && device.getAddress().equals(getRecentConnectedDevice().equals(device.getAddress())) && !hasFailedToConnectMustRetry)
                {

                    if (!Connect(device))
                    {
                        hasFailedToConnectMustRetry = true;
                        abortBroadcast();
                        MyApplication.getAppContext().unregisterReceiver(this);
                        startSearching();
                    }
                    else
                    {
                        hasFailedToConnectMustRetry = false;
                        mBluetoothDetectedDevices = null;
                        ((MainActivity) mActivity).OnFragmentChange(FragmentTags.LiquorList, null);
                    }
                } else
                {

                    if (!mBluetoothDetectedDevices.contains(device.getAddress()))
                    {
                        mBluetoothDetectedDevices.add(device);
                    }
                }
            }
        }

    };

    private boolean Connect(BluetoothDevice device)
    {
        try
        {
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket = createBluetoothSocket(device);
            mBluetoothSocket.connect();
            Log.i("socket connect", "Connection success");
            mOutputStream = mBluetoothSocket.getOutputStream();
            mInputStream = mBluetoothSocket.getInputStream();
            return true;
        } catch (Exception e)
        {
            try
            {
                mBluetoothSocket.close();
                Log.i("socket close", "trying to close socket after connection failure");
            } catch (Exception e2)
            {
                Log.e("Fatal Error", "In onResume() and unable to close socket during connection failure"
                        + e2.getMessage() + ".");
            } finally
            {
                return false;
                //new dialog with list of paired and detected devices
            }
        }
    }

    private void ListBluetoothDevices()
    {
        if(mAlertDialog == null)
        {
            mAlertDialog = new AlertDialog.Builder(mActivity);
            AlertDialog al = mAlertDialog.create();

            al.setView(mBluetoothDevicesLayout);
            al.setCancelable(false);

        }
    }


    protected BluetoothConnect(Parcel in)
    {
        mBluetoothAdapter = (BluetoothAdapter) in.readValue(ClassLoader.getSystemClassLoader());
        mBluetoothSocket = (BluetoothSocket) in.readValue(ClassLoader.getSystemClassLoader());
        mOutputStream = (OutputStream) in.readValue(ClassLoader.getSystemClassLoader());
        mInputStream = (InputStream) in.readValue(ClassLoader.getSystemClassLoader());
        in.readTypedList(mBluetoothDetectedDevices, BluetoothDevice.CREATOR);
        in.readTypedList(mBluetoothPairedDevices,BluetoothDevice.CREATOR);
    }

    public static final Creator<BluetoothConnect> CREATOR = new Creator<BluetoothConnect>()
    {
        @Override
        public BluetoothConnect createFromParcel(Parcel in)
        {
            return new BluetoothConnect(in);
        }

        @Override
        public BluetoothConnect[] newArray(int size)
        {
            return new BluetoothConnect[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeValue(mBluetoothAdapter);
        dest.writeValue(mBluetoothSocket);
        dest.writeValue(mOutputStream);
        dest.writeValue(mInputStream);
        dest.writeTypedList(mBluetoothDetectedDevices);
        dest.writeTypedList(mBluetoothPairedDevices);
    }



}
