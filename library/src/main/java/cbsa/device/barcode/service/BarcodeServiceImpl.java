package cbsa.device.barcode.service;


import java.io.IOException;

import javax.inject.Inject;

import cbsa.device.barcode.ResultCallback;
import cbsa.device.barcode.exception.BarcodeScannerException;
import cbsa.device.barcode.sdk.CardScannerService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class BarcodeServiceImpl implements BarcodeService {

    private DisposableObserver<String> scanDisposal;
    private final CardScannerService cardScannerService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public BarcodeServiceImpl(CardScannerService cardScannerService) {
        this.cardScannerService = cardScannerService;
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
        disposePreviousRequest();
        scanDisposal = getScanResultDisposal(resultCallback);
        getScanResultObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scanDisposal);
        compositeDisposable.add(scanDisposal);
    }

    @Override
    public void startListener(DisposableObserver<String> subscriber) {
        // Do nothing
    }

    @Override
    public void stopListener() {
        // Do nothing
    }

    @Override
    public void updateConfig(String text, String text1) {
        // Do nothing
    }

    private void disposePreviousRequest() {
        if (scanDisposal != null) {
            scanDisposal.dispose();
        }
    }

    private DisposableObserver<String> getScanResultDisposal(final ResultCallback<String> resultCallback) {
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

    private String getScanResult() throws IOException, BarcodeScannerException {
        return cardScannerService.scan();
    }
}
