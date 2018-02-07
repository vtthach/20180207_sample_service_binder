package cbsa.device.barcode.sdk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicBoolean;

import cbsa.device.barcode.exception.InputDataError;
import cbsa.device.barcode.exception.ReceiveDataError;
import cbsa.device.barcode.exception.DisconnectError;
import cbsa.device.barcode.exception.CannotConnectError;
import cbsa.device.barcode.exception.BarcodeScannerException;
import timber.log.Timber;

import static cbsa.device.Constant.LOG_TAG;

public class BarcodeScannerWrapperImpl implements BarcodeScannerWrapper {

    private State autoListenerState;
    private final BarcodeScannerConfig config;

    private SocketStatusListener socketStatusListener;
    private AtomicBoolean isRunning = new AtomicBoolean();
    private State manualState;

    public BarcodeScannerWrapperImpl(BarcodeScannerConfig config) {
        this.config = config;
    }

    private State getState() {
        byte[] sendBuffer = new byte[]{3, 2};
        return State.create(sendBuffer, 1024);
    }

    private void connect(State listenerState) throws IOException {
        listenerState.connect(config.getIpAddress(), config.getPort(), config.getTimeoutInMillis());
    }

    public void startListener(SocketStatusListener listener) {
        Timber.i(LOG_TAG + "-> startListener");
        this.socketStatusListener = listener;
        isRunning.set(true);
        autoListenerState = getState();
        startAutoReceiveData(isRunning, autoListenerState, socketStatusListener);
    }

    private void startAutoReceiveData(AtomicBoolean isRunning, State autoListenerState, SocketStatusListener statusListener) {
        try {
            onStartListener(autoListenerState);
            while (isRunning.get() && !Thread.interrupted() && autoListenerState.isConnected()) {
                receiveData(autoListenerState, statusListener);
            }
        } catch (BarcodeScannerException e) {
            Timber.d(e, LOG_TAG + "startListener error : " + e.getMessage());
            notifyError(statusListener, e.getMessage());
        } finally {
            onEndListener(autoListenerState, statusListener);
        }
    }

    private String startManualReceiveData(State state) {
        String rs = null;
        try {
            onStartListener(state);
            rs = receiveData(state, null);
        } catch (BarcodeScannerException e) {
            Timber.d(e, LOG_TAG + "startListener error : " + e.getMessage());
        } finally {
            onEndListener(state, null);
        }
        return rs;
    }

    @Override
    public void stopListener() {
        isRunning.set(false);
        closeState(autoListenerState);
    }

    private void notifyError(SocketStatusListener statusListener, String message) {
        if (statusListener != null && isRunning.get()) {
            statusListener.onError(message);
        }
    }

    @Override
    public boolean isOnline() throws DisconnectError {
        State onlineState = State.create(null, 0);
        boolean isConnected;
        try {
            onlineState.connect(config.getIpAddress(), config.getPort(), config.getTimeoutInMillis());
            isConnected = onlineState.isConnected();
        } catch (IOException var10) {
            throw new DisconnectError();
        } finally {
            this.closeState(onlineState);
        }
        return isConnected;
    }

    @Override
    public String scan() {
        closeStateIfAny(manualState);
        manualState = getState();
        return startManualReceiveData(manualState);
    }

    @Override
    public BarcodeScannerConfig getConfig() {
        return config;
    }

    private void onStartListener(State listenerState) throws BarcodeScannerException {
        tryConnect(listenerState);
        registerData(listenerState);
    }

    private void onEndListener(State autoListenerState, SocketStatusListener statusListener) {
        closeStateIfAny(autoListenerState);
        notifyStatusChange(SocketClientStatus.End, statusListener);
    }

    private void notifyStatusChange(SocketClientStatus status, SocketStatusListener statusListener) {
        if (statusListener != null && isRunning.get()) {
            statusListener.onStatusChange(status);
        }
    }

    private String receiveData(State autoListenerState, SocketStatusListener statusListener) throws ReceiveDataError {
        String data;
        try {
            Timber.i(LOG_TAG + "->>>> Receive data START");
            byte[] bytes = autoListenerState.receiveBytes();
            data = parseData(bytes);
            if (statusListener != null) {
                statusListener.onReceive(data);
            }
            Timber.i(LOG_TAG + "-<<<< Receive data END");
        } catch (IOException e) {
            throw new ReceiveDataError();
        }
        return data;
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

    private void registerData(State listenerState) throws BarcodeScannerException {
        int bytesSent;
        try {
            bytesSent = listenerState.send();
        } catch (IOException e) {
            Timber.e(e, LOG_TAG + "registerData error: " + e.getMessage());
            throw new DisconnectError();
        }
        if (bytesSent < 1) {
            Timber.e(LOG_TAG + "registerData error: bytesSent < 1");
            throw new InputDataError();
        }
    }

    private void tryConnect(State listenerState) throws CannotConnectError {
        if (!listenerState.isConnected()) {
            try {
                connect(listenerState);
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

    public void closeStateIfAny(State state) {
        if (state != null) {
            closeState(state);
        }
    }
}
