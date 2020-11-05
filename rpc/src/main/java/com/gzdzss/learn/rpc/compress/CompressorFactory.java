package com.gzdzss.learn.rpc.compress;

/**
 * @author Andrew
 * @create 2020/11/5 5:28 下午
 */
public class CompressorFactory {
    
    public static Compressor get(byte extraInfo) {
        switch (extraInfo & 24) {
            case 0x0:
                return new SnappyCompressor();
            default:
                return new SnappyCompressor();
        }
    }
}
