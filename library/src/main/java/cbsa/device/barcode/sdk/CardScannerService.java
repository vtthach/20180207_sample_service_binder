package cbsa.device.barcode.sdk;


import java.io.IOException;

import cbsa.device.barcode.exception.BarcodeScannerException;


public interface CardScannerService {
    String scan() throws IOException, BarcodeScannerException;

    boolean isOnline();
}
