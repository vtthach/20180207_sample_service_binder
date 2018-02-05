package cbsa.device.barcode.sdk;

public enum SocketClientStatus {
    None,
    Connecting,
    Sending,
    Receiving,
    Error,
    End;

    SocketClientStatus() {
        // Do nothing
    }
}
