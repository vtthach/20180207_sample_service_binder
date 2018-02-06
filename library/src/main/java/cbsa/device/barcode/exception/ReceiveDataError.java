package cbsa.device.barcode.exception;


/**
 * Exception occur when read byte data form input stream of socket
 */
public class ReceiveDataError extends BarcodeScannerException {
    public ReceiveDataError() {
        super("Device disconnected while reading data");
    }
}
