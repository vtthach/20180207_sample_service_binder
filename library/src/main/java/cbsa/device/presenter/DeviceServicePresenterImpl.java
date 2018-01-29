package cbsa.device.presenter;


import javax.inject.Inject;

import cbsa.device.barcode.service.BarcodeService;

public class DeviceServicePresenterImpl implements DeviceServicePresenter {

    BarcodeService barcodeService;

    @Inject
    public DeviceServicePresenterImpl(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    @Override
    public BarcodeService getBarcodeService() {
        return barcodeService;
    }

    @Override
    public void stopAllTask() {
        barcodeService.stopAllTask();
    }
}
