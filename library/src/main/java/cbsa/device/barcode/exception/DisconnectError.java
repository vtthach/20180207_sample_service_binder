package cbsa.device.barcode.exception;


public class DisconnectError extends BarcodeScannerException {
    public DisconnectError() {
        super("Device disconnected");
    }
}
