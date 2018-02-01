package cbsa.device.barcode.service;


import cbsa.device.barcode.ResultCallback;

public interface BarcodeService extends BaseDeviceService {
    void scan(ResultCallback<String> resultCallback);

    void searchDevices();
}
