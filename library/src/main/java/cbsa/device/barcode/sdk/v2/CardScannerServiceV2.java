package cbsa.device.barcode.sdk.v2;


import cbsa.device.barcode.exception.DisconnectError;

public interface CardScannerServiceV2 {
    void startListener();

    void stopListener();

    boolean isOnline() throws DisconnectError;
}
