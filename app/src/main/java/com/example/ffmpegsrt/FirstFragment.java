package com.example.ffmpegsrt;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*FFmpegHelper.saveVideo(getActivity());
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        getVideo();
                    }
                });*/

                startListening();

                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Socket socket = new Socket("0.0.0.0", 7800);
                            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                            printWriter.write("holaaa");
                            printWriter.flush();
                            printWriter.close();
                            socket.close();
                            Log.d("dddddddd", "message sent!");
                        }catch (Exception ex){
                            StringWriter errors = new StringWriter();
                            ex.printStackTrace(new PrintWriter(errors));
                            Log.d("dddddddd", errors.toString());
                        }
                    }
                }).start();*/

                //t.start();


                int rc = FFmpeg.execute("-f android_camera -video_size hd720 -input_queue_size 60 -i 0:0 -r 30 -f h264 udp://0.0.0.0:7800");

                if (rc == RETURN_CODE_SUCCESS) {
                    Log.d(Config.TAG, "Command execution completed successfully.");
                } else if (rc == RETURN_CODE_CANCEL) {
                    Log.d(Config.TAG, "Command execution cancelled by user.");
                } else {
                    Log.d(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
                    Config.printLastCommandOutput(Log.INFO);
                }
            }
        });
    }

    public void getVideo() {
        try {

            DatagramSocket socket = new DatagramSocket();

            while (true) {
                byte[] buffer = new byte[512];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
            }
        }catch (Exception e) {
            Log.e(String.valueOf(e), "");
        }
    }

    private void startListening(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                /*try{
                    ServerSocket serverSocket = new ServerSocket(7800);
                    while(true){
                        Socket socket = serverSocket.accept();
                        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String message = bufferedReader.readLine();
                        Log.d("dddddddd", message);
                    }
                }catch (Exception ex){
                    StringWriter errors = new StringWriter();
                    ex.printStackTrace(new PrintWriter(errors));
                    Log.d("dddddddd", errors.toString());
                }*/

                try{
                    byte[] buffer = new byte[512];
                    DatagramSocket socketUDP = new DatagramSocket(7800);
                    while(true){
                        DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
                        socketUDP.receive(peticion);
                        String message = new String(peticion.getData());
                        Log.d("dddddddd", message);
                    }
                }catch (Exception ex){
                    StringWriter errors = new StringWriter();
                    ex.printStackTrace(new PrintWriter(errors));
                    Log.d("dddddddd", errors.toString());
                }
            }
        }).start();
    }
}