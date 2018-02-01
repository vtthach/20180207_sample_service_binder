package cbsa.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.SocketException;

import cbsa.device.Utils;
import cbsa.device.barcode.ResultCallback;
import cbsa.device.barcode.service.BarcodeService;
import cbsa.device.service.DeviceService;
import cbsa.device.service.DeviceServiceNative;
import cbsa.device.service.ServiceBinder;
import timber.log.Timber;

public class SampleBarcodeActivity extends AppCompatActivity implements ServiceBinder.IServiceBinderCallback<DeviceService> {

    ServiceBinder<DeviceService> serviceBinder;

    TextView textView;
    TextView scanStatus;
    private BarcodeService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.isConnect);
        scanStatus = findViewById(R.id.scanStatus);
        Button btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(view -> {
            if (service != null) {
                scanStatus.setText("Scan...");
                service.scan(new ResultCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        scanStatus.setText(String.format("%s (Time: %s)", result, System.currentTimeMillis()));
                    }

                    @Override
                    public void onError(String reason) {
                        scanStatus.setText(reason);
                    }
                });

            }
        });

        serviceBinder = new ServiceBinder<>(this,
                DeviceServiceNative.class,
                DeviceService.class,
                this);

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
    public void onServiceConnected(DeviceService service) {
        Timber.i("vtt onServiceConnected: ");
        this.service = service.getBarCodeService();

        service.getBarCodeService().isConnected(new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                textView.setText("Is connected : " + result);
            }

            @Override
            public void onError(String reason) {
                textView.setText("Check device connect is error: " + reason);
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
    }
}
