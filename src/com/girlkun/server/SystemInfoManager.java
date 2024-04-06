package com.girlkun.server;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.TimeUnit;

/**
 * Quản lý thông tin hệ thống.
 * 
 * @author delb1
 */
public class SystemInfoManager {

    private static long startTime = System.currentTimeMillis(); // Ghi lại thời điểm bắt đầu dự án

    public static String getSystemInfoMessage() {
        int activeThreadCount = Thread.activeCount();
        int availableProcessors = Runtime.getRuntime().availableProcessors();

        // Lấy thông tin về bộ nhớ
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long totalPhysicalMemory = heapMemoryUsage.getMax();
        long usedPhysicalMemory = heapMemoryUsage.getUsed();
        long freePhysicalMemory = totalPhysicalMemory - usedPhysicalMemory;

        // Tính tỷ lệ sử dụng CPU
        double cpuUsage = (double) (totalPhysicalMemory - freePhysicalMemory) / totalPhysicalMemory * 100.0;

        // Chuyển đổi dung lượng RAM từ byte sang gigabyte
        double totalPhysicalMemoryGB = totalPhysicalMemory / (1024.0 * 1024.0 * 1024.0);
        double usedPhysicalMemoryGB = usedPhysicalMemory / (1024.0 * 1024.0 * 1024.0);
        double freePhysicalMemoryGB = freePhysicalMemory / (1024.0 * 1024.0 * 1024.0);

        // Format các giá trị thành chuỗi
        String availableProcessorsStr = String.valueOf(availableProcessors);
        String cpuUsageStr = String.format("%.2f", cpuUsage);
        String totalPhysicalMemoryStr = String.format("%.2f", totalPhysicalMemoryGB);
        String usedPhysicalMemoryStr = String.format("%.2f", usedPhysicalMemoryGB);
        String freePhysicalMemoryStr = String.format("%.2f", freePhysicalMemoryGB);

        // Tính thời gian chạy của dự án và định dạng thành chuỗi
        long elapsedTimeMillis = System.currentTimeMillis() - startTime;
        String elapsedTimeStr = formatElapsedTime(elapsedTimeMillis);

        return "\n Thread: " + activeThreadCount
                + "\nSố lượng CPU: " + availableProcessorsStr
                + "\n|5|Tỷ lệ sử dụng CPU : " + cpuUsageStr + "%"
                + "\n|7|Tổng dung lượng RAM: " + totalPhysicalMemoryStr + " GB"
                + "\n|8|Đã sử dụng Ram: " + usedPhysicalMemoryStr + " GB"
                + "\n|3|Ram trống: " + freePhysicalMemoryStr + " GB"
                + "\n|1|Thời gian chạy: " + elapsedTimeStr;
    }

    private static String formatElapsedTime(long elapsedTimeMillis) {
        // Tính toán thời gian chạy thành giờ, phút, giây
        long hours = TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);

        // Định dạng thành chuỗi với hai chữ số cho giờ và giây
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String getElapsedTimeMessage() {
        long elapsedTimeMillis = System.currentTimeMillis() - startTime;
        return "Thời gian chạy: " + formatElapsedTime(elapsedTimeMillis);
    }
}