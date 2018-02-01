package cbsa.device.barcode.exception;


/**
 * Exception occur when read byte data form input stream of socket
 */
public class BarCodeOutputDataException extends RuntimeException {
    public BarCodeOutputDataException() {
        super("Output data empty or invalid");
    }
}
