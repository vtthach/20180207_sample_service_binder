package cbsa.device.service;


import android.content.Intent;

import javax.inject.Inject;

import cbsa.device.barcode.service.BarcodeService;
import cbsa.device.injection.BarcodeServiceModule;
import cbsa.device.injection.BarcodeServiceV2Module;
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
                .barcodeServiceV2Module(getBarCodeServiceModule())
                .deviceServiceModule(new DeviceServiceModule(this))
                .build()
                .inject(this);
    }

    private BarcodeServiceV2Module getBarCodeServiceModule() {
        // TODO remove hardcode when get define from BE
        return new BarcodeServiceV2Module("192.168.1.12", 20108, 5000);
    }

    @Override
    public DeviceService getService() {
        return this;
    }

    @Override
    protected void onStartCheckIntent(Intent intent) {
        super.onStartCheckIntent(intent);
        Timber.i("onStartCheckIntent: %s", intent.toString());

    }

    @Override
    public BarcodeService getBarCodeService() {
        return servicePresenter.getBarcodeService();
    }
}
