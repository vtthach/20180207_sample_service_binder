package cbsa.device.presenter;


import cbsa.device.barcode.service.BarcodeService;

public interface DeviceServicePresenter {
    BarcodeService getBarcodeService();

    void stopAllTask();
}
