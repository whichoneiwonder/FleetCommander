package com.project.jaja.fleetcommander;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.project.jaja.fleetcommander.util.SystemUiHider;

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
*   Created by avnishjain and jmcma
*
*   Added NewGame button on MainActivity for debugging purposes.
*   Use this for testing game components when not using the P2P features.
*
* */
public class NewGameActivity extends Activity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //Requests the view not to show the top banner
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        ArrayList<Integer> dirs1 = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0)){};
//
//        ArrayList<DefaultShip> fleetLeft = new ArrayList<DefaultShip>();
//
//        // All ships are facing inwards, hence 2 (East) for fleetLeft
//        Location locLeft1 = new Location(2, 3);
//        DefaultShip shipLeft1 = new DefaultShip(locLeft1, 2, 100, dirs1);
//        fleetLeft.add(shipLeft1);
//
//        Location locLeft2 = new Location(2, 6);
//        DefaultShip shipLeft2 = new DefaultShip(locLeft2, 2, 100, dirs1);
//        fleetLeft.add(shipLeft2);
//
//        Location locLeft3 = new Location(2, 9);
//        DefaultShip shipLeft3 = new DefaultShip(locLeft3, 2, 100, dirs1);
//        fleetLeft.add(shipLeft3);
//
//        ArrayList<DefaultShip> fleetRight = new ArrayList<DefaultShip>();
//
//        // All ships are facing inwards, hence 6 (West) for fleetRight
//        Location locRight1 = new Location(18, 3);
//        DefaultShip shipRight1 = new DefaultShip(locRight1, 6, 100, dirs1);
//        fleetRight.add(shipRight1);
//
//        Location locRight2 = new Location(18, 6);
//        DefaultShip shipRight2 = new DefaultShip(locRight2, 6, 100, dirs1);
//        fleetRight.add(shipRight2);
//
//        Location locRight3 = new Location(18, 9);
//        DefaultShip shipRight3 = new DefaultShip(locRight3, 6, 100, dirs1);
//        fleetRight.add(shipRight3);
//
//
//        // Server Player always starts play on left of screen
//        Player myPlayer = new Player("", 0, 10, fleetLeft, "blue");
//        Player opponent = new Player("", 0, 10, fleetRight, "red");
//
//        //Sets the content of the custom view to be that of the Activity
//        setContentView(new GameView(this, myPlayer, opponent));
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.new_game, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//}

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
    private Button sendButton;

    // The field where the String is entered which will be sent
    private EditText messageField;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_new_game);

        // Get data from previous View
        Intent intent = getIntent();
        SERVERIP = intent.getStringExtra("SERVERIP");
        CLIENTIP = intent.getStringExtra("CLIENTIP");

        pauseButton = (Button) findViewById(R.id.pause_button);
        sendButton = (Button) findViewById(R.id.send_button);
        sent = (TextView) findViewById(R.id.sent);
        countDownText = (TextView) findViewById(R.id.count_down_text);
        messageField = (EditText) findViewById(R.id.message_field);
        countDown = new MyCount(timeLeft, 1000);

        if (CLIENTIP.equals("")) {
            // Server Player always starts play on left of screen
            myPlayer = new Player(SERVERIP, "", 0, 10, "blue");
            opponent = new Player(CLIENTIP, "", 0, 10, "red");

            Thread serverThread = new Thread(new ServerThread());
            serverThread.start();
        } else {
            // Client Player always starts play on right of screen
            myPlayer = new Player(CLIENTIP, "", 0, 10, "red");
            opponent = new Player(SERVERIP, "", 0, 10, "blue");

            Thread clientThread = new Thread(new ClientThread());
            clientThread.start();
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
                        countDownText.setText("DONE");
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
                                    out.println("Connected to " + SERVERIP);
                                    countDown.start();
                                    setContentView(new GameView(getApplicationContext(), myPlayer, opponent, 3));
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

                            connected = true;
                            while (connected) {

                                line = in.readLine();
                                line.replaceAll("\\s", "");

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
                                        } else if (line.startsWith("{")) {
                                            try {
                                                opponent.updatePlayer(line);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            try {
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

    /* Client thread looks for open server sockets on the given IP address and port
     *
     */
    public class ClientThread implements Runnable {

        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVERIP);
                try {
                    socket = new Socket(serverAddr, SERVERPORT);

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

                    connected = true;
                    while (connected) {
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
                                } else if (line.startsWith("{")) {
                                    try {
                                        opponent.updatePlayer(line);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        countDownText.setText(opponent.toJSONString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    if (line.length() > 9 &&
                                            line.substring(0, 9).equals("Connected")) {
                                        sent.setText(line);
                                        countDown.start();
                                        setContentView(new GameView(getApplicationContext(), myPlayer, opponent, 3));
                                    } else {
                                        countDownText.setText(line);
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