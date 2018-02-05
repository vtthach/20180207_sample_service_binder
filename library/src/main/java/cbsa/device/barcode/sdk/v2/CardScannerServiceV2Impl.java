package cbsa.device.barcode.sdk.v2;


import java.io.IOException;

import cbsa.device.barcode.exception.BarcodeScannerException;
import cbsa.device.barcode.exception.DisconnectError;
import cbsa.device.barcode.sdk.CardScannerService;
import cbsa.device.barcode.sdk.SocketClientStatus;
import cbsa.device.barcode.sdk.SocketStatusListener;
import timber.log.Timber;

/**
 * Decompile from jar
 */
public class CardScannerServiceV2Impl implements CardScannerServiceV2, SocketStatusListener {
    private final BarcodeScannerWrapper scannerWrapper;
    private final String ipAddress;
    private final int port;
    private final int connectionTimeout;
    private String latestScanResult;

    public CardScannerServiceV2Impl(BarcodeScannerWrapper scannerWrapper, String ipAddress, int port, int connectionTimeout) {
        this.scannerWrapper = scannerWrapper;
        this.ipAddress = ipAddress;
        this.port = port;
        this.connectionTimeout = connectionTimeout;
    }

    public boolean isOnline() throws DisconnectError {
        return scannerWrapper.isOnline();
    }

    @Override
    public void onStatusChange(SocketClientStatus var1) {
        Timber.i("onStatusChange :" + var1);
    }

    @Override
    public void onReceive(String var1) {
        Timber.i("onReceive :" + var1);
    }

    @Override
    public void onError(String var1) {
        Timber.i("onError :" + var1);
    }

    @Override
    public void startListener() {
        scannerWrapper.startListener(this);
    }

    @Override
    public void stopListener() {
        scannerWrapper.stopListener();
    }
}
