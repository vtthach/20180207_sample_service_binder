package cbsa.device.barcode.exception;


/**
 * Exception occur when forget to init socket connection via socket.connect(...) method
 */
public class BarcodeDeviceNotConnectError extends Throwable {

    public BarcodeDeviceNotConnectError() {
        super("Device not connect");
    }
}
