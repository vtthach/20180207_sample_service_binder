package cbsa.device.barcode.sdk;

import java.io.IOException;

import cbsa.device.barcode.exception.BarcodeDeviceNotConnectException;

public interface SocketClient {
    void setSocketStatusListener(SocketStatusListener paramSocketStatusListener);

    String send(String paramString, int paramInt1, int paramInt2, byte[] paramArrayOfByte) throws IOException, BarcodeDeviceNotConnectException;

    boolean isOnline(String paramString, int paramInt1, int paramInt2);
}
