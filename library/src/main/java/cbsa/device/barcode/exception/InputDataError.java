package cbsa.device.barcode.exception;


/**
 * Exception occur when input byteBuffer used for outputStream is invalid
 */
public class InputDataError extends BarcodeScannerException {
    public InputDataError() {
        super("Input data in request buffer invalid");
    }
}
