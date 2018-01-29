package cbsa.device.service;


import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;

public class ServiceBinder<T> {

    private final Context context;
    private final Class<T> serviceClassName;
    private final Class<? extends Service> nativeServiceClassName;
    private final IServiceBinderCallback<T> serviceBinderCallback;

    private boolean bound;

    private T boundService;

    public ServiceBinder(@NonNull Context context,
                         @NonNull Class<? extends Service> nativeServiceClassName,
                         @NonNull Class<T> serviceClassName,
                         @NonNull IServiceBinderCallback<T> serviceBinderCallback) {
        this.context = context;
        this.nativeServiceClassName = nativeServiceClassName;
        this.serviceClassName = serviceClassName;
        this.serviceBinderCallback = serviceBinderCallback;
    }

    public interface IServiceBinderCallback<T> {
        void onServiceConnected(T service);

        void onServiceDisconnected();
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection connection = new ServiceConnection() {

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BaseStickyService.LocalBinder binder = (BaseStickyService.LocalBinder) service;
            Object myService = binder.getService();
            if (serviceClassName.isAssignableFrom(myService.getClass())) {
                boundService = (T) myService;
                serviceBinderCallback.onServiceConnected(boundService);
            }
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceBinderCallback.onServiceDisconnected();
            bound = false;
        }
    };

    public void bindService() {
        Intent intent = new Intent(context, nativeServiceClassName);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService() {
        context.unbindService(connection);
    }
}
