package cbsa.device;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import timber.log.Timber;

public class Utils {

    /**
     * Convert String to Byte
     */
    public static byte[] String2Byte(String s) {
        if (s.length() <= 0) return null;

        byte[] b = new byte[s.length() * 2 + 2];
        for (int i = 0; i < s.length(); i++) {
            b[i * 2] = (byte) (s.charAt(i) & 0x00FF);
            b[i * 2 + 1] = (byte) ((s.charAt(i) & 0xFF00) >> 8);
            ;
        }

        return b;
    }

    /**
     * Convert Byte to String
     */
    public static String Byte2String(byte[] b, int l) {
        if (l / 2 <= 0) return null;

        char[] buf = new char[l / 2 + 1];
        for (int i = 0; i < l / 2; i++)
            buf[i] = (char) ((b[i * 2] & 0x00FF) + ((b[i * 2 + 1] & 0x00FF) << 8));

        return new String(buf);
    }

    /**
     * Get Local Address
     *
     * @throws IOException
     */
    public static InetAddress getLocalAddress(Context context) throws IOException {
        InetAddress ret = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface ni = en.nextElement();
                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                    InetAddress ip = interfaceAddress.getAddress();
                    InetAddress bc = interfaceAddress.getBroadcast();
                    if (!ni.isLoopback() && ip != null && bc != null && ip.isSiteLocalAddress()) {
                        ret = ip;
                        break;
                    }
                }
            }
        } catch (SocketException ex) {
        }

        return ret;
    }

    /**
     * Get Broad Cast Address
     *
     * @throws IOException
     */
    public static InetAddress getBroadcastAddress(Context context) throws IOException {
        InetAddress ret = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface ni = en.nextElement();
                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                    InetAddress ip = interfaceAddress.getAddress();
                    InetAddress bc = interfaceAddress.getBroadcast();
                    if (!ni.isLoopback() && ip != null && bc != null && ip.isSiteLocalAddress()) {
                        ret = bc;
                        break;
                    }
                }
            }
        } catch (SocketException ex) {
        }

        return ret;
    }


    public static List<InetAddress> getActiveAddressOnLan() throws SocketException {
        Enumeration nis = NetworkInterface.getNetworkInterfaces();
        List<InetAddress> list = new ArrayList<>();
        while (nis.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) nis.nextElement();
            Enumeration ias = ni.getInetAddresses();
            while (ias.hasMoreElements()) {
                InetAddress i = (InetAddress) ias.nextElement();
                list.add(i);
                byte[] a = i.getAddress();
                Timber.i("- address:  " + Utils.Byte2String(i.getAddress(), a.length));
            }
        }
        return list;
    }
}
