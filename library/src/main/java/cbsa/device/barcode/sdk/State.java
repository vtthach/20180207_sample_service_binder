package cbsa.device.barcode.sdk;


import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import timber.log.Timber;

import static cbsa.device.Constant.LOG_TAG;

public class State implements Closeable {
    private Socket socket;
    private byte[] sendBuffer;
    private byte[] receiveBuffer;
    private DataOutputStream dataOutputStream;

    public byte[] getReceiveBuffer() {
        return this.receiveBuffer;
    }

    public static State create(byte[] sendBuffer, int receiveBufferSize) {
        return new State(constructSocket(), sendBuffer, receiveBufferSize);
    }

    static Socket constructSocket() {
        return new Socket();
    }

    State(Socket socket, byte[] sendBuffer, int receiveBufferSize) {
        this.socket = socket;
        this.sendBuffer = sendBuffer;
        this.receiveBuffer = new byte[receiveBufferSize];
    }

    public void connect(String ipAddress, int port, int connectionTimeout) throws IOException {
        SocketAddress socketAddress = new InetSocketAddress(ipAddress, port);
        this.socket.connect(socketAddress, connectionTimeout);
    }

    public int send() throws IOException {
        Timber.i("send");
        this.disposeDataOutputStream();
        this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
        this.dataOutputStream.write(this.sendBuffer, 0, this.sendBuffer.length);
        return this.dataOutputStream.size();
    }

    public int receive() throws IOException {
        Timber.i("receive");
        InputStream inputStream = this.socket.getInputStream();
        byte[] data = new byte[1024];
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int readBytes = inputStream.read(data);
        if (readBytes > 0) {
            buffer.write(data, 0, readBytes);
            this.receiveBuffer = buffer.toByteArray();
        }
        buffer.flush();
        buffer.close();
        return readBytes;
    }

    public byte[] receiveBytes() throws IOException {
        Timber.i("receive");
        InputStream inputStream = this.socket.getInputStream();
        byte[] data = new byte[1024];
        byte[] byteRs = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int readBytes = inputStream.read(data);
        if (readBytes > 0) {
            buffer.write(data, 0, readBytes);
            byteRs = buffer.toByteArray();
        }
        buffer.flush();
        buffer.close();
        return byteRs;
    }

    public boolean isConnected() {
        return this.socket.isConnected();
    }

    public void close() throws IOException {
        this.disposeDataOutputStream();
        this.disposeSocket();
    }

    private void disposeDataOutputStream() throws IOException {
        if (this.dataOutputStream != null) {
            Timber.i("disposeDataOutputStream");
            this.dataOutputStream.flush();
            this.dataOutputStream.close();
            this.dataOutputStream = null;
        }
    }

    private synchronized void disposeSocket() throws IOException {
        if (socket != null) {
            Timber.i(LOG_TAG + "disposeSocket");
            try {
                if (!socket.isInputShutdown()) {
                    socket.shutdownInput();
                }
                if (!socket.isOutputShutdown()) {
                    socket.shutdownOutput();
                }
            } catch (IOException var3) {
                Timber.d(var3, "Error when disposeSocket: " + var3.getMessage());
            } finally {
                socket.close();
                socket = null;
            }
        }
    }
}
