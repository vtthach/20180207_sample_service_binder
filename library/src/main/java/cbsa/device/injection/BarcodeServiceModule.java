package cbsa.device.injection;


import javax.inject.Singleton;

import cbsa.cardscanner.CardScannerService;
import cbsa.cardscanner.CardScannerServiceImpl;
import cbsa.cardscanner.SocketClient;
import cbsa.cardscanner.SocketClientImpl;
import cbsa.device.barcode.service.BarcodeService;
import cbsa.device.barcode.service.BarcodeServiceImpl;
import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public class BarcodeServiceModule {

    private String ipAddress;
    private int port;
    private int connectionTimeout;

    public BarcodeServiceModule(String ipAddress, int port, int connectionTimeout) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.connectionTimeout = connectionTimeout;
    }

    @Provides
    @Singleton
    BarcodeService provideBarCodeService(BarcodeServiceImpl barcodeService) {
        return barcodeService;
    }

    @Provides
    @Singleton
    SocketClient provideSocketClient() {
        return new SocketClientImpl();
    }

    @Provides
    @Singleton
    CardScannerService provideCardScannerService(SocketClient socketClient) {
        return new CardScannerServiceImpl(socketClient, ipAddress, port, connectionTimeout);
    }
}
