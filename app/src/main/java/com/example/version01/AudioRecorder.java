package com.example.version01;

import android.media.MediaRecorder;
import java.io.IOException;

public class AudioRecorder {

    private MediaRecorder recorder;
    private String filePath;
    private boolean isRecording = false;

    public AudioRecorder(String filePath) {
        this.filePath = filePath;
    }

    public void startRecording() {
        if (isRecording) return;

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(filePath);

        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if (!isRecording) return;

        recorder.stop();
        recorder.release();
        recorder = null;
        isRecording = false;
    }
}
