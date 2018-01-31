package cbsa.device.barcode.sdk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SocketClientImpl implements SocketClient {
    private SocketStatusListener socketStatusListener;

    public SocketClientImpl() {
    }

    public void setSocketStatusListener(SocketStatusListener socketStatusListener) {
        this.socketStatusListener = socketStatusListener;
    }

    public void send(String ipAddress, int port, int connectionTimeout, byte[] sendBuffer) throws IOException {
        this.socketStatusListener.onStatusChange(SocketClientStatus.Connecting);
        State state = State.create(sendBuffer, 1024);

        try {
            state.connect(ipAddress, port, connectionTimeout);
            if (!state.isConnected()) {
                this.socketStatusListener.onError("Socket not connected!");
                this.closeState(state);
                return;
            }

            this.socketStatusListener.onStatusChange(SocketClientStatus.Sending);
            int bytesSent = state.send();
            if (bytesSent < 1) {
                this.socketStatusListener.onError("Nothing sent!");
                this.closeState(state);
                return;
            }
        } catch (IOException var7) {
            this.socketStatusListener.onError(var7.getMessage());
            this.closeState(state);
            return;
        }

        this.handleReceiveBytes(state);
        this.closeState(state);
    }

    private void handleReceiveBytes(State state) throws IOException {
        this.socketStatusListener.onStatusChange(SocketClientStatus.Receiving);

        int receivedBytes;
        try {
            receivedBytes = state.receive();
        } catch (IOException var4) {
            this.socketStatusListener.onError(var4.getMessage());
            this.closeState(state);
            return;
        }

        if (receivedBytes > 0) {
            this.assertReceiveBuffer(state.getReceiveBuffer());
        }

    }

    private void assertReceiveBuffer(byte[] buffer) throws UnsupportedEncodingException {
        if (buffer != null) {
            String barcodeString = new String(buffer, "UTF-8");
            if (!barcodeString.equals("")) {
                String[] stringsResult = barcodeString.split("\r");
                String barcode = stringsResult.length > 0 ? stringsResult[0] : null;
                this.socketStatusListener.onReceive(barcode);
            }
        }
    }

    private void closeState(State state) {
        try {
            state.close();
        } catch (IOException var3) {
            this.socketStatusListener.onError(var3.getMessage());
        }

    }

    public boolean isOnline(String ipAddress, int port, int connectionTimeout) {
        this.socketStatusListener.onStatusChange(SocketClientStatus.Connecting);
        State state = State.create(null, 0);
        boolean isConnected = false;

        try {
            state.connect(ipAddress, port, connectionTimeout);
            isConnected = state.isConnected();
        } catch (IOException var10) {
            this.socketStatusListener.onError(var10.getMessage());
        } finally {
            this.closeState(state);
        }

        return isConnected;
    }
}