package cbsa.device.barcode.exception;


/**
 * Exception occur when forget to init socket connection via socket.connect(...) method
 */
public class CannotConnectError extends BarcodeScannerException {

    public CannotConnectError() {
        super("Device not connect");
    }
}
