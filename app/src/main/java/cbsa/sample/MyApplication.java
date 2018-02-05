package cbsa.sample;


import android.app.Application;
import android.util.Log;

import timber.log.Timber;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        Log.i("vtt", "onCreate");
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        Timber.i("vtt MyApplication onCreate done");
    }
}
