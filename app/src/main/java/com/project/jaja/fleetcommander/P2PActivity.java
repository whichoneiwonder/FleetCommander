package com.project.jaja.fleetcommander;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.project.jaja.fleetcommander.util.SystemUiHider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * This class tests the socket connection which will eventually be used to transfer JSON data
 * between the two phones. This is a temporary Activity and by no means indicative of the
 * final product.
 */
public class P2PActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    // Client IP
    public String CLIENTIP;

    // SERVER IP
    public String SERVERIP = "10.0.2.15";

    // DESIGNATE A PORT
    public static final int SERVERPORT = 5554;

    private Handler handler = new Handler();
    private ServerSocket serverSocket = null;

    // Explains whether the socket is connected or not
    private boolean connected = false;

    // Explains whether this Activity is acting as a ServerThread or a ClientThread
    private boolean hosting = false;

    public PrintWriter out = null;

    // Associated with the top TextView in the UI
    private TextView serverStatus;

    // Associated with the Send button in the UI
    public Button sendMessage;

    // Associated with the IP EditText field in the UI
    private EditText serverIpField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_p2p);
        serverIpField = (EditText) findViewById(R.id.server_ip);
        serverStatus = (TextView) findViewById(R.id.server_status);
        sendMessage = (Button) findViewById(R.id.send_button);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /**
     * This function activates the ServerThread and displays this device's IP address
     * @param view This view
     */
    public void clickServer(View view) {
        SERVERIP = getLocalIpAddress();
        Thread fst = new Thread(new ServerThread());
        fst.start();
        hosting = true;

        // Prevent the user from kicking off the thread again
        Button serverButton = (Button) findViewById(R.id.server_button);
        serverButton.setEnabled(false);
    }

    /**
     * This function activates the ClientThread given the correct IP address has been entered
     * @param view This view
     */
    public void clickClient(View view) {
        if (!connected) {
            SERVERIP = serverIpField.getText().toString();
            CLIENTIP = getLocalIpAddress();
            if (!SERVERIP.equals("")) {
                Thread cThread = new Thread(new ClientThread());
                cThread.start();
            }
        }
    }

    /**
     * This ServerThread lets this device act as server
     */
    public class ServerThread implements Runnable {
        public void run() {
            try {
                if (SERVERIP != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Listening on IP: " + SERVERIP);
                        }
                    });
                    serverSocket = new ServerSocket(SERVERPORT);
                    while (true) {
                        // LISTEN FOR INCOMING CLIENTS
                        Socket client = serverSocket.accept();
                        CLIENTIP = client.getInetAddress().getHostAddress();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatus.setText("Connected.");
                            }
                        });

                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            String line = null;
                            out = new PrintWriter(client.getOutputStream(), true);

                            // Broken at the moment
                            sendMessage.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    EditText message = (EditText) findViewById(R.id.message_field);
                                    out.println(message.getText().toString());
                                    message.setText("");
                                }
                            });

                            connected = true;
                            while (connected) {
                                line = in.readLine();

                                if (line.equals("GAME END")) {
                                    serverSocket.close();
                                } else {
                                    serverStatus.setText(line);
                                }
                            }
                            break;
                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    serverStatus.setText("Oops. Connection interrupted. Please reconnect your phones.");
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Couldn't detect internet connection.");
                        }
                    });
                }
            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Error");
                    }
                });
                e.printStackTrace();
            }
        }
    }

    /**
     * This function gets the local IP address in a readable String form
     * Source: http://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device
     * @return String value IP address
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ClientThread implements Runnable {

        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVERIP);
                try {
                    Socket socket = new Socket(serverAddr, SERVERPORT);
                    connected = true;
                    while (connected) {
                        // Set up socket streams in and out
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String input = in.readLine();
                        out = new PrintWriter(socket.getOutputStream(), true);

                        // Broken at the moment
                        sendMessage.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                EditText message = (EditText) findViewById(R.id.message_field);
                                out.println(message.getText().toString());
                                message.setText("");
                            }
                        });

                        // Deal with various commands, will be expanded
                        if (input.equals("GAME END")) {
                            connected = false;
                        } else {
                            serverStatus.setText(input);
                        }
                    }
                    socket.close();
                    serverStatus.setText("socket closed");
                    Log.d("ClientActivity", "C: Closed.");
                } catch (Exception e) {
                        serverStatus.setText("except");
                        Log.e("ClientActivity", "S: Error", e);
                }
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
            }
        }
    }
}
