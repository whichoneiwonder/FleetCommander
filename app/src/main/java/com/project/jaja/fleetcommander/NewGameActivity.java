package com.project.jaja.fleetcommander;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/*
*   Created by avnishjain and jmcma and a little help from Anton ;)
*
*   Core game functionality here
*
* */
public class NewGameActivity extends Activity {

    // Client IP
    public String CLIENTIP = "";

    // Server IP
    public String SERVERIP = "";

    // This Player's MAC Address
    private String mac;

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

    // Input String from socket
    private String line = null;

    // Countdown timer
    private MyCount countDown;

    // Countdown timer time variable
    private long timeLeft = 10000;

    // Player class associated with this instance
    private Player myPlayer;

    // Player class associated with other instance
    private Player opponent;

    // The GameView to which we transition when connected
    private GameView gv;

    // The Panel inside the above declared GameView
    private Panel p;

    // This ServerThread
    private ServerThread st = null;

    // This ClientThread
    private ClientThread ct = null;

    // The TextView which shows your IP that you are listening on when running a Serve Thread
    private TextView sent;

    // This game's intent
    private Intent intent;

    // Statistics for this Player
    private Statistics stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //We don't want the created window to have a title bar as games should be an immersive
        //experience
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_new_game);

        // Get data from previous View
        intent = getIntent();
        SERVERIP = intent.getStringExtra("SERVERIP");
        CLIENTIP = intent.getStringExtra("CLIENTIP");

        // Find MAC Address, courtesy of StackOverflow
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        mac = info.getMacAddress();

        //The text that is to be sent across the P2P network
        sent = (TextView) findViewById(R.id.sent);
        countDown = new MyCount(timeLeft, 1000);

        //If the CLIENTIP is not set then the user is the server
        if (CLIENTIP.equals("")) {
            // Server Player always starts play on left of screen
            myPlayer = new Player(SERVERIP, mac, 0, 10, "blue");
            opponent = new Player(CLIENTIP, "", 0, 10, "red");

            st = new ServerThread();
            Thread serverThread = new Thread(st);
            serverThread.start();
        } else {
            // Client Player always starts play on right of screen
            myPlayer = new Player(CLIENTIP, mac, 0, 10, "red");
            opponent = new Player(SERVERIP, "", 0, 10, "blue");

            ct = new ClientThread();
            Thread clientThread = new Thread(ct);
            clientThread.start();
        }

        //Once the threads have been started we need to instantiate the GameView and Panel
        gv = new GameView(getApplicationContext(), myPlayer, opponent, 3);
        p = gv.getPanel();

        // Observe the panel
        if (CLIENTIP.equals("")) {
            p.addObserver(st);
        } else {
            p.addObserver(ct);
        }

        //Reading in the statistics from a text file on the SD card and instantiating an instance
        //of statistics with the retrieved data
        try {
            SharedPreferences settings = getSharedPreferences("fleetCommander", 0);
            stats = new Statistics(settings.getString("playerStatistics", ""));
        } catch(JSONException e){
            Log.e("Reading player stats", e.toString());
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
    //////////////////////////////////////////////////////////////////////////////////////////////
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
        }

        /**
         * Overrides onFinish, set's text to DONE, disables Pause/Play button and sends over
         * this android Player's details as a JSON string
         */
        @Override
        public void onFinish() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    myPlayer.setTurn(myPlayer.getTurn() + 1);
                    try {
                        out.println(myPlayer.toJSONString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////

    /* Server Thread - operates much like the client, however it opens the server socket and
     * waits for connections from a client
     */
    public class ServerThread implements Runnable, Observer {

        /**
         * Updates when the Panel has been touched
         * @param o Panel object
         */
        public void update(Object o) {
            p = (Panel) o;
            if (!p.isPaused()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        countDown.cancel();
                        out.println("PAUSE");
                    }
                });
                Log.d("Pause", "Pause sent");
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        countDown = new MyCount(timeLeft, 1000);
                        countDown.start();
                        out.println("PLAY");
                    }
                });
                Log.d("Pause", "Play sent");
            }
        }

        /**
         * Sends out the game end message
         * @param user User that lost
         */
        public void sendEndGame(String user) {
            final String result = "GAME END" + user;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    out.println(result);
                }
            });
        }

        /**
         * Updates the stats json file and shows a toast message
         * @param result Result of this game
         */
        public void endGame(String result) {
            Toast.makeText(getApplicationContext(), "YOU " + result, Toast.LENGTH_SHORT).show();
            Statistic stat = new Statistic(0, 0);

            //Updating the player statistics
            stats.addStatistics(opponent.getMacAddress(), stat);

            //Writing the statistics to shared preferences
            try {
                SharedPreferences settings = getSharedPreferences("fleetCommander", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("playerStatistics", stats.toJSONString());
                editor.commit();
            } catch(JSONException e){
                Log.e("Reading player stats", e.toString());
            }


            // Go back to home
            onStop();
            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
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
                                    out.println("Connected to " + SERVERIP);
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
                                        if (line.startsWith("GAME END")) {
                                            if (line.startsWith("GAME END me")) {
                                                endGame("WON");
                                            } else if (line.startsWith("GAME END you")) {
                                                endGame("LOST");
                                            } else {
                                                endGame("DREW");
                                            }
                                            connected = false;
                                        } else if (line.startsWith("PAUSE")) {
                                            countDown.cancel();
                                            gv.getPanel().setPause(!p.isPaused());
                                            gv.setNoClick(true);
                                            Log.d("Pause", "Pause received");
                                        } else if (line.startsWith("PLAY")) {
                                            countDown = new MyCount(timeLeft, 1000);
                                            countDown.start();
                                            gv.getPanel().setPause(!p.isPaused());
                                            gv.setNoClick(false);
                                            Log.d("Pause", "Play received");
                                        } else if (line.startsWith("{")) {
                                            String test = null;
                                            try {
                                                opponent.updatePlayer(line);
                                                test = opponent.toJSONString();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            Log.d("json", test);

                                            // check alive
                                            if (myPlayer.stillHasShips() &&
                                                    opponent.stillHasShips()) {
                                                    timeLeft = 10000;
                                                    countDown = new MyCount(timeLeft, 1000);
                                                    countDown.start();

                                            } else if (!myPlayer.stillHasShips()) {
                                                sendEndGame("me");
                                                endGame("LOST");
                                            } else if (!opponent.stillHasShips()) {
                                                sendEndGame("you");
                                                endGame("WON");
                                            } else {
                                                sendEndGame("draw");
                                                endGame("DREW");
                                            }
                                        }
                                    }
                                });
                            }
                            // Go back to home
                            onStop();
                            intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);

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

        /**
         * Updates when the Panel has been touched
         * @param o Panel object
         */
        public void update(Object o) {
            p = (Panel) o;
            if (!p.isPaused()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        countDown.cancel();
                        out.println("PAUSE");
                    }
                });
                Log.d("Pause", "Pause sent");
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        countDown = new MyCount(timeLeft, 1000);
                        countDown.start();
                        out.println("PLAY");
                    }
                });
                Log.d("Pause", "Play sent");
            }
        }

        /**
         * Sends out the game end message
         * @param user User that lost
         */
        public void sendEndGame(String user) {
            final String result = "GAME END" + user;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    out.println(result);
                }
            });
        }

        /**
         * Updates the stats json file and shows a toast message
         * @param result Result of this game
         */
        public void endGame(String result) {
            Toast.makeText(getApplicationContext(), "YOU " + result, Toast.LENGTH_SHORT).show();
            Statistic stat = new Statistic(0, 0);

            //Updating the statistics
            stats.addStatistics(opponent.getMacAddress(), stat);

            //Writing the statistics to sharedPreferences
            try {
                SharedPreferences settings = getSharedPreferences("fleetCommander", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("playerStatistics", stats.toJSONString());
                editor.commit();
            } catch(JSONException e){
                Log.e("Reading player stats", e.toString());
            }

            // Go back to home
            onStop();
            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
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
                                if (line.startsWith("GAME END")) {
                                    if (line.startsWith("GAME END me")) {
                                        endGame("WON");
                                    } else if (line.startsWith("GAME END you")) {
                                        endGame("LOST");
                                    } else {
                                        endGame("DREW");
                                    }
                                    connected = false;
                                } else if (line.startsWith("PAUSE")) {
                                    countDown.cancel();
                                    gv.getPanel().setPause(!p.isPaused());
                                    gv.setNoClick(true);
                                    Log.d("Pause", "Pause received");
                                } else if (line.startsWith("PLAY")) {
                                    countDown = new MyCount(timeLeft, 1000);
                                    countDown.start();
                                    gv.getPanel().setPause(!p.isPaused());
                                    gv.setNoClick(false);
                                    Log.d("Pause", "Play received");
                                } else if (line.startsWith("{")) {
                                    String test = null;
                                    try {
                                        opponent.updatePlayer(line);
                                        test = opponent.toJSONString();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("json", test);

                                    // check alive
                                    if (myPlayer.stillHasShips() &&
                                            opponent.stillHasShips()) {
                                        timeLeft = 10000;
                                        countDown = new MyCount(timeLeft, 1000);
                                        countDown.start();

                                    } else if (!myPlayer.stillHasShips()) {
                                        sendEndGame("me");
                                        endGame("LOST");
                                    } else if (!opponent.stillHasShips()) {
                                        sendEndGame("you");
                                        endGame("WON");
                                    } else {
                                        sendEndGame("draw");
                                        endGame("DREW");
                                    }
                                } else if (line.startsWith("Connected")) {
                                    sent.setText(line);
                                    countDown.start();
                                    setContentView(gv);
                                }
                            }
                        });
                    }

                     Log.d("ClientActivity", "Socket closed");
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