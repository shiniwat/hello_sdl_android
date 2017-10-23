package com.sdl.hellosdlandroid;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class SdlApplication extends Application{

    private static final String TAG = SdlApplication.class.getSimpleName();

    private static SdlApplication instance;
    public static boolean forceAoa = false;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LockScreenActivity.registerActivityLifecycle(this);
    }

    private static boolean isSdlServiceRunning() {
        ActivityManager manager = (ActivityManager) instance.getSystemService(instance.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SdlService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void startSDL(String action) {
        if (!isSdlServiceRunning()) {
            Intent intent = new Intent(instance, SdlService.class);
            if (action != null) {
                intent.setAction(action);
            }
            instance.startService(intent);
        } else {
            Log.d(TAG, "SdlService has been instantiated already");
        }
    }

    public static void stopSDL() {
        Intent intent = new Intent(instance, SdlService.class);
        instance.stopService(intent);
    }

    public static void restartSDL() {
        stopSDL();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSDL("");
            }
        }, 1000);
    }
}
