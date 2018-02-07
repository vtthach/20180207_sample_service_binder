package cbsa.device.barcode.sdk;

import timber.log.Timber;

public class BarcodeScannerConfig {

    private String ipAddress;
    private int port;
    private int timeoutInMillis;

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public int getTimeoutInMillis() {
        return timeoutInMillis;
    }

    public BarcodeScannerConfig(String ipAddress, int port, int timeoutInMillis) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.timeoutInMillis = timeoutInMillis;
    }

    public void update(String ip, int port) {
        this.ipAddress = ip;
        this.port = port;
    }

    public static BarcodeScannerConfig getDefaultConfig() {
        return new BarcodeScannerConfig("", 0, 5000);
    }

    public void update(String ipAddress, int port, int timeout) {
        Timber.i("Update config: %s, %d , %d", ipAddress, port, timeout);
        this.ipAddress = ipAddress;
        this.port = port;
        this.timeoutInMillis = timeout;
    }
}
