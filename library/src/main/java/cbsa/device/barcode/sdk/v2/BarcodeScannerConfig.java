package cbsa.device.barcode.sdk.v2;

public class BarcodeScannerConfig {
    public String ipAddress;
    public int port;
    public int timeoutInMillis;

    public BarcodeScannerConfig(String ipAddress, int port, int timeoutInMillis) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.timeoutInMillis = timeoutInMillis;
    }

    public void update(String ip, int port) {
        this.ipAddress = ip;
        this.port = port;
    }
}
