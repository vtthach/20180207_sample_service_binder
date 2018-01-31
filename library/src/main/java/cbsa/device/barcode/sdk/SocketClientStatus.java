package cbsa.device.barcode.sdk;

public enum SocketClientStatus {
    None,
    Connecting,
    Sending,
    Receiving,
    socketStatusListener,
    Error;

    private SocketClientStatus() {
    }
}
