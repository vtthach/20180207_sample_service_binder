package cbsa.device.barcode.sdk;


import java.io.IOException;


public interface CardScannerService {
    void setSocketStatusListener(SocketStatusListener paramSocketStatusListener);

    String scan() throws IOException;

    boolean isOnline();
}
