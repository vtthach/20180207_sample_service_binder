package cbsa.device.service;

import android.os.IBinder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;

import timber.log.Timber;

public abstract class BaseStickyService<T> extends Service {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.i("Received start id " + startId + ": " + intent);
        if (intent != null) {
            onStartCheckIntent(intent);
        }
        return START_STICKY; // Run until explicitly stopped.
    }

    protected void onStartCheckIntent(Intent intent){
        // Stub method
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("BaseStickyService - onDestroy");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.i("BaseStickyService - onCreate");

    }

    public abstract T getService();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public T getService() {
            // Return this instance of LocalService so clients can call public methods
            return BaseStickyService.this.getService();
        }
    }
}
