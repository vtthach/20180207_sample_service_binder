package cbsa.device.barcode.sdk;


import cbsa.device.barcode.exception.DisconnectError;

public interface BarcodeScannerWrapper {
    void startListener(SocketStatusListener listener);

    void stopListener();

    boolean isOnline() throws DisconnectError;

    String scan();

    BarcodeScannerConfig getConfig();
}
