package com.gzdzss.learn.rpc.compress;

import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * @author Andrew
 * @create 2020/11/5 5:29 下午
 */
public class SnappyCompressor implements Compressor {
    public byte[] compress(byte[] array) throws IOException {
        if (array == null) {
            return null;
        }
        return Snappy.compress(array);
    }
    
    public byte[] unCompress(byte[] array) throws IOException {
        if (array == null) {
            return null;
        }
        return Snappy.uncompress(array);
    }
}
