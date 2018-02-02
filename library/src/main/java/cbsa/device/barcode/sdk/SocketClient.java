package cbsa.device.barcode.sdk;

import java.io.IOException;

import cbsa.device.barcode.exception.BarcodeDeviceNotConnectError;

public interface SocketClient {
    void setSocketStatusListener(SocketStatusListener paramSocketStatusListener);

    String send(String paramString, int paramInt1, int paramInt2, byte[] paramArrayOfByte) throws IOException, BarcodeDeviceNotConnectError;

    boolean isOnline(String paramString, int paramInt1, int paramInt2);
}
