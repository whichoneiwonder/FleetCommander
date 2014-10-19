package com.project.jaja.fleetcommander;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/*
*   Created by avnishjain and jmcma and don't forget Anton :)
*
*   Added NewGame button on MainActivity for debugging purposes.
*   Use this for testing game components when not using the P2P features.
*
* */
public class NewGameActivity extends Activity {

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

    // Client socket to connect to ServerSocket
    private Socket socket = null;

    // True when the socket is connected
    private boolean connected = false;

    // The output stream for this connection
    public PrintWriter out = null;

    // Message field where data that was sent is displayed
    private TextView sent;

    // Countdown text field
    private TextView countDownText;

    // Input String from socket
    private String line = null;

    // Associated with the Play/Pause button in the UI
    private Button pauseButton;

    // Associated with the sending of Strings in the UI
    // private Button sendButton;

    // The field where the String is entered which will be sent
    // private EditText messageField;

    // The GameView within which the game is played
    private GameView gv;

    // Countdown timer
    private MyCount countDown;

    // Countdown timer time variable
    private long timeLeft = 30000;

    // Game play is paused when true
    private boolean paused = false;

    // Indicates end of round
    private boolean roundEnd = false;

    // Player class associated with this instance
    private Player myPlayer;

    // Player class associated with other instance
    private Player opponent;

    // This Player's MAC Address
    private String mac;

    // All game statistics
    private Statistics stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_new_game);

        // Get data from previous View
        Intent intent = getIntent();
        SERVERIP = intent.getStringExtra("SERVERIP");
        CLIENTIP = intent.getStringExtra("CLIENTIP");

        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        mac = info.getMacAddress();

        pauseButton = (Button) findViewById(R.id.pause_button);
//        sendButton = (Button) findViewById(R.id.send_button);
        sent = (TextView) findViewById(R.id.sent);
        countDownText = (TextView) findViewById(R.id.count_down_text);
//        messageField = (EditText) findViewById(R.id.message_field);
        countDown = new MyCount(timeLeft, 1000);


        ServerThread st = null;
        ClientThread ct = null;
        Thread serverThread;
        Thread clientThread;

        if (CLIENTIP.equals("")) {
            // Server Player always starts play on left of screen
            myPlayer = new Player(SERVERIP, mac, 0, 10, "blue");
            opponent = new Player(CLIENTIP, "",0, 10, "red");

            st = new ServerThread();
            serverThread = new Thread(st);
            serverThread.start();
        } else {
            // Client Player always starts play on right of screen
            myPlayer = new Player(CLIENTIP, mac, 0, 10, "red");
            opponent = new Player(SERVERIP, "", 0, 10, "blue");

            ct = new ClientThread();
            clientThread = new Thread(ct);
            clientThread.start();
        }

        gv = new GameView(getApplicationContext(), myPlayer, opponent, 3);
        Panel p = gv.getPanel();

        if (CLIENTIP.equals("")) {
            p.addObserver(st);
        } else {
            p.addObserver(ct);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Extending CountDownTimer to more easily pause and resume
    public class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * Overrides onTick to save the amount of time left which enables pause and play and
         * also shows the amount of time left on the timer in the display
         * @param millisUntilFinished
         */
        @Override
        public void onTick(long millisUntilFinished) {
            timeLeft = millisUntilFinished;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    countDownText.setText("" + timeLeft / 1000);
                }
            });
        }

        /**
         * Overrides onFinish, set's text to DONE, disables Pause/Play button and sends over
         * this android Player's details as a JSON string
         */
        @Override
        public void onFinish() {
            roundEnd = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    myPlayer.setTurn(1);
                    try {
                        out.println(myPlayer.toJSONString());
//                        countDownText.setText("DONE");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    pauseButton.setEnabled(false);
                }
            });
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////

    /* Server Thread - operates much like the client, however it opens the server socket and
     * waits for connections from a client
     */
    public class ServerThread implements Runnable, Observer {
        public void update(Object o) {
            if(o instanceof Panel) {
                Panel panel = (Panel)o;
                if (panel.isPaused()) {
                    countDown.cancel();
                    paused = true;
                    out.println("PAUSE");
                } else if (panel.isPaused()) {
                    countDown = new MyCount(timeLeft, 1000);
                    countDown.start();
                    paused = false;
                    out.println("PLAY");
                }
            }
        }

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
                        /* Listen for incoming clients
                         * Since the server was unaware of this client, it must update the
                         * IP address of the opponent
                         */
                        Socket client = serverSocket.accept();
                        CLIENTIP = client.getInetAddress().getHostAddress();
                        opponent.setIp(CLIENTIP);

                        try {
                            /* The main loop where connections happen, only broken if opponent
                             * disconnects or if a 'GAME END' signal is given
                             */

                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(client.getInputStream()));
                            out = new PrintWriter(client.getOutputStream(), true);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    sent.setText("Connected to " + CLIENTIP);
                                    out.print(mac + " " + SERVERIP);
                                    countDown.start();
                                    setContentView(gv);
                                }
                            });

                            connected = true;
                            while (connected) {

                                line = in.readLine();

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
//                                            sent.setText("Play received");
                                            pauseButton.setEnabled(true);
//                                            pauseButton.setText("Pause");
                                            paused = false;
                                        } else if (line.startsWith("mac")) {
                                            opponent.setMacAddress(line.substring(4));
                                        } else if (line.startsWith("{")) {
                                            try {
                                                opponent.updatePlayer(line);
                                                countDownText.setText(opponent.toJSONString());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            countDownText.setText(line);
                                        }
                                    }
                                });
                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    out.print("GAME END");
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

    /* Client thread looks for open server sockets on the given IP address and port
     *
     */
    public class ClientThread implements Runnable, Observer {
        public void update(Object o) {
            if(o instanceof Panel) {
                Panel panel = (Panel)o;
                if (panel.isPaused()) {
                    countDown.cancel();
                    paused = true;
                    out.println("PAUSE");
                } else if (panel.isPaused()) {
                    countDown = new MyCount(timeLeft, 1000);
                    countDown.start();
                    paused = false;
                    out.println("PLAY");
                }
            }
        }

        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVERIP);
                try {
                    socket = new Socket(serverAddr, SERVERPORT);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);

                    connected = true;
                    while (connected) {
                        line = in.readLine();

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
                                    Log.d("newgame", "PLAY");
                                    sent.setText("Play received");
                                    pauseButton.setEnabled(true);
                                    pauseButton.setText("Pause");
                                    paused = false;
                                } else if (line.startsWith("{")) {
                                    try {
                                        opponent.updatePlayer(line);
                                        countDownText.setText(opponent.toJSONString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    countDown.start();
                                    setContentView(gv);
                                    String[] comms = line.split("\\s");
                                    opponent.setIp(comms[1]);
                                    opponent.setMacAddress(comms[0]);
                                    out.print("mac " + myPlayer.getMacAddress());
                                    Log.d("newgame", "connection");
                                }
                            }
                        });
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            out.print("GAME END");
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
