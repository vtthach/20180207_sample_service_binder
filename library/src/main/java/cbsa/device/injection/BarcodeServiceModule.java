package cbsa.device.injection;


import javax.inject.Singleton;

import cbsa.device.barcode.sdk.BarcodeScannerConfig;
import cbsa.device.barcode.sdk.BarcodeScannerWrapper;
import cbsa.device.barcode.sdk.BarcodeScannerWrapperImpl;
import cbsa.device.barcode.service.BarcodeService;
import cbsa.device.barcode.service.BarcodeServiceV2Impl;
import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public class BarcodeServiceModule {

    @Provides
    @Singleton
    BarcodeService provideBarCodeServiceV2(BarcodeServiceV2Impl barcodeService) {
        return barcodeService;
    }

    @Provides
    @Singleton
    BarcodeScannerWrapper provideBarcodeScannerWrapper(BarcodeScannerConfig config) {
        return new BarcodeScannerWrapperImpl(config);
    }

    @Provides
    @Singleton
    BarcodeScannerConfig provideBarcodeScannerConfig() {
        return BarcodeScannerConfig.getDefaultConfig();
    }
}
