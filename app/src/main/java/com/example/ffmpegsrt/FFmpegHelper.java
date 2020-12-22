package com.example.ffmpegsrt;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.github.thibaultbee.srtdroid.Srt;
import com.github.thibaultbee.srtdroid.models.Socket;

import java.io.File;


public class FFmpegHelper {

    public static void saveVideo(Context context) {
        File path = context.getFilesDir();

        Srt srt = new Srt();
        srt.startUp();



        long executionId = FFmpeg.executeAsync("-f android_camera -video_size hd720 -input_queue_size 60 -i 0:0 -r 30 -f h264 udp://192.168.1.102:1235", new ExecuteCallback() {
            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == 1) {
                    Log.i(Config.TAG, "Async command execution completed successfully.");
                } else if (returnCode == 0) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                }
                Log.i(Config.TAG, String.format("Termino with returnCode=%d.", returnCode));

            }
        });
    }




}
