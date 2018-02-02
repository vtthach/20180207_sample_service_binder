package cbsa.device.barcode.sdk.v2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cbsa.device.barcode.exception.BarCodeInputDataException;
import cbsa.device.barcode.exception.BarCodeOutputDataError;
import cbsa.device.barcode.exception.BarcodeDeviceDisconnectError;
import cbsa.device.barcode.exception.BarcodeDeviceNotConnectError;
import cbsa.device.barcode.sdk.SocketClientStatus;
import cbsa.device.barcode.sdk.SocketStatusListener;
import cbsa.device.barcode.sdk.State;
import timber.log.Timber;

public class BarcodeScannerWrapper {

    private final byte[] sendBuffer = new byte[]{3, 2};

    private final State state;
    String ipAddress;
    int port;
    int timeoutInMillis;
    boolean isContinueListenData;

    private SocketStatusListener socketStatusListener;

    public BarcodeScannerWrapper() {
        state = State.create(sendBuffer, 1024);
    }

    public void connect() throws IOException {
        state.connect(ipAddress, port, timeoutInMillis);
    }

    public synchronized void startListener() throws BarcodeDeviceNotConnectError, BarcodeDeviceDisconnectError {
        isContinueListenData = true;
        tryConnect();
        registerData();
        while (isContinueListenData) {
            try {
                receiveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String rs = null;
    }

    private void receiveData() throws IOException, BarCodeOutputDataError {
        this.socketStatusListener.onStatusChange(SocketClientStatus.Receiving);
        int receivedBytes = state.receive();
        if (receivedBytes > 0) {
            socketStatusListener.onReceive(parseData(state.getReceiveBuffer()));
        } else {
            socketStatusListener.onError("");
            throw new BarCodeOutputDataError();
        }
    }

    private String parseData(byte[] receiveBuffer) throws UnsupportedEncodingException {
        String rs = null;
        if (receiveBuffer != null) {
            String barcodeString = new String(receiveBuffer, "UTF-8");
            Timber.i("onReceiveData : " + barcodeString)
            if (!barcodeString.equals("")) {
                String[] stringsResult = barcodeString.split("\r");
                rs = stringsResult.length > 0 ? stringsResult[0] : null;
            }
        }
        return rs;
    }

    private void registerData() throws BarcodeDeviceDisconnectError {
        int bytesSent = 0;
        try {
            bytesSent = state.send();
        } catch (IOException e) {
            Timber.e(e, "vtt - IOException exception");
            this.socketStatusListener.onError(e.getMessage());
            this.closeState(state);
            throw new BarcodeDeviceDisconnectError();
        }
        if (bytesSent < 1) {
            this.socketStatusListener.onError("Nothing sent!");
            disconnectIfAny();
            throw new BarCodeInputDataException();
        }
    }

    private void tryConnect() throws BarcodeDeviceNotConnectError {
        if (!state.isConnected()) {
            disconnectIfAny();
            try {
                connect();
            } catch (IOException e) {
                throw new BarcodeDeviceNotConnectError();
            }
        }
    }

    private void closeState(State state) {
        try {
            state.close();
        } catch (IOException e) {
            Timber.e(e, "close State error");
        }
    }

    public void disconnectIfAny() {
        if (state != null) {
            closeState(state);
        }
    }
}
