package cbsa.device.barcode.service;


import java.io.IOException;

import javax.inject.Inject;

import cbsa.device.barcode.ResultCallback;
import cbsa.device.barcode.sdk.CardScannerService;
import cbsa.device.barcode.sdk.SocketClient;
import cbsa.device.barcode.sdk.SocketClientStatus;
import cbsa.device.barcode.sdk.SocketStatusListener;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static cbsa.device.Constant.LOG_TAG;

public class BarcodeServiceImpl implements BarcodeService {

    private final SocketClient socketClient;
    private final CardScannerService cardScannerService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SocketStatusListener socketStatusListener = new SocketStatusListener() {
        @Override
        public void onStatusChange(SocketClientStatus socketClientStatus) {
            Timber.i(LOG_TAG + "onStatusChange: " + socketClientStatus);
        }

        @Override
        public void onReceive(String s) {
            Timber.i(LOG_TAG + "onReceive: " + s);
        }

        @Override
        public void onError(String s) {
            Timber.i(LOG_TAG + "onError: " + s);
        }
    };

    @Inject
    public BarcodeServiceImpl(SocketClient socketClient,
                              CardScannerService cardScannerService) {
        this.socketClient = socketClient;
        this.cardScannerService = cardScannerService;
        this.cardScannerService.setSocketStatusListener(socketStatusListener);
    }

    @Override
    public void isConnected(ResultCallback<Boolean> callback) {
        DisposableObserver<Boolean> disposal = getIsConnectedDisposal(callback);
        getDeviceStateObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposal);
        compositeDisposable.add(disposal);
    }

    @Override
    public void stopAllTask() {
        compositeDisposable.clear();
    }

    private DisposableObserver<Boolean> getIsConnectedDisposal(ResultCallback<Boolean> callback) {
        return new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                callback.onSuccess(aBoolean);
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private Observable<Boolean> getDeviceStateObservable() {
        return Observable.defer(() -> Observable.just(getCardScannerDeviceState()));
    }

    private boolean getCardScannerDeviceState() {
        return cardScannerService.isOnline();
    }

    @Override
    public void scan(ResultCallback<String> resultCallback) {
        DisposableObserver<String> disposal = getScanResultDisposal(resultCallback);
        getScanResultObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposal);
        compositeDisposable.add(disposal);
    }

    private DisposableObserver<String> getScanResultDisposal(ResultCallback<String> resultCallback) {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String result) {
                resultCallback.onSuccess(result);
            }

            @Override
            public void onError(Throwable e) {
                resultCallback.onError(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private Observable<String> getScanResultObservable() {
        return Observable.defer(() -> Observable.just(getScanResult()));
    }

    private String getScanResult() throws IOException {
        cardScannerService.scan();
        return "TODO need update library";
    }
}
