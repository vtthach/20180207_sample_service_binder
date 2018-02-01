package cbsa.device.barcode.sdk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cbsa.device.barcode.exception.BarCodeOutputDataException;
import cbsa.device.barcode.exception.BarcodeDeviceNotConnectException;
import cbsa.device.barcode.exception.BarCodeInputDataException;

public class SocketClientImpl implements SocketClient {
    private SocketStatusListener socketStatusListener;

    public SocketClientImpl() {
    }

    public void setSocketStatusListener(SocketStatusListener socketStatusListener) {
        this.socketStatusListener = socketStatusListener;
    }

    public String send(String ipAddress, int port, int connectionTimeout, byte[] sendBuffer) throws IOException, BarcodeDeviceNotConnectException {
        this.socketStatusListener.onStatusChange(SocketClientStatus.Connecting);
        State state = State.create(sendBuffer, 1024);

        try {
            state.connect(ipAddress, port, connectionTimeout);
            if (!state.isConnected()) {
                this.socketStatusListener.onError("Socket not connected!");
                this.closeState(state);
                throw new BarcodeDeviceNotConnectException();
            }

            this.socketStatusListener.onStatusChange(SocketClientStatus.Sending);
            int bytesSent = state.send();
            if (bytesSent < 1) {
                this.socketStatusListener.onError("Nothing sent!");
                this.closeState(state);
                throw new BarCodeInputDataException();
            }
        } catch (IOException var7) {
            this.socketStatusListener.onError(var7.getMessage());
            this.closeState(state);
            throw var7;
        }
        String barcode = handleReceiveBytes(state);
        closeState(state);
        return barcode;
    }

    private String handleReceiveBytes(State state) throws IOException {
        this.socketStatusListener.onStatusChange(SocketClientStatus.Receiving);
        int receivedBytes;
        try {
            receivedBytes = state.receive();
            if (receivedBytes > 0) {
                return this.assertReceiveBuffer(state.getReceiveBuffer());
            } else {
                throw new BarCodeOutputDataException();
            }
        } catch (IOException ioe) {
            this.socketStatusListener.onError(ioe.getMessage());
            this.closeState(state);
            throw ioe;
        }
    }

    private String assertReceiveBuffer(byte[] buffer) throws UnsupportedEncodingException {
        String rs = null;
        if (buffer != null) {
            String barcodeString = new String(buffer, "UTF-8");
            if (!barcodeString.equals("")) {
                String[] stringsResult = barcodeString.split("\r");
                rs = stringsResult.length > 0 ? stringsResult[0] : null;
                this.socketStatusListener.onReceive(rs);
            }
        }
        return rs;
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