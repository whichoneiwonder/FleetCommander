package com.project.jaja.fleetcommander;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.project.jaja.fleetcommander.DefaultShip;
import com.project.jaja.fleetcommander.Location;
import com.project.jaja.fleetcommander.Player;
import com.project.jaja.fleetcommander.util.SystemUiHider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * This class tests the socket connection which will eventually be used to transfer JSON data
 * between the two phones. This is a temporary Activity and by no means indicative of the
 * final product.
 *
 * Both the server and client behave in similar ways, with the only difference being if you are
 * hosting the game or if you are connecting as a client.
 *
 * Inspired by, but heavily altered:
 * http://thinkandroid.wordpress.com/2010/03/27/
 * incorporating-socket-programming-into-your-applications/
 */
public class PlayActivity extends Activity {
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
    public String CLIENTIP = "";

    // Server IP
    public String SERVERIP = "";

    // Default server port
    public static final int SERVERPORT = 5554;

    // Handler for UI and socket connections
    private Handler handler = new Handler();

    // Server socket for this connection
    private ServerSocket serverSocket = null;

    private Socket socket = null;

    // Explains whether the socket is connected or not
    private boolean connected = false;

    // The output stream for this connection
    public PrintWriter out = null;

    // Message field
    private TextView sent;

    // Countdown text field
    private TextView countDownText;

    // Socket input line
    private String line = null;

    // Associated with the Play/Pause button in the UI
    private Button pauseButton;

    // Associated with the sending of Strings in the UI
    private Button sendButton;

    // The field where the String is entered which will be sent
    private EditText messageField;

    // Countdown timer for testing
    private MyCount countDown;

    // Countdown timer time variable
    private long timeLeft = 10000;

    // Whether gameplay is paused or resumed
    private boolean paused = false;

    // Player class associated with this instance
    private Player myPlayer;

    // Player class associated with opposite instance
    private Player opponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_play);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        Intent intent = getIntent();
        SERVERIP = intent.getStringExtra("SERVERIP");
        CLIENTIP = intent.getStringExtra("CLIENTIP");

        pauseButton = (Button) findViewById(R.id.pause_button);
        sendButton = (Button) findViewById(R.id.send_button);
        sent = (TextView) findViewById(R.id.sent);
        countDownText = (TextView) findViewById(R.id.count_down_text);
        messageField = (EditText) findViewById(R.id.message_field);
        countDown = new MyCount(timeLeft, 1000);

        // All have the same movement list since they have made no movement
        ArrayList<Integer> dirs1 = new ArrayList<Integer>(Arrays.asList(0,0,0,0,0,0,0,0,0)){};

        ArrayList<DefaultShip> fleetLeft = new ArrayList<DefaultShip>();

        // All ships are facing inwards, hence 2 (East) for fleetLeft
        Location locLeft1 = new Location(2, 3);
        DefaultShip shipLeft1 = new DefaultShip(locLeft1, 2, 100, dirs1);
        fleetLeft.add(shipLeft1);

        Location locLeft2 = new Location(2, 6);
        DefaultShip shipLeft2 = new DefaultShip(locLeft2, 2, 100, dirs1);
        fleetLeft.add(shipLeft2);

        Location locLeft3 = new Location(2, 9);
        DefaultShip shipLeft3 = new DefaultShip(locLeft3, 2, 100, dirs1);
        fleetLeft.add(shipLeft3);

        ArrayList<DefaultShip> fleetRight = new ArrayList<DefaultShip>();

        // All ships are facing inwards, hence 6 (West) for fleetRight
        Location locRight1 = new Location(18, 3);
        DefaultShip shipRight1 = new DefaultShip(locRight1, 6, 100, dirs1);
        fleetRight.add(shipRight1);

        Location locRight2 = new Location(18, 6);
        DefaultShip shipRight2 = new DefaultShip(locRight2, 6, 100, dirs1);
        fleetRight.add(shipRight2);

        Location locRight3 = new Location(18, 9);
        DefaultShip shipRight3 = new DefaultShip(locRight3, 6, 100, dirs1);
        fleetRight.add(shipRight3);

        if (CLIENTIP.equals("")) {
            // Server Player always starts play on left of screen
            myPlayer = new Player(SERVERIP, 0, 10, fleetLeft);
            opponent = new Player(CLIENTIP, 0, 10, fleetRight);

            Thread serverThread = new Thread(new ServerThread());
            serverThread.start();
        } else {
            // Client Player always starts play on right of screen
            myPlayer = new Player(CLIENTIP, 0, 10, fleetRight);
            opponent = new Player(SERVERIP, 0, 10, fleetLeft);

            Thread clientThread = new Thread(new ClientThread());
            clientThread.start();
        }

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
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
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

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
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

    // Extending CountDownTimer to more easily pause and resume
    public class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timeLeft=millisUntilFinished;
            countDownText.setText("" + millisUntilFinished/1000);
        }

        @Override
        public void onFinish() {
            countDownText.setText("DONE");
            pauseButton.setEnabled(false);
        }
    }

    public class ServerThread implements Runnable {
        public void run() {
            try {
                if (SERVERIP != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sent.setText("Listening on IP: " + SERVERIP);
                        }
                    });
                    serverSocket = new ServerSocket(SERVERPORT);
                    while (true) {
                        // Listen for incoming clients
                        Socket client = serverSocket.accept();
                        CLIENTIP = client.getInetAddress().getHostAddress();
                        opponent.setIp(CLIENTIP);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sent.setText("Connected to " + CLIENTIP);
                            }
                        });
                        try {
                            connected = true;
                            while (connected) {
                                BufferedReader in = new BufferedReader(
                                        new InputStreamReader(client.getInputStream()));
                                out = new PrintWriter(client.getOutputStream(), true);

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        out.println("Connected to " + SERVERIP);
                                        countDown.start();
                                    }
                                });

                                // Sends string across to another phone
                                // Duplication is necessary
                                sendButton.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                out.println(messageField.getText().toString());
                                                messageField.setText("");
                                            }
                                        });
                                    }
                                });

                                // Enables pause on other phone
                                // Duplication is necessary
                                pauseButton.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (paused) {
                                                    countDown = new MyCount(timeLeft, 1000);
                                                    countDown.start();
                                                    paused = false;
                                                    out.println("PLAY");
                                                    sent.setText("Play sent");
                                                    pauseButton.setText("Pause");
                                                } else {
                                                    countDown.cancel();
                                                    paused = true;
                                                    out.println("PAUSE");
                                                    sent.setText("Pause sent");
                                                    pauseButton.setText("Play");
                                                }
                                            }
                                        });
                                    }
                                });
                                line = in.readLine();
                                line.replaceAll("\\s","");

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (line.equals("GAME END")) {
                                            connected = false;
                                        }  else if (line.equals("PAUSE")) {
                                            countDown.cancel();
                                            sent.setText("Pause received");
                                            pauseButton.setEnabled(false);
                                            pauseButton.setText("Paused");
                                            paused = true;
                                        } else if (line.equals("PLAY")) {
                                            countDown = new MyCount(timeLeft, 1000);
                                            countDown.start();
                                            sent.setText("Play received");
                                            pauseButton.setEnabled(true);
                                            pauseButton.setText("Pause");
                                            paused = false;
                                        } else {
                                            sent.setText(line);
                                        }
                                    }
                                });
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    out.println("GAME END");
                                }
                            });
                            break;
                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    sent.setText("Oops. Connection interrupted.");
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sent.setText("Couldn't detect internet connection.");
                        }
                    });
                }
            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sent.setText("Error");
                    }
                });
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (socket != null) {
                socket.close();
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
                    socket = new Socket(serverAddr, SERVERPORT);
                    connected = true;
                    while (connected) {
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        out = new PrintWriter(socket.getOutputStream(), true);

                        // Sends string across to another phone
                        // Duplication is necessary
                        sendButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        out.println(messageField.getText().toString());
                                        messageField.setText("");
                                    }
                                });
                            }
                        });

                        // Enables pause on other phone
                        // Duplication is necessary
                        pauseButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (paused) {
                                            countDown = new MyCount(timeLeft, 1000);
                                            countDown.start();
                                            paused = false;
                                            out.println("PLAY");
                                            sent.setText("Play sent");
                                            pauseButton.setText("Pause");
                                        } else {
                                            countDown.cancel();
                                            paused = true;
                                            out.println("PAUSE");
                                            sent.setText("Pause sent");
                                            pauseButton.setText("Play");
                                        }
                                    }
                                });
                            }
                        });

                        line = in.readLine();
                        line.replaceAll("\\s", "");

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (line.equals("GAME END")) {
                                    connected = false;
                                } else if (line.equals("PAUSE")) {
                                    countDown.cancel();
                                    sent.setText("Pause received");
                                    pauseButton.setEnabled(false);
                                    pauseButton.setText("Paused");
                                    paused = true;
                                } else if (line.equals("PLAY")) {
                                    countDown = new MyCount(timeLeft, 1000);
                                    countDown.start();
                                    sent.setText("Play received");
                                    pauseButton.setEnabled(true);
                                    pauseButton.setText("Pause");
                                    paused = false;
                                } else {
                                    sent.setText(line);
                                    if (line.length() > 9) {
                                        if (line.substring(0, 9).equals("Connected")) {
                                            countDown.start();
                                        }
                                    }
                                }
                            }
                        });
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            out.println("GAME END");
                        }
                    });
                    socket.close();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sent.setText("socket closed");
                        }
                    });
                    Log.d("ClientActivity", "C: Closed.");
                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sent.setText("except");

                        }
                    });
                    Log.e("ClientActivity", "S: Error", e);
                }

            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
            }
        }
    }
}
