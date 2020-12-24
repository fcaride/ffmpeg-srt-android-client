package com.example.ffmpegsrt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.github.thibaultbee.srtdroid.Srt;
import com.github.thibaultbee.srtdroid.enums.SockOpt;
import com.github.thibaultbee.srtdroid.enums.Transtype;
import com.github.thibaultbee.srtdroid.models.Socket;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class MainActivity2 extends AppCompatActivity {

    // views
    private Button mButtonSendSrtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // init
        Srt srt = new Srt();
        srt.startUp();

        // views binding
        mButtonSendSrtMessage = findViewById(R.id.button_send_srt_message);
        mButtonSendSrtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSendingSRTStream();
            }
        });
    }

    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //-------------------------------------- PRIVATE METHODS --------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------

    private void startSendingSRTStream(){
        // create SRT sockeet to send stream
        Socket clientSocket = new Socket();
        if(!clientSocket.isValid()){
            Log.d("dddddddddd", "clientSocket is not valid");
            return;
        }
        //clientSocket.setSockFlag(SockOpt.PASSPHRASE, "1234");
        //clientSocket.connect("192.168.43.50", 11001);



        clientSocket.setSockFlag(SockOpt.TRANSTYPE, Transtype.LIVE);
        //clientSocket.bind("192.168.1.101", 5000);
        //clientSocket.listen(2);

        clientSocket.connect("192.168.1.102", 5000);

        Log.d("dddddddddd", "client socket connected!");

        // create udp listening on 0.0.0.0:7800
        new Thread(new Runnable() {
            @Override
            public void run() {

                byte[] data;
                try{
                    byte[] buffer = new byte[1316];
                    DatagramSocket socketUDP = new DatagramSocket(7800);
                    while(true){
                        DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
                        socketUDP.receive(peticion);
                        data = peticion.getData();
                        int ret = clientSocket.send(data);
                        //Log.d("dddddddd", "ret: " + ret);
                        String message = data[data.length-1] + "";
                        Log.d("dddddddd", message);
                    }
                }catch (Exception ex){
                    StringWriter errors = new StringWriter();
                    ex.printStackTrace(new PrintWriter(errors));
                    Log.d("dddddddd", errors.toString());
                }
            }
        }).start();





        // start sending camera stream to 0.0.0.0:7800 by udp
        int rc = FFmpeg.execute("-f android_camera -video_size hd720 -i 0:0 -r 30 -f h264 udp://0.0.0.0:7800?pkt_size=1316");

        if (rc == RETURN_CODE_SUCCESS) {
            Log.d(Config.TAG, "Command execution completed successfully.");
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.d(Config.TAG, "Command execution cancelled by user.");
        } else {
            Log.d(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
            Config.printLastCommandOutput(Log.INFO);
        }
    }

    /*private void startSrtServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socketServer = new Socket();
                socketServer.setSockFlag(SockOpt.RCVSYN, true);
                socketServer.bind("0.0.0.0", 50001);
                socketServer.listen(1);
                Log.d("dddddddddd", "before accept");
                Pair<Socket, InetSocketAddress> pair = socketServer.accept();
                Socket clienteSocket = pair.first;
                //cie
                Log.d("dddddddddd", "after accept");
                //showToast("SRT Server Listening!");
            }
        }).start();
    }*/

    private void showToast(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity2.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}