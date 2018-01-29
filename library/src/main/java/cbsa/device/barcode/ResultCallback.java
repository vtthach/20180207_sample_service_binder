package cbsa.device.barcode;


public interface ResultCallback<T> {
    void onSuccess(T result);
    void onError(String reason);
}
