package cbsa.device.barcode.sdk.v2;


import cbsa.device.barcode.exception.DisconnectError;
import io.reactivex.observers.DisposableObserver;

public interface CardScannerServiceV2 {
    void startListener(DisposableObserver<String> subscriber);

    void stopListener();

    boolean isOnline() throws DisconnectError;
}
