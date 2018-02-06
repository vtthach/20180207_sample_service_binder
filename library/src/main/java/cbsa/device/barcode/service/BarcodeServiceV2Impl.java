package cbsa.device.barcode.service;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import cbsa.device.barcode.ResultCallback;
import cbsa.device.barcode.sdk.v2.BarcodeScannerWrapper;
import cbsa.device.barcode.sdk.v2.CardScannerServiceV2;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class BarcodeServiceV2Impl implements BarcodeService {

    private DisposableObserver<String> scanDisposal;
    private final BarcodeScannerWrapper barcodeScannerWrapper;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ExecutorService myExecutor;

    @Inject
    public BarcodeServiceV2Impl(BarcodeScannerWrapper barcodeScannerWrapper) {
        this.barcodeScannerWrapper = barcodeScannerWrapper;
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
        return Observable.defer(() -> Observable.just(barcodeScannerWrapper.isOnline()));
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
        myExecutor = buildCustomExecutor();
        DisposableObserver<Boolean> disposal = new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                Timber.i("startListener onNext");
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "startListener onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Timber.i("startListener onComplete");
            }
        };
        getObservableListener(subscriber)
                .subscribeOn(Schedulers.from(myExecutor))
                .observeOn(Schedulers.from(myExecutor))
                .subscribe(disposal);
        compositeDisposable.add(disposal);
    }

    private ExecutorService buildCustomExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    private Observable<Boolean> getObservableListener(DisposableObserver<String> subscriber) {
        return Observable.defer(() -> {
            barcodeScannerWrapper.startListener(subscriber);
            return Observable.just(true);
        });
    }

    @Override
    public void stopListener() {
        barcodeScannerWrapper.stopListener();
        disposePreviousRequest();
        stopAllTask();
        if (myExecutor != null) {
            myExecutor.shutdownNow();
        }
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

    private String getScanResult() throws Exception {
        return barcodeScannerWrapper.scan();
    }
}
