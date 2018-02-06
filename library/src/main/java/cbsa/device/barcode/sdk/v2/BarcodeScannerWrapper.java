package cbsa.device.barcode.sdk.v2;


import cbsa.device.barcode.exception.DisconnectError;
import cbsa.device.barcode.sdk.SocketStatusListener;

public interface BarcodeScannerWrapper {
    void startListener(SocketStatusListener listener);

    void stopListener();

    boolean isOnline() throws DisconnectError;

    String scan();
}
