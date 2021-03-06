package ru.linkos.tcp_async;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class basicTCP {

    public void sendTCP(Socket targetSocket, byte[] data) throws IOException {

        int len = Integer.reverseBytes(data.length);
        OutputStream out = targetSocket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeInt(len);
        if (len > 0) {
            dos.write(data, 0, len);
            dos.flush();
        }

    }


    public byte[] recieveTCP(Socket targetSocket) throws IOException {
        InputStream in = targetSocket.getInputStream();
        DataInputStream dis = new DataInputStream(in);
        int buf = dis.readInt();
        int len = Integer.reverseBytes(buf);
        byte[] data = new byte[len];
        if (len > 0) {
            dis.readFully(data);
        }
        return data;
    }
}
