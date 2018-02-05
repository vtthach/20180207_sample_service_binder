package cbsa.device.injection;


import javax.inject.Singleton;

import cbsa.device.barcode.sdk.CardScannerService;
import cbsa.device.barcode.sdk.CardScannerServiceImpl;
import cbsa.device.barcode.sdk.SocketClient;
import cbsa.device.barcode.sdk.SocketClientImpl;
import cbsa.device.barcode.sdk.v2.BarcodeScannerWrapper;
import cbsa.device.barcode.sdk.v2.BarcodeScannerWrapperImpl;
import cbsa.device.barcode.sdk.v2.CardScannerServiceV2;
import cbsa.device.barcode.sdk.v2.CardScannerServiceV2Impl;
import cbsa.device.barcode.service.BarcodeService;
import cbsa.device.barcode.service.BarcodeServiceV2Impl;
import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public class BarcodeServiceV2Module {

    private String ipAddress;
    private int port;
    private int connectionTimeout;

    public BarcodeServiceV2Module(String ipAddress, int port, int connectionTimeout) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.connectionTimeout = connectionTimeout;
    }

    @Provides
    @Singleton
    BarcodeService provideBarCodeServiceV2(BarcodeServiceV2Impl barcodeService) {
        return barcodeService;
    }

    @Provides
    @Singleton
    CardScannerServiceV2 provideCardScannerServiceV2(BarcodeScannerWrapper scanWrapper) {
        return new CardScannerServiceV2Impl(scanWrapper, ipAddress, port, connectionTimeout);
    }

    @Provides
    @Singleton
    BarcodeScannerWrapper provideBarcodeScannerWrapper() {
        return new BarcodeScannerWrapperImpl(ipAddress, port, connectionTimeout);
    }
}
