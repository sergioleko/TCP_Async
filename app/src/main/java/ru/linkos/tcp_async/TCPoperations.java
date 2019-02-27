package ru.linkos.tcp_async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static ru.linkos.tcp_async.WiFiOperations.EXTRA_MESSAGE_ERR_NO;

public class TCPoperations extends AsyncTask<Void,Void,Void> {
    protoOperations po = new protoOperations();
    Context mContext;
    private final String targetIp;
    private final String targetPort;
    Socket targetSocket;


    public TCPoperations(final Context context, final String ip,  final String port) {
        mContext = context;
        targetIp = ip;
        targetPort = port;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            targetSocket = connectTCP(targetIp, targetPort);
            statusUpdater();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return null;
    }

    public Socket connectTCP(String ip, String targetPort) throws IOException {
        // Log.i("Socket ", String.valueOf(mySocket));
        return new Socket(ip, Integer.decode(targetPort));
    }

    public void sendTCP(Socket targetSocket, byte[] data) throws IOException {

      int len = data.length;
      int sen = Integer.reverseBytes(len);
        OutputStream out = targetSocket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeInt(sen);
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



    public void statusUpdater() throws IOException, InterruptedException {
        while (true){
            sendTCP(targetSocket, po.makeSreqProto());
            Log.i("SREQ sent", "Yep");
            Log.i("SREP", String.valueOf(po.parseSrepProto(recieveTCP(targetSocket))));
            if (po.parseSrepProto(recieveTCP(targetSocket)) == 0){

                startError(mContext, "Not ready to control");
                return;
            }
            Thread.sleep(1000);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(mContext, "Not ready", Toast.LENGTH_SHORT).show();

    }

    public void startError(Context curContext, String error) {
        Intent errorIntent = new Intent(curContext, startErrorActivity.class);
        errorIntent.putExtra(EXTRA_MESSAGE_ERR_NO, error);
        curContext.startActivity(errorIntent);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);


    }
}
