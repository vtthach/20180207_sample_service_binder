package cbsa.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private TextView tvIp;
    private TextView tvPort;
    private Button btnStartListener;
    private Button btnScan;
    private Button btnStopListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isConnect = findViewById(R.id.isConnect);
        scanStatus = findViewById(R.id.scanStatus);
        tvIp = findViewById(R.id.tvIp);
        tvPort = findViewById(R.id.tvPort);
        scanStatus = findViewById(R.id.scanStatus);
        btnScan = findViewById(R.id.btnScan);
        btnStopListener = findViewById(R.id.btnStopListener);
        btnStartListener = findViewById(R.id.btnStartListener);
        tvIp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateConfig();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        tvPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateConfig();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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
                    isConnect.setText("Auto listen value...");
                    scanStatus.setText("Waiting...");
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
        updateConfig();
        service.isConnected(connectResultCallback);
    }

    private void updateConfig() {
        if (service != null) {
            service.updateConfig(tvIp.getText().toString(), tvPort.getText().toString());
        }
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && getCurrentFocus() != null) {
            View v = getCurrentFocus();

            // check keyboard is shown before dispatch touch event
            boolean beforeDispatch = UIUtil.isKeyboardShown(v.getRootView());

            boolean ret = super.dispatchTouchEvent(event);

            if (v instanceof EditText) {
                // check keyboard is shown after dispatch touch event
                boolean isAfterDispatch = UIUtil.isKeyboardShown(v.getRootView());

                if (event.getAction() == MotionEvent.ACTION_DOWN && isAfterDispatch &&
                        beforeDispatch) {
                    UIUtil.hideKeyboard(this, getWindow().getCurrentFocus());
                }
            }
            return ret;
        } else
            return super.dispatchTouchEvent(event);
    }
}
