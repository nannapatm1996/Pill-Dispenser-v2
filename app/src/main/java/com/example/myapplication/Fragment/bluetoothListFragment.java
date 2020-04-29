package com.example.myapplication.Fragment;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.service.BluetoothConnectionService;
import com.example.myapplication.service.SerialListener;
import com.example.myapplication.service.SerialService;
import com.example.myapplication.service.SerialSocket;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class bluetoothListFragment extends ListFragment {

    private String TAG = "BluetoothListFrag";

    BluetoothAdapter mBluetoothAdapter;
    //FOR SOCKET
    BluetoothConnectionService mBluetoothConnection;
    Button btnStartConnection;
    Button btnSend;
    String newline = "\r\n";
    EditText etSend;
    BluetoothDevice mBTDevice;
    //private ArrayList<BluetoothDevice> listItems = new ArrayList<>();
    private ArrayList<BluetoothDevice> mDevices = new ArrayList<>();
    private ArrayAdapter<BluetoothDevice> listAdapter;

    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listAdapter = new ArrayAdapter<BluetoothDevice>(getActivity(), 0, mDevices) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                BluetoothDevice device = mDevices.get(position);
                convertView = getActivity().getLayoutInflater().
                        inflate(R.layout.bluetooth_row, parent, false);
                if (device != null) {
                    TextView deviceName = (TextView) convertView.findViewById(R.id.tvDeviceName);
                    TextView deviceAdress = (TextView) convertView.findViewById(R.id.tvDeviceAddress);

                    if (deviceName != null) {
                        deviceName.setText(device.getName());
                    }
                    if (deviceAdress != null) {
                        deviceAdress.setText(device.getAddress());
                    }
                }

                return convertView;

                /*if (view == null)
                    view = getActivity().getLayoutInflater().
                            inflate(R.layout.bluetooth_row, parent, false);
                TextView text1 = view.findViewById(R.id.tvDeviceName);
                TextView text2 = view.findViewById(R.id.tvDeviceAddress);
                text1.setText(device.getName());
                text2.setText(device.getAddress());
                return view;*/

            }
        };
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(null);
        View header = getActivity().getLayoutInflater().inflate(R.layout.fragment_bluetooth, null, false);
        getListView().addHeaderView(header, null, false);
        setEmptyText("initializing...");
        ((TextView) getListView().getEmptyView()).setTextSize(18);
        setListAdapter(listAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mBluetoothAdapter == null)
            setEmptyText("<bluetooth not supported>");
        else if(!mBluetoothAdapter.isEnabled())
            setEmptyText("<bluetooth is disabled>");
        else
            setEmptyText("<no bluetooth devices found>");
        refresh();
    }

    void refresh() {
        mDevices.clear();
        if(mBluetoothAdapter != null) {
            for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices())
                if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE)
                    mDevices.add(device);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        BluetoothDevice device = mDevices.get(position-1);
       // Bundle args = new Bundle();
       // args.putString("device", device.getAddress());
        Log.d("bluetooth", device.getAddress());


    }

}
