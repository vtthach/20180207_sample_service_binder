package cbsa.device.barcode.exception;


/**
 * Exception occur when read byte data form input stream of socket
 */
public class BarCodeOutputDataError extends Throwable {
    public BarCodeOutputDataError() {
        super("Output data empty or invalid");
    }
}
