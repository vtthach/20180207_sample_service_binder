package cbsa.device.barcode.sdk.v2.device;

import io.reactivex.Observable;

public interface BarcodeScanner extends Device {
    String getPrinterIp();

    Observable<BarcodeScannerResponse> scan(final BarcodeScannerRequest card);
}