package cbsa.device.barcode.sdk.v2.device;

import android.content.Context;

import java.util.List;

import io.reactivex.Observable;

/**
 * The interface DeviceDetector.
 */
public interface DeviceDetector {
    /**
     * // TODO find another solution because this just only for CardPrinter
     * @param context the context
     * @return the observable
     */
    Observable<List<BarcodeScanner>> searchDevices(final Context context);
}
