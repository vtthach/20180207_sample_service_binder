package cbsa.device.barcode.service;


import android.content.Intent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import cbsa.device.barcode.ResultCallback;
import cbsa.device.barcode.sdk.SocketClientStatus;
import cbsa.device.barcode.sdk.SocketStatusListener;
import cbsa.device.barcode.sdk.BarcodeScannerConfig;
import cbsa.device.barcode.sdk.BarcodeScannerWrapper;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

import static cbsa.device.service.DeviceServiceNative.KEY_CONNECTION_TIMEOUT_IN_MILLIS;
import static cbsa.device.service.DeviceServiceNative.KEY_IP_ADDRESS;
import static cbsa.device.service.DeviceServiceNative.KEY_PORT;

public class BarcodeServiceV2Impl implements BarcodeService {

    private static final int DEFAULT_PORT = 20108;
    private static final int DEFAULT_TIMEOUT = 5000;
    private static final String DEFAULT_IP_ADDRESS = "192.168.1.12";
    private final BarcodeScannerConfig config;
    private DisposableObserver<String> scanDisposal;
    private final BarcodeScannerWrapper barcodeScannerWrapper;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private SocketStatusListener autoScanListener = new SocketStatusListener() {
        @Override
        public void onStatusChange(SocketClientStatus var1) {

        }

        @Override
        public void onReceive(String var1) {
            if (publisher != null && var1 != null) {
                publisher.onNext(var1);
            }
        }

        @Override
        public void onError(String var1) {
            if (publisher != null) {
                publisher.onError(new Throwable(var1));
            }
        }
    };
    private PublishSubject<String> publisher;
    private DisposableObserver<Boolean> autoScanDisposable;

    @Inject
    public BarcodeServiceV2Impl(BarcodeScannerWrapper barcodeScannerWrapper) {
        this.barcodeScannerWrapper = barcodeScannerWrapper;
        this.config = this.barcodeScannerWrapper.getConfig();
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
        disposeDisposal(scanDisposal);
        scanDisposal = getScanResultDisposal(resultCallback);
        getScanResultObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scanDisposal);
        compositeDisposable.add(scanDisposal);
    }

    @Override
    public void startListener(DisposableObserver<String> subscriber) {
        disposeDisposal(autoScanDisposable);
        autoScanDisposable = new DisposableObserver<Boolean>() {
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
        Scheduler scheduler = Schedulers.newThread();
        getObservableListener(subscriber)
                .subscribeOn(scheduler)
                .observeOn(scheduler)
                .subscribe(autoScanDisposable);
        compositeDisposable.add(autoScanDisposable);
    }

    private ExecutorService buildCustomExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    private Observable<Boolean> getObservableListener(final DisposableObserver<String> subscriber) {
        return Observable.defer(() -> {
            publisher = PublishSubject.create();
            publisher.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(subscriber);
            barcodeScannerWrapper.startListener(autoScanListener);
            return Observable.just(true);
        });
    }

    @Override
    public void stopListener() {
        barcodeScannerWrapper.stopListener();
        disposeDisposal(scanDisposal);
    }

    @Override
    public void updateConfig(String ip, String port) {
        config.update(ip, Integer.parseInt(port));
    }

    @Override
    public void initConfig(Intent intent) {
        String ipAddress = intent.getStringExtra(KEY_IP_ADDRESS);
        int port = intent.getIntExtra(KEY_PORT, DEFAULT_PORT);
        int timeout = intent.getIntExtra(KEY_CONNECTION_TIMEOUT_IN_MILLIS, DEFAULT_TIMEOUT);
        config.update(ipAddress != null ? ipAddress : DEFAULT_IP_ADDRESS, port, timeout);
    }

    private void disposeDisposal(DisposableObserver<?> disposal) {
        if (disposal != null) {
            disposal.dispose();
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
