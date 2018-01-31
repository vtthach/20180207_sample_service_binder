package cbsa.device.barcode.sdk;

import java.io.IOException;

public interface SocketClient {
    void setSocketStatusListener(SocketStatusListener paramSocketStatusListener);

    void send(String paramString, int paramInt1, int paramInt2, byte[] paramArrayOfByte) throws IOException;

    String scan(String paramString, int paramInt1, int paramInt2, byte[] paramArrayOfByte) throws IOException;

    boolean isOnline(String paramString, int paramInt1, int paramInt2);
}
