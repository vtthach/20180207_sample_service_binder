package cbsa.device.barcode.sdk.v2.device;

import android.content.Context;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import cbsa.device.Utils;
import io.reactivex.Observable;

/**
 * The type DeviceDetector.
 */
public class DeviceDetectorImpl implements DeviceDetector {

    private static final String TAG = DeviceDetectorImpl.class.getSimpleName();

    public DeviceDetectorImpl() {
    }

    @Override
    public Observable<List<BarcodeScanner>> searchDevices(final Context context) {
        final ArrayList<BarcodeScanner> listDevices = new ArrayList<>();
        return Observable.create(emitter -> {
            // TODO find another solution because this just only for CardPrinter
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket();
                socket.setBroadcast(true);
                socket.setSoTimeout(500);

                final InetAddress localIp = Utils.getLocalAddress(context);
                int localPort = socket.getLocalPort();
                final InetAddress remoteIp = Utils.getBroadcastAddress(context);
                int remotePort = 20108; // 11119 for card printer

                String request = "ISNP2.0 1 " + localIp.getHostAddress() + " " + localPort;
                byte[] requestBuf = Utils.String2Byte(request);
                DatagramPacket requestPacket =
                        new DatagramPacket(requestBuf, requestBuf.length, remoteIp, remotePort);

                socket.send(requestPacket);

                byte[] receiveBuf = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuf, 1024);
                socket.receive(receivePacket);
                //Todo: this sourcecode is used to find out more than one printer, so now just use 1 printer for testing
//                    while (0 < receivePacket.getLength()) {
                String receive =
                        Utils.Byte2String(receivePacket.getData(), receivePacket.getLength());
//                if (!"OFFLINE".equals(receive.split(" ")[2])) {
//                    printers.add(new PrinterImpl(receive.split(" ")[2] + " : " + receive.split(" ")[3], mIsKeepConnection));
//                }
//                        socket.receive(receivePacket);
//                    }
                socket.close();
            } catch (Exception exception) {
                Log.e(TAG, Exception.class.getSimpleName(), exception);
                emitter.onError(exception);
            } finally {
                closeSocket(socket);
            }

            emitter.onNext(listDevices);
        });
    }

    private void closeSocket(DatagramSocket socket) {
        if (socket != null) {
            socket.close();
        }
    }
}
