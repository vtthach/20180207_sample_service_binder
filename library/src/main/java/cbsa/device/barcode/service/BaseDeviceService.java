package cbsa.device.barcode.service;


import cbsa.device.barcode.ResultCallback;

public interface BaseDeviceService {
    void isConnected(ResultCallback<Boolean> callback);
    void stopAllTask();
}
