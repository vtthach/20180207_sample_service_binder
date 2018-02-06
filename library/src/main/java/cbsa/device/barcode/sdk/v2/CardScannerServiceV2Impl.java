package cbsa.device.barcode.sdk.v2;


import cbsa.device.barcode.exception.DisconnectError;
import cbsa.device.barcode.sdk.SocketClientStatus;
import cbsa.device.barcode.sdk.SocketStatusListener;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

/**
 * Decompile from jar
 */
public class CardScannerServiceV2Impl implements CardScannerServiceV2, SocketStatusListener {
    private final BarcodeScannerWrapper scannerWrapper;
    private final String ipAddress;
    private final int port;
    private final int connectionTimeout;
    private String latestScanResult;

    private PublishSubject<String> publisher;

    public CardScannerServiceV2Impl(BarcodeScannerWrapper scannerWrapper, String ipAddress, int port, int connectionTimeout) {
        this.scannerWrapper = scannerWrapper;
        this.ipAddress = ipAddress;
        this.port = port;
        this.connectionTimeout = connectionTimeout;
        publisher = PublishSubject.create();
    }

    public boolean isOnline() throws DisconnectError {
        return scannerWrapper.isOnline();
    }

    @Override
    public void onStatusChange(SocketClientStatus var1) {
        Timber.i("onStatusChange :" + var1);
    }

    @Override
    public void onReceive(String var1) {
        Timber.i("onReceive :" + var1);
        publisher.onNext(var1);
    }

    @Override
    public void onError(String var1) {
        Timber.i("onError :" + var1);
        publisher.onError(new Throwable(var1));
    }

    @Override
    public void startListener(DisposableObserver<String> subscriber) {
        publisher = PublishSubject.create();
        publisher.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(subscriber);
        scannerWrapper.startListener(this);
    }

    @Override
    public void stopListener() {
        scannerWrapper.stopListener();
    }
}
