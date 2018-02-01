package cbsa.device.barcode.exception;


/**
 * Exception occur when input byteBuffer used for outputStream is invalid
 */
public class BarCodeInputDataException extends RuntimeException {
    public BarCodeInputDataException() {
        super("Input data in request buffer invalid");
    }
}
