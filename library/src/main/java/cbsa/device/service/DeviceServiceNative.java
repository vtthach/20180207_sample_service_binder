package cbsa.device.service;


import android.content.Intent;

import javax.inject.Inject;

import cbsa.device.barcode.service.BarcodeService;
import cbsa.device.injection.BarcodeServiceModule;
import cbsa.device.injection.DaggerDeviceServiceComponent;
import cbsa.device.injection.DeviceServiceModule;
import cbsa.device.presenter.DeviceServicePresenter;
import timber.log.Timber;

/**
 * This service aim to manage all connected device
 * The other app interactive with this service via LocalBinder
 * Consider using aidl if this service run on other process and serve multiple app
 */
public class DeviceServiceNative extends BaseStickyService<DeviceService> implements DeviceService {

    public static final String KEY_IP_ADDRESS = "KEY_IP_ADDRESS";
    public static final String KEY_PORT = "KEY_PORT";
    public static final String KEY_CONNECTION_TIMEOUT_IN_MILLIS = "KEY_CONNECTION_TIMEOUT_IN_MILLIS";

    @Inject
    DeviceServicePresenter servicePresenter;

    @Override
    public void onCreate() {
        super.onCreate();
        setUpComponent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        servicePresenter.stopAllTask();
    }

    private void setUpComponent() {
        DaggerDeviceServiceComponent
                .builder()
                .deviceServiceModule(new DeviceServiceModule(this))
                .build()
                .inject(this);
    }

    @Override
    public DeviceService getService() {
        return this;
    }

    @Override
    protected void onStartCheckIntent(Intent intent) {
        super.onStartCheckIntent(intent);
        Timber.i("onStartCheckIntent: %s", intent.toString());
        servicePresenter.getBarcodeService().initConfig(intent);
    }

    @Override
    public BarcodeService getBarCodeService() {
        return servicePresenter.getBarcodeService();
    }
}
