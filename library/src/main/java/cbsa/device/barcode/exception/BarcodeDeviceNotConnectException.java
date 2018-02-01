package cbsa.device.barcode.exception;


/**
 * Exception occur when forget to init socket connection via socket.connect(...) method
 */
public class BarcodeDeviceNotConnectException extends RuntimeException {
    // Use later

    public BarcodeDeviceNotConnectException() {
        super("Device not connect");
    }
}
