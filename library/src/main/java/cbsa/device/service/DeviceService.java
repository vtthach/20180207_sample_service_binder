package cbsa.device.service;


import cbsa.device.barcode.service.BarcodeService;

public interface DeviceService {
    BarcodeService getBarCodeService();
}
