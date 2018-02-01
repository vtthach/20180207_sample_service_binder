package cbsa.device.barcode.sdk.v2.device;


import io.reactivex.Observable;

public interface Device {
    Observable<Boolean> isDeviceConnected();
}
