package com.girlkun.server;

import java.time.LocalTime;
import com.girlkun.utils.Logger;
import java.io.IOException;
import java.time.LocalTime;

public class AutoMaintenance extends Thread {

    public static boolean AutoMaintenance = true;
    public static final int hours = 0;
    public static final int mins = 1;
    private static AutoMaintenance instance;
    public static boolean isRuning;

    public static AutoMaintenance gl() {
        if (instance == null) {
            instance = new AutoMaintenance();
        }
        return instance;
    }

    @Override
    public void run() {
        while (!Maintenance.isRuning && !isRuning) {
//            try {
            if (AutoMaintenance) {
                LocalTime currentTime = LocalTime.now();

        
                if (currentTime.getHour() == hours && currentTime.getMinute() == mins) {
                    Logger.log(Logger.PURPLE_BRIGHT, "Tiến hành bảo trì tự động");
              
                    Maintenance.gI().start(60);
                    AutoMaintenance = false;
                }
            }

        }
    }

    public static void runBatFile(String batchFilePath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "start", batchFilePath);
        Process process = processBuilder.start();
        try {
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
