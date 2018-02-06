package cbsa.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import cbsa.device.barcode.ResultCallback;
import cbsa.device.barcode.service.BarcodeService;
import cbsa.device.service.DeviceService;
import cbsa.device.service.DeviceServiceNative;
import cbsa.device.service.ServiceBinder;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

public class SampleBarcodeActivity extends AppCompatActivity implements ServiceBinder.IServiceBinderCallback<DeviceService> {

    ServiceBinder<DeviceService> serviceBinder;

    TextView isConnect;
    TextView scanStatus;

    private BarcodeService service;
    private Boolean isBarcodeDeviceConnected;
    private DisposableObserver<String> dis;

    private ResultCallback<Boolean> connectResultCallback = new ResultCallback<Boolean>() {
        @Override
        public void onSuccess(Boolean result) {
            isConnect.setText(result ? "Connected" : "Disconnected");
            updateFlagConnection(result);
        }

        @Override
        public void onError(String reason) {
            isConnect.setText(reason);
            updateFlagConnection(false);
        }
    };

    private void updateFlagConnection(Boolean result) {
        isBarcodeDeviceConnected = result;
        if (result) {
            btnStartListener.setText("Start listener");
        } else {
            btnStartListener.setText("Check device status");
        }
        btnStartListener.setEnabled(true);
    }

    private Button btnStartListener;
    private Button btnScan;
    private Button btnStopListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isConnect = findViewById(R.id.isConnect);
        scanStatus = findViewById(R.id.scanStatus);
        btnScan = findViewById(R.id.btnScan);
        btnStopListener = findViewById(R.id.btnStopListener);
        btnStartListener = findViewById(R.id.btnStartListener);

        btnScan.setOnClickListener(view -> {
            if (service != null) {
                scanStatus.setText("Scanning...");
                service.scan(new ResultCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        scanStatus.setText(result + "(Time: " + DateFormat.getTimeInstance().format(new Date()) + ")");
                    }

                    @Override
                    public void onError(String reason) {
                        scanStatus.setText(reason);
                    }
                });
            }
        });
        btnStartListener.setOnClickListener(view -> {
            if (service != null) {
                btnStartListener.setEnabled(false);
                if (isBarcodeDeviceConnected) {
                    btnStopListener.setEnabled(true);
                    btnScan.setEnabled(false);
                    scanStatus.setText("Auto Scan");
                    service.startListener(getDisposal());
                } else {
                    service.isConnected(connectResultCallback);
                }
            }
        });

        btnStopListener.setOnClickListener(view -> {
            if (service != null) {
                stopListener();
            }
        });

        serviceBinder = new ServiceBinder<>(this,
                DeviceServiceNative.class,
                DeviceService.class,
                this);

        btnStopListener.setEnabled(false);

    }

    private void stopListener() {
//        disposeListener();
        btnScan.setEnabled(true);
        scanStatus.setText("Manual Scan");
        btnStopListener.setEnabled(false);
        btnStartListener.setEnabled(true);
        service.stopListener();
    }

    private DisposableObserver<String> getDisposal() {
        disposeListener();
        dis = new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                scanStatus.setText(s + "(Time: " + DateFormat.getTimeInstance().format(new Date()) + ")");
            }

            @Override
            public void onError(Throwable e) {
                isConnect.setText(e.getMessage());
                connectResultCallback.onSuccess(false);
                stopListener();
            }

            @Override
            public void onComplete() {

            }
        };
        return dis;
    }

    private void disposeListener() {
        if (dis != null) {
            dis.dispose();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        serviceBinder.bindService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        serviceBinder.unBindService();
    }

    @Override
    public void onServiceConnected(DeviceService deviceService) {
        Timber.i("vtt onServiceConnected: ");
        service = deviceService.getBarCodeService();
        service.isConnected(connectResultCallback);
    }

    @Override
    public void onServiceDisconnected() {
        Timber.i("vtt onServiceDisconnected ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.i("vtt onDestroy");
        if (dis != null) {
            dis.dispose();
        }
        if (service != null) {
            service.stopListener();
        }
    }
}
