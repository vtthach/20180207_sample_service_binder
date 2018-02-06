package cbsa.device.barcode.service;


import cbsa.device.barcode.ResultCallback;
import io.reactivex.observers.DisposableObserver;

public interface BarcodeService extends BaseDeviceService {
    void scan(ResultCallback<String> resultCallback);

    void startListener(DisposableObserver<String> subscriber);

    void stopListener();
}
