package cbsa.device.barcode.sdk;


public class SocketListener implements SocketStatusListener {
    private SocketStatusListener listener;

    public SocketListener(SocketStatusListener listener) {
        this.listener = listener;
    }

    public void onStatusChange(SocketClientStatus socketClientStatus) {
        this.listener.onStatusChange(socketClientStatus);
    }

    public void onReceive(String data) {
        this.listener.onReceive(data);
    }

    public void onError(String errorMessage) {
        this.listener.onError(errorMessage);
    }
}
