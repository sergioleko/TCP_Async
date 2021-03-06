package ru.linkos.tcp_async;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import Linkos.RTC.Message.AXS.Axs;
import Linkos.RTC.Message.GenericOuterClass;
import Linkos.RTC.Message.Range;

public class protoOperations {
    boolean xPosLoop;
    boolean yPosLoop;
    double xposmin;
    double xposmax;
    double yposmin;
    double yposmax;
    double curXpos;
    double curYpos;
    double xspdmax;
    double yspdmax;
    List<Integer> dataList;

    public byte[] makeSreqProto () throws IOException {

        GenericOuterClass.Generic.Builder gocB = GenericOuterClass.Generic.newBuilder();

        gocB.getDefaultInstanceForType();
        gocB.setMid(1398292803);


        GenericOuterClass.SREQ.Builder srqB = GenericOuterClass.SREQ.newBuilder();
        srqB.getDefaultInstanceForType();
        gocB.setSreq(srqB);
        srqB.clear();
        gocB.setSreq(srqB);



        byte[] os = gocB.build().toByteArray();

        return os;


    }

    public int parseSrepProto(byte[] incoming) throws InvalidProtocolBufferException {

        GenericOuterClass.Generic input = GenericOuterClass.Generic.parseFrom(incoming);
//        int gotmid = input.getMid();

        GenericOuterClass.SREP srep = input.getSrep();



        int statusOK = 0;
        if (srep.getReady()){
            statusOK = 1;
            if (srep.getBusy()){
                statusOK = 2;
            }
        }
        //Log.i("status: ", String.valueOf(statusOK));
        return statusOK;
    }

    public byte[] makeCreq() {

        GenericOuterClass.Generic.Builder gocB = GenericOuterClass.Generic.newBuilder();

        gocB.getDefaultInstanceForType();
        gocB.setMid(1398292803);
        GenericOuterClass.CREQ.Builder creq = GenericOuterClass.CREQ.newBuilder();
        creq.getDefaultInstanceForType();
        gocB.setCreq(creq);

        byte[] creqpacket = gocB.build().toByteArray();
        return creqpacket;

    }

    public void parseCrep(byte[] incoming) throws InvalidProtocolBufferException, NoSuchAlgorithmException {
        GenericOuterClass.Generic input = GenericOuterClass.Generic.parseFrom(incoming);
        if (input.hasCrep()) {
            GenericOuterClass.CREP crep = input.getCrep();
            Axs.CREP AxsCrep = crep.getAxs();

            xPosLoop = AxsCrep.getXpositionLoop();
            yPosLoop = AxsCrep.getYpositionLoop();
            Range.range_d rangex = AxsCrep.getXposition();
            Range.range_d rangey = AxsCrep.getYposition();
            Range.range_d spdx = AxsCrep.getXspeed();
            Range.range_d spdy = AxsCrep.getYspeed();
            xposmin = rangex.getMin();
            xposmax = rangex.getMax();
            yposmin = rangey.getMin();
            yposmax = rangey.getMax();
            xspdmax = spdx.getMax();
            yspdmax = spdy.getMax();

            byte[] hash = MessageDigest.getInstance("MD5").digest(incoming);
            dataList.clear();
            for (int i = 0; i < hash.length; i += 4) {
                int floatBits = hash[i] & 0xFF |
                        (hash[i + 1] & 0xFF) << 8 |
                        (hash[i + 2] & 0xFF) << 16 |
                        (hash[i + 3] & 0xFF) << 24;

                dataList.add(floatBits);




            }


        }
    }

    public String parseSrep(byte[] bytes) throws InvalidProtocolBufferException {


        GenericOuterClass.Generic input = GenericOuterClass.Generic.parseFrom(bytes);
        if (input.hasSrep()){
            GenericOuterClass.SREP srep = input.getSrep();
            Axs.SREP axsSrep = srep.getAxs();

            curXpos = axsSrep.getXposition();
            curYpos = axsSrep.getYposition();

            Log.i ("Cur pos:", curXpos + "\t" + curYpos);
        }
        return String.valueOf(curXpos) + String.valueOf(curYpos);
    }

    public byte[] makeSreq() {

        GenericOuterClass.Generic.Builder gocB = GenericOuterClass.Generic.newBuilder();

        gocB.getDefaultInstanceForType();
        gocB.setMid(1398292803);
        GenericOuterClass.SREQ.Builder sreq = GenericOuterClass.SREQ.newBuilder();
        sreq.getDefaultInstanceForType();
        gocB.setSreq(sreq);

        byte[] sreqpacket = gocB.build().toByteArray();
        return sreqpacket;


    }

    public byte[] makeMreq(int Kspeed, boolean x, boolean y, boolean up)  {

        GenericOuterClass.Generic.Builder gocB = GenericOuterClass.Generic.newBuilder();
        gocB.getDefaultInstanceForType();
        gocB.setMid(1398292803);
        GenericOuterClass.MREQ.Builder mreq = GenericOuterClass.MREQ.newBuilder();

        mreq.setMd5A(dataList.get(0));
        // Log.i("MD5A", String.valueOf(dataList.get(0)));
        mreq.setMd5B(dataList.get(1));
        //Log.i("MD5B", String.valueOf(dataList.get(1)));
        mreq.setMd5C(dataList.get(2));
        //Log.i("MD5C", String.valueOf(dataList.get(2)));
        mreq.setMd5D(dataList.get(3));
        //Log.i("MD5D", String.valueOf(dataList.get(3)));
        mreq.setPriority(0);


        Axs.MREQ.Builder axsMreq = Axs.MREQ.newBuilder();
        double xpseed = xspdmax / Kspeed;
        double yspeed = yspdmax / Kspeed;
        if (x){
            if (up){
                axsMreq.setXspeed(xpseed);
            }
            else {
                axsMreq.setXspeed(-xpseed);
            }
        }
        else {
            axsMreq.setXspeed(0);
        }
        if (y){
            if (up){
                axsMreq.setYspeed(yspeed);
            }
            else {
                axsMreq.setYspeed(-yspeed);
            }
        }
        else {
            axsMreq.setYspeed(0);
        }

        mreq.setAxs(axsMreq.build());
        gocB.setMreq(mreq.build());
        return gocB.build().toByteArray();

    }





}
