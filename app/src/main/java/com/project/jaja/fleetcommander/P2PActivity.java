package com.project.jaja.fleetcommander;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.project.jaja.fleetcommander.code.*;

public class P2PActivity extends Activity implements OnClickListener,
        android.content.DialogInterface.OnClickListener, ConnectionInfoListener {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private Button mDiscover;
    private TextView mDevices;
    public ArrayAdapter mAdapter;
    private ArrayList<WifiP2pDevice> mDeviceList = new ArrayList<WifiP2pDevice>();
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2p);

        mDiscover = (Button) findViewById(R.id.discover);
        mDiscover.setOnClickListener(this);

        mDevices = (TextView) findViewById(R.id.peers);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_p2p, menu);
//        return true;
//    }

    private class WiFiDirectReceiver extends BroadcastReceiver {

        private WifiP2pManager mManager;
        private Channel mChannel;
        private P2PActivity pActivity;

        public WiFiDirectReceiver(WifiP2pManager manager, Channel channel, P2PActivity activity) {
            super();
            mManager = manager;
            mChannel = channel;
            pActivity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    String title = "ANDROID_ID[" + getAndroid_ID() + "]";
                    title += "   MAC[" + getMACAddress() + "]";
                    Toast.makeText(pActivity, "Wi-Fi Direct is enabled."+title, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(pActivity, "Wi-Fi Direct is disabled.", Toast.LENGTH_SHORT).show();
                }

            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

                if (mManager != null) {
                    mManager.requestPeers(mChannel, new PeerListListener() {

                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList peers) {
                            if (peers != null) {
                                mDeviceList.addAll(peers.getDeviceList());
                                ArrayList<String> deviceNames = new ArrayList<String>();
                                for (WifiP2pDevice device : mDeviceList) {
                                    deviceNames.add(device.deviceName);
                                }
                                if (deviceNames.size() > 0) {
                                    mAdapter = new ArrayAdapter<String>(pActivity,
                                            android.R.layout.simple_list_item_1, deviceNames);
                                    if(flag==0)
                                    {
                                        flag=1;
                                        showDeviceListDialog();
                                    }
                                } else {
                                    Toast.makeText(pActivity, "Device list is empty.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }

            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {


            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discover:
                onDiscover();
                break;
        }
    }

    private void onDiscover() {
        mManager.discoverPeers(mChannel, new ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(P2PActivity.this, "Discover peers successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(P2PActivity.this, "Discover peers failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeviceListDialog() {
        DeviceListDialog deviceListDialog = new DeviceListDialog();
        deviceListDialog.show(getFragmentManager(), "devices");
    }

    private class DeviceListDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select a device")
                    .setSingleChoiceItems(mAdapter, 0, P2PActivity.this)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });

            return builder.create();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        onDeviceSelected(which);
        dialog.dismiss();
    }

    private void onDeviceSelected(int which) {
        WifiP2pDevice device = mDeviceList.get(which);
        if (device == null) {
            return;
        }

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        mManager.connect(mChannel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(P2PActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(P2PActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * ANDROID_ID
     */
    private String getAndroid_ID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Wi-Fi MAC
     */
    private String getMACAddress() {
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();
        String mac = wifiInfo.getMacAddress();

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.

        new FileServerAsyncTask(getApplicationContext())
                .execute();

        return mac;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
        Uri uri = data.getData();

        Log.d("intent", "Intent----------- " + uri);
        Intent serviceIntent = new Intent(P2PActivity.this, FileTransfer.class);
        serviceIntent.setAction(FileTransfer.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransfer.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(FileTransfer.EXTRAS_GROUP_OWNER_ADDRESS,
                getMACAddress());
        serviceIntent.putExtra(FileTransfer.EXTRAS_GROUP_OWNER_PORT, 8988);
        startService(serviceIntent);
    }
    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;

        /**
         * @param context
         */
        public FileServerAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                System.out.println("inside");
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d("Server: Socket opened", "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.d("Server: connection done", "Server: connection done");
                final File f = new File(Environment.getExternalStorageDirectory() + "/"
                        + context.getPackageName() + "/wifip2pshared-" +   System.currentTimeMillis()
                        + ".jpg");

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();

                Log.d("server: copying files ", "server: copying files " + f.toString());
                InputStream inputstream = client.getInputStream();
                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
                return f.getAbsolutePath();
            } catch (IOException e) {
                Log.e("exp", e.getMessage());
                System.out.println(":iooo:"+e);
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {

                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                context.startActivity(intent);
            }

        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

        }

    }
    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        long startTime=System.currentTimeMillis();

        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
            long endTime=System.currentTimeMillis()-startTime;
            Log.v("","Time taken to transfer all bytes is : "+endTime);

        } catch (IOException e) {
            Log.d("exp", e.toString());
            return false;
        }
        return true;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        // TODO Auto-generated method stub

        Toast.makeText(getApplicationContext(), "connection info", 3000).show();

    }

}// This below is what it was

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
//public class P2PActivity extends Activity {
//    /**
//     * Whether or not the system UI should be auto-hidden after
//     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
//     */
//    private static final boolean AUTO_HIDE = true;
//
//    /**
//     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
//     * user interaction before hiding the system UI.
//     */
//    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
//
//    /**
//     * If set, will toggle the system UI visibility upon interaction. Otherwise,
//     * will show the system UI visibility upon interaction.
//     */
//    private static final boolean TOGGLE_ON_CLICK = true;
//
//    /**
//     * The flags to pass to {@link SystemUiHider#getInstance}.
//     */
//    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
//
//    /**
//     * The instance of the {@link SystemUiHider} for this activity.
//     */
//    private SystemUiHider mSystemUiHider;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_p2p);
//
//        final View controlsView = findViewById(R.id.fullscreen_content_controls);
//        final View contentView = findViewById(R.id.fullscreen_content);
//
//        // Set up an instance of SystemUiHider to control the system UI for
//        // this activity.
//        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
//        mSystemUiHider.setup();
//        mSystemUiHider
//                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
//                    // Cached values.
//                    int mControlsHeight;
//                    int mShortAnimTime;
//
//                    @Override
//                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//                    public void onVisibilityChange(boolean visible) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//                            // If the ViewPropertyAnimator API is available
//                            // (Honeycomb MR2 and later), use it to animate the
//                            // in-layout UI controls at the bottom of the
//                            // screen.
//                            if (mControlsHeight == 0) {
//                                mControlsHeight = controlsView.getHeight();
//                            }
//                            if (mShortAnimTime == 0) {
//                                mShortAnimTime = getResources().getInteger(
//                                        android.R.integer.config_shortAnimTime);
//                            }
//                            controlsView.animate()
//                                    .translationY(visible ? 0 : mControlsHeight)
//                                    .setDuration(mShortAnimTime);
//                        } else {
//                            // If the ViewPropertyAnimator APIs aren't
//                            // available, simply show or hide the in-layout UI
//                            // controls.
//                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
//                        }
//
//                        if (visible && AUTO_HIDE) {
//                            // Schedule a hide().
//                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
//                        }
//                    }
//                });
//
//        // Set up the user interaction to manually show or hide the system UI.
//        contentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (TOGGLE_ON_CLICK) {
//                    mSystemUiHider.toggle();
//                } else {
//                    mSystemUiHider.show();
//                }
//            }
//        });
//
//        // Upon interacting with UI controls, delay any scheduled hide()
//        // operations to prevent the jarring behavior of controls going away
//        // while interacting with the UI.
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
//    }
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        // Trigger the initial hide() shortly after the activity has been
//        // created, to briefly hint to the user that UI controls
//        // are available.
//        delayedHide(100);
//    }
//
//
//    /**
//     * Touch listener to use for in-layout UI controls to delay hiding the
//     * system UI. This is to prevent the jarring behavior of controls going away
//     * while interacting with activity UI.
//     */
//    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            if (AUTO_HIDE) {
//                delayedHide(AUTO_HIDE_DELAY_MILLIS);
//            }
//            return false;
//        }
//    };
//
//    Handler mHideHandler = new Handler();
//    Runnable mHideRunnable = new Runnable() {
//        @Override
//        public void run() {
//            mSystemUiHider.hide();
//        }
//    };
//
//    /**
//     * Schedules a call to hide() in [delay] milliseconds, canceling any
//     * previously scheduled calls.
//     */
//    private void delayedHide(int delayMillis) {
//        mHideHandler.removeCallbacks(mHideRunnable);
//        mHideHandler.postDelayed(mHideRunnable, delayMillis);
//    }
//}
