package org.p2p;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SystemLogger {
    private static final Logger logger = Logger.getLogger("system-log");

    public static void createLogger(Node systemNode) {
        FileHandler fh;
        try {
            LogManager.getLogManager().reset();
            // This block configure the logger with handler and formatter
            String loggerName = "log/system" + systemNode.getPort() + ".log";
            fh = new FileHandler(loggerName);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            System.out.println(System.getProperty("user.dir"));
            // the following statement is used to log any messages
            logger.info("Init log");

            // start command powershell inorder to view logs
            String[] str= { "cmd",
                    "/c",
                    "start",
                    "powershell.exe",
                    "-NoExit",
                    "-Command",
                    "Get-Content",
                    "-Path",
                    "'"+ System.getProperty("user.dir")+"/" + loggerName +"'","-Wait"};
            Runtime.getRuntime().exec(str);

        } catch (SecurityException | IOException e) {
            SystemLogger.info(e.getMessage());
        }
    }

    public static void info(String message) {
        logger.info(message);
    }
}
