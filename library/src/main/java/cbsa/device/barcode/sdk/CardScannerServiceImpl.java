package cbsa.device.barcode.sdk;


import java.io.IOException;

public class CardScannerServiceImpl implements CardScannerService {
    private final SocketClient socketClient;
    private final String ipAddress;
    private final int port;
    private final int connectionTimeout;

    public CardScannerServiceImpl(SocketClient socketClient, String ipAddress, int port, int connectionTimeout) {
        this.socketClient = socketClient;
        this.ipAddress = ipAddress;
        this.port = port;
        this.connectionTimeout = connectionTimeout;
    }

    public void setSocketStatusListener(SocketStatusListener socketStatusListener) {
        this.socketClient.setSocketStatusListener(new SocketListener(socketStatusListener));
    }

    public void scan() throws IOException {
        final byte[] etxStx = {3, 2};
        this.socketClient.send(this.ipAddress, this.port, this.connectionTimeout, etxStx);
    }

    public boolean isOnline() {
        return this.socketClient.isOnline(this.ipAddress, this.port, this.connectionTimeout);
    }
}
