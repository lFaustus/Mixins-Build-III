package com.fluxinated.mixins;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fluxinated.mixins.database.MyApplication;
import com.fluxinated.mixins.enums.FragmentTags;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Created by proxmaccoy25 on 10/16/2015.
 */
public class BluetoothConnect {

    public static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket mBluetoothSocket = null;
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;
    private ArrayList<BluetoothDevice> mBluetoothDetectedDevices, mBluetoothPairedDevices;
    private ArrayList<String> mBluetoothDetectedDevicesListViewItems, mBluetoothPairedDevicesListViewItems;
    private ArrayAdapter<String> mBluetoothDetectedDevicesAdapter, mBluetoothPairedDevicesAdapter;
    private Activity mActivity;
    private static final String TAG = "BlueToothConnect";
    private boolean hasFailedToConnectMustRetry = false;
    private AlertDialog.Builder mAlertDialog = null;
    private ListView mListViewDetectedDevices, mListViewPairedDevices;
    private AdapterView.OnItemClickListener mListViewItemClickedOnDetectedDevices, mListViewItemClickedOnPairedDevices;
    private View mBluetoothDevicesLayout;
    private LinearLayout mBluetoothStatusView, mListViews;
    private TextView mBluetoothStatusText;
    private AlertDialog al;
    private connectTask mConnectTask;



    protected BluetoothConnect(WeakReference<Activity> activity) {
        mActivity = activity.get();
        mBluetoothDevicesLayout = LayoutInflater.from(MyApplication.getAppContext()).inflate(R.layout.list_bluetoothdevices, null);
        mListViewDetectedDevices = (ListView) mBluetoothDevicesLayout.findViewById(R.id.list_available_devices);
        mListViewPairedDevices = (ListView) mBluetoothDevicesLayout.findViewById(R.id.list_paired_devices);
        mBluetoothStatusView = (LinearLayout) mBluetoothDevicesLayout.findViewById(R.id.bluetooth_status_view);
        mListViews = (LinearLayout) mBluetoothDevicesLayout.findViewById(R.id.listviews);
        mBluetoothStatusText = (TextView) mBluetoothDevicesLayout.findViewById(R.id.bluetooth_status_text);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothDetectedDevices = new ArrayList<>();
        mBluetoothPairedDevices = new ArrayList<>();
        mBluetoothDetectedDevicesListViewItems = new ArrayList<>();
        mBluetoothPairedDevicesListViewItems = new ArrayList<>();
        mBluetoothDetectedDevicesAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, mBluetoothDetectedDevicesListViewItems);
        mBluetoothPairedDevicesAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, mBluetoothPairedDevicesListViewItems);
        mListViewDetectedDevices.setAdapter(mBluetoothDetectedDevicesAdapter);
        mListViewPairedDevices.setAdapter(mBluetoothPairedDevicesAdapter);


        mListViewItemClickedOnDetectedDevices = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice mBluetoothDevice = mBluetoothDetectedDevices.get(position);
                Boolean isBonded = false;
                try {
                    isBonded = createBond(mBluetoothDevice);
                    if (isBonded) {
                        //arrayListpaired.add(bdDevice.getName()+"\n"+bdDevice.getAddress());
                        //adapter.notifyDataSetChanged();
                        Toast.makeText(MyApplication.getAppContext(), "Pairing Success", Toast.LENGTH_SHORT);
                        //getPairedDevices();
                        //mBluetoothPairedDevicesAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }//connect(bdDevice);
                Log.i("Log", "The bond is created: " + isBonded);
            }


        };

        mListViewItemClickedOnPairedDevices = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice mDevice = mBluetoothPairedDevices.get(position);
                try {
                    Boolean removeBonding = removeBond(mDevice);
                    if (removeBonding) {
                        mBluetoothPairedDevicesListViewItems.remove(position);
                        mBluetoothPairedDevicesAdapter.notifyDataSetChanged();
                        Toast.makeText(MyApplication.getAppContext(), "Unpaired success", Toast.LENGTH_SHORT).show();
                    }


                    Log.i("Log", "Removed" + removeBonding);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };

        mListViewDetectedDevices.setOnItemClickListener(mListViewItemClickedOnDetectedDevices);
        mListViewPairedDevices.setOnItemClickListener(mListViewItemClickedOnPairedDevices);
        Start();
        /*try {
            mOutputStream = mBluetoothSocket.getOutputStream();
            mInputStream = mBluetoothSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    private void getPairedDevices() {
        mBluetoothPairedDevicesListViewItems = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            Iterator<BluetoothDevice> bluetoothDevice = pairedDevices.iterator();
            while (bluetoothDevice.hasNext()) {
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

    public void Start() {
        onBluetooth();
        startSearching();

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
            throws IOException {

        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod(
                        "createInsecureRfcommSocketToServiceRecord",
                        UUID.class);
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    public boolean removeBond(BluetoothDevice btDevice)
            throws Exception {
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


    public boolean createBond(BluetoothDevice btDevice)
            throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    private void startSearching() {
        Log.i("Log", "in the start searching method");
        mBluetoothDetectedDevices = new ArrayList<>();
        getPairedDevices();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
       intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        mActivity.registerReceiver(myReceiver, intentFilter);
        mBluetoothAdapter.startDiscovery();
    }

    private void onBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
            Log.i("Log", "Bluetooth is Enabled");
        }
    }

    public void offBluetooth() {
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    public String getRecentConnectedDevice() {
        return ((MainActivity) mActivity).getLastConnectedBluetoothDevice();
    }


    private void makeDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        mActivity.startActivity(discoverableIntent);
        Log.i("Log", "Discoverable ");
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Message msg = Message.obtain();
            Toast.makeText(context, "ACTION", Toast.LENGTH_SHORT).show();
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            //BluetoothDevice mDevice = null;
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                // Toast.makeText(context, "ACTION_FOUND_STATE_CHANGED", Toast.LENGTH_SHORT).show();
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.i("Connection SUccess", "ACTION_FOUND_STATE_CHANGED");
                   /* Log.i("Connection SUccess", "BONDED");
                    SharedPreferences.Editor editor = ((MainActivity) mActivity).getSharedPreferencesBluetoothDevice().edit();
                    editor.putString(mActivity.getResources().getString(R.string.shared_preference_bluetooth_address), device.getAddress());
                    editor.commit();
                    mBluetoothDetectedDevices = null;
                    al.cancel();*/
                    /*if (Connect(device)) {
                        Log.i("Connection SUccess", "ACTION_FOUND_STATE_CHANGED");
                        //hasFailedToConnectMustRetry = false;
                        SharedPreferences.Editor editor = ((MainActivity) mActivity).getSharedPreferencesBluetoothDevice().edit();
                        editor.putString(mActivity.getResources().getString(R.string.shared_preference_bluetooth_address), device.getAddress());
                        editor.commit();
                        mBluetoothDetectedDevices = null;
                        al.cancel();
                        ((MainActivity) mActivity).OnFragmentChange(FragmentTags.LiquorList, null);
                    } else {
                        Log.i("Connection Failed", "ACTION_FOUND_STATE_CHANGED");
                        //hasFailedToConnectMustRetry = true;
                        abortBroadcast();
                        mActivity.unregisterReceiver(this);
                        startSearching();
                    }*/
                    if(mConnectTask == null) {
                        mConnectTask = new connectTask(device);
                        mConnectTask.execute();
                    }

                } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.i("BondState", "BONDING");
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.i("BondState", "NONE");
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                ListBluetoothDevices();
                mBluetoothStatusText.setText("Searching For Devices...");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                if(mConnectTask == null) {
                    mBluetoothStatusView.setVisibility(View.GONE);
                    mListViews.setVisibility(View.VISIBLE);
                }


            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (device.getBondState() == BluetoothDevice.BOND_BONDED && device.getAddress().equals(getRecentConnectedDevice())) {

                    if(mConnectTask == null) {
                        mConnectTask = new connectTask(device);
                        mConnectTask.execute();
                    }

                } else {

                    if (!mBluetoothDetectedDevices.contains(device.getAddress())) {
                        mBluetoothDetectedDevices.add(device);

                        mBluetoothDetectedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                }
                mBluetoothDetectedDevicesAdapter.notifyDataSetChanged();
                Log.e("Size", mBluetoothDetectedDevicesAdapter.getCount() + "");
            }
        }

    };

    private boolean Connect(BluetoothDevice device) {
        try
        {
            mBluetoothSocket = createBluetoothSocket(device);
            mBluetoothAdapter.cancelDiscovery();

            mBluetoothSocket.connect();
            Log.i("socket connect", "Connection success");
            mOutputStream = mBluetoothSocket.getOutputStream();
            mInputStream = mBluetoothSocket.getInputStream();
            return true;
        } catch (Exception e) {
            try {
                mBluetoothSocket.close();
                Log.i("socket close", "trying to close socket after connection failure");
            } catch (Exception e2) {
                Log.e("Fatal Error", "In onResume() and unable to close socket during connection failure"
                        + e2.getMessage() + ".");
            } finally {
                return false;
                //new dialog with list of paired and detected devices
            }
        }
    }

    private void ListBluetoothDevices() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mActivity);
            al = mAlertDialog.create();

            al.setView(mBluetoothDevicesLayout);
            al.setCancelable(true);
            al.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    mAlertDialog = null;
                }
            });
            al.show();
        }
    }

    public void unregisterReceiver()
    {
        mActivity.unregisterReceiver(myReceiver);
    }

    public OutputStream getOutputStream()
    {
        return this.mOutputStream;
    }

    public InputStream getInputStream()
    {
        return this.mInputStream;
    }

    class connectTask extends AsyncTask<Void,Void,Boolean>
    {
        BluetoothDevice mDevice;
        connectTask(BluetoothDevice device)
        {
            mDevice = device;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            return Connect(mDevice);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if(aBoolean)
            {
                Log.i("Connection SUccess", "ACTION_FOUND_STATE_CHANGED");
                //hasFailedToConnectMustRetry = false;
                mActivity.unregisterReceiver(myReceiver);
                SharedPreferences.Editor editor = ((MainActivity) mActivity).getSharedPreferencesBluetoothDevice().edit();
                editor.putString(mActivity.getResources().getString(R.string.shared_preference_bluetooth_address), mDevice.getAddress());
                editor.commit();
                mBluetoothDetectedDevices = null;
                al.cancel();
                ((MainActivity) mActivity).OnFragmentChange(FragmentTags.LiquorList, null);
            }
            else
            {
                Log.i("Connection Failed", "ACTION_FOUND_STATE_CHANGED");
                //hasFailedToConnectMustRetry = true;
               // abortBroadcast();
               // mActivity.unregisterReceiver(this);
                //startSearching();
            }
        }
    }

    public class DispenseTask extends AsyncTask<Void, String, Void>
    {
        ProgressDialog prgdialog;
        Activity activity;
        public DispenseTask(Activity activity)
        {
            prgdialog = new ProgressDialog(activity);
            prgdialog.setIndeterminate(true);
            prgdialog.setCancelable(false);
            prgdialog.setMessage("Dispensing...");
            prgdialog.setTitle("Please Wait");
            this.activity = activity;
        }

        @Override
        protected void onPreExecute()
        {
            prgdialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            byte[] buffer = new byte[256];
            StringBuilder sb = new StringBuilder();
            int bytes;
            while(true)
            {
                try
                {

                    bytes = mInputStream.read(buffer);
                    String strIncom = new String(buffer,0,bytes);
                    sb.append(strIncom);
                    Log.e("Arduino Data String",strIncom);
                    if(sb.toString().equals("done"))
                    {
                        sb.delete(0, sb.length());
                        publishProgress("SUCCESS");
                        return null;
                    }

                }
                catch(IOException e)
                {
                    Log.e("CATCH DISPENSE",e.toString());
                    Log.e("Arduino Data String",sb+" CATCH");
                    return null;
                }

            }
        }



        @Override
        protected void onProgressUpdate(String... values)
        {
            //try
            //{
            prgdialog.dismiss();
            //((MainActivity)activity).getInputStream().close();
            //}
            //catch (IOException e)
            //{
            // TODO Auto-generated catch block
            //	e.printStackTrace();
            //}
        }

        @Override
        protected void onPostExecute(Void result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }


    }
}
