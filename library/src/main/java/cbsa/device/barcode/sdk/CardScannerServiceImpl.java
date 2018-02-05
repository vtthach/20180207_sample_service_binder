package cbsa.device.barcode.sdk;


import java.io.IOException;

import cbsa.device.barcode.exception.BarcodeScannerException;
import timber.log.Timber;

import static cbsa.device.Constant.LOG_TAG;

/**
 * Decompile from jar
 */
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
        initSocketListener(socketClient);
    }

    private void initSocketListener(SocketClient socketClient) {
        SocketStatusListener socketStatusListener = new SocketStatusListener() {
            @Override
            public void onStatusChange(SocketClientStatus socketClientStatus) {
                Timber.i(LOG_TAG + "onStatusChange: " + socketClientStatus);
            }

            @Override
            public void onReceive(String s) {
                Timber.i(LOG_TAG + "onReceive: " + s);
            }

            @Override
            public void onError(String s) {
                Timber.i(LOG_TAG + "onError: " + s);
            }
        };
        socketClient.setSocketStatusListener(socketStatusListener);
    }

    public String scan() throws IOException, BarcodeScannerException {
        final byte[] etxStx = {3, 2};
        return this.socketClient.send(this.ipAddress, this.port, this.connectionTimeout, etxStx);
    }

    public boolean isOnline() {
        return this.socketClient.isOnline(this.ipAddress, this.port, this.connectionTimeout);
    }
}
