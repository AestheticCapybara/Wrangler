package com.aesthetic.wrangler.core;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogHandler {
    private static DateTimeFormatter dateTimeFormatter;
    private static PipedOutputStream pipeOut;
    private static boolean isPipeConnected;

    static {
        pipeOut = new PipedOutputStream();
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        isPipeConnected = false;
    }

    private static String composeMessage(String message) {
        String output =
                "[" + LocalDateTime.now().format(dateTimeFormatter) + "] " +
                message +
                "\n";
        return output;
    }

    public static boolean connectPipe(PipedInputStream pipeIn) {
        try {
            pipeOut.connect(pipeIn);
            log("[Log Handler] PipedOutputStream connected. Logger initialized.");
            isPipeConnected = true;
            return true;
        } catch (IOException ex) {
            log("[Log Handler] Couldn't connect pipe. " + ex);
            return false;
        }
    }
    public static void log(String message) {
        String composedMessage = composeMessage(message);
        System.out.print(composedMessage);
        if (isPipeConnected) {
            try {
                pipeOut.write(composedMessage.getBytes("UTF-8"));
            } catch (IOException ex) {
                System.out.print(composeMessage("[Log Handler] Couldn't log to pipe. ") + ex);
                isPipeConnected = false;
            }
        }
    }
}
