package cbsa.device.barcode.sdk.v2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import cbsa.device.barcode.exception.InputDataError;
import cbsa.device.barcode.exception.ReceiveDataError;
import cbsa.device.barcode.exception.DisconnectError;
import cbsa.device.barcode.exception.CannotConnectError;
import cbsa.device.barcode.exception.BarcodeScannerException;
import cbsa.device.barcode.sdk.SocketClientStatus;
import cbsa.device.barcode.sdk.SocketStatusListener;
import cbsa.device.barcode.sdk.State;
import timber.log.Timber;

import static cbsa.device.Constant.LOG_TAG;

public class BarcodeScannerWrapperImpl implements BarcodeScannerWrapper {

    private final State state;
    private final String ipAddress;
    private final int port;
    private final int timeoutInMillis;

    private SocketStatusListener socketStatusListener;

    public BarcodeScannerWrapperImpl(String ipAddress,
                                     int port,
                                     int timeoutInMillis) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.timeoutInMillis = timeoutInMillis;
        state = initState();
    }

    private State initState() {
        byte[] sendBuffer = new byte[]{3, 2};
        return State.create(sendBuffer, 1024);
    }

    private void connect() throws IOException {
        state.connect(ipAddress, port, timeoutInMillis);
    }

    public synchronized void startListener(SocketStatusListener listener) {
        Timber.i(LOG_TAG + "-> startListener");
        this.socketStatusListener = listener;
        try {
            onStartListener();
            while (state.isConnected()) {
                receiveData();
            }
        } catch (BarcodeScannerException e) {
            Timber.e(e, LOG_TAG + "startListener error : " + e.getMessage());
            notifyError(e.getMessage());
        } finally {
            onEndListener();
        }
    }

    public synchronized void stopListener() {
        disconnectIfAny();
    }

    private void notifyError(String message) {
        if (socketStatusListener != null) {
            socketStatusListener.onError(message);
        }
    }

    @Override
    public boolean isOnline() throws DisconnectError {
        State onlineState = State.create(null, 0);
        boolean isConnected;
        try {
            onlineState.connect(ipAddress, port, timeoutInMillis);
            isConnected = onlineState.isConnected();
        } catch (IOException var10) {
            throw new DisconnectError();
        } finally {
            this.closeState(onlineState);
        }
        return isConnected;
    }

    private void onStartListener() throws BarcodeScannerException {
        tryConnect();
        registerData();
    }

    private void onEndListener() {
        disconnectIfAny();
        notifyStatusChange(SocketClientStatus.End);
    }

    private void notifyStatusChange(SocketClientStatus status) {
        if (socketStatusListener != null) {
            socketStatusListener.onStatusChange(status);
        }
    }

    private void receiveData() throws ReceiveDataError {
        try {
            Timber.i("->>>> Receive data START");
            socketStatusListener.onReceive(parseData(state.receiveBytes()));
            Timber.i("-<<<< Receive data END");
        } catch (IOException e) {
            throw new ReceiveDataError();
        }
    }

    private String parseData(byte[] receiveBuffer) throws UnsupportedEncodingException {
        String rs = null;
        if (receiveBuffer != null) {
            String barcodeString = new String(receiveBuffer, "UTF-8");
            Timber.i(LOG_TAG + "parseData - raw data : " + barcodeString);
            if (!barcodeString.equals("")) {
                String[] stringsResult = barcodeString.split("\r");
                rs = stringsResult.length > 0 ? stringsResult[0] : null;
            }
        }
        return rs;
    }

    private void registerData() throws BarcodeScannerException {
        int bytesSent;
        try {
            bytesSent = state.send();
        } catch (IOException e) {
            Timber.e(e, LOG_TAG + "registerData error: " + e.getMessage());
            throw new DisconnectError();
        }
        if (bytesSent < 1) {
            Timber.e(LOG_TAG + "registerData error: bytesSent < 1");
            throw new InputDataError();
        }
    }

    private void tryConnect() throws CannotConnectError {
        if (!state.isConnected()) {
            try {
                connect();
            } catch (IOException e) {
                Timber.e(LOG_TAG + "tryConnect error: " + e.getMessage());
                throw new CannotConnectError();
            }
        }
    }

    private void closeState(State state) {
        try {
            state.close();
        } catch (IOException e) {
            Timber.e(e, LOG_TAG + "close State error");
        }
    }

    public void disconnectIfAny() {
        if (state != null) {
            closeState(state);
        }
    }
}
