package com.aesthetic.wrangler.controllers;

import com.aesthetic.wrangler.core.LogHandler;

import javafx.scene.control.TextArea;
import java.io.IOException;
import java.io.PipedInputStream;

public class LogAreaController extends Thread {
    private TextArea logArea;
    private PipedInputStream pipeIn;
    private boolean isListening;

    public LogAreaController(TextArea logArea) {
        this.logArea = logArea;
        pipeIn = new PipedInputStream();
        if (LogHandler.connectPipe(pipeIn)) {
            isListening = true;
        } else {
            isListening = false;
        }
    }

    private String pipeInToString() {
        try {
            byte[] readBuffer = new byte[1024];
            pipeIn.read(readBuffer);
            String bytesToString = new String(readBuffer, "UTF-8");
            return bytesToString;
        } catch (IOException ex) {
            LogHandler.log("[LogAreaController] Couldn't transpose pipe message to string. " + ex);
            return null;
        }
    }

    public void run() {
        while (isListening) {
            String message = pipeInToString();
            if (message != null) {
                logArea.appendText(message);
            }
        }
    }
}
