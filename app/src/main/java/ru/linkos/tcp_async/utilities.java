package ru.linkos.tcp_async;

public class utilities {

    byte [] intToByte (int len) {
        byte[] ret = new byte[4];
        ret[3] = (byte) (len & 0xFF);
        ret[2] = (byte) ((len >> 8) & 0xFF);
        ret[1] = (byte) ((len >> 16) & 0xFF);
        ret[0] = (byte) ((len >> 24) & 0xFF);
    return ret;
    }

}
