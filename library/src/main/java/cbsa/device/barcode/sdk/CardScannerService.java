package cbsa.device.barcode.sdk;


import java.io.IOException;


public interface CardScannerService {
    void setSocketStatusListener(SocketStatusListener paramSocketStatusListener);

    void scan() throws IOException;

    boolean isOnline();
}
