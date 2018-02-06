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

    TextView textView;
    TextView scanStatus;
    private BarcodeService service;
    private Boolean isBarcodeDeviceConnected;
    private DisposableObserver<String> dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.isConnect);
        scanStatus = findViewById(R.id.scanStatus);
        Button btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(view -> {
            if (service != null) {
//                scanStatus.setText("Scan...");
//                service.scan(new ResultCallback<String>() {
//                    @Override
//                    public void onSuccess(String result) {
//                        scanStatus.setText(String.format("%s (Time: %s)", result, System.currentTimeMillis()));
//                    }
//
//                    @Override
//                    public void onError(String reason) {
//                        scanStatus.setText(reason);
//                    }
//                });
            }
        });
        Button btnStopListener = findViewById(R.id.btnStopListener);
        Button btnStartListener = findViewById(R.id.btnStartListener);

        btnStartListener.setOnClickListener(view -> {
            if (service != null && isBarcodeDeviceConnected) {
                btnStopListener.setEnabled(true);
                btnStartListener.setEnabled(false);
                btnScan.setEnabled(false);
                scanStatus.setText("Auto Scan");
                service.startListener(getDisposal());
            }
        });

        btnStopListener.setOnClickListener(view -> {
            if (service != null && isBarcodeDeviceConnected) {
                disposeListener();
                btnScan.setEnabled(true);
                scanStatus.setText("Manual Scan");
                btnStopListener.setEnabled(false);
                btnStartListener.setEnabled(true);
                service.stopListener();
            }
        });

        serviceBinder = new ServiceBinder<>(this,
                DeviceServiceNative.class,
                DeviceService.class,
                this);

        btnStopListener.setEnabled(false);

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
        service.isConnected(new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                textView.setText(result ? "Connected" : "Disconnected");
                isBarcodeDeviceConnected = result;
            }

            @Override
            public void onError(String reason) {
                textView.setText("Check device connect is error: " + reason);
                isBarcodeDeviceConnected = false;
            }
        });
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
