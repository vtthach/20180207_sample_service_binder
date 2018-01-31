package cbsa.device.barcode.sdk;


public interface SocketStatusListener {
    void onStatusChange(SocketClientStatus var1);

    void onReceive(String var1);

    void onError(String var1);
}
