package com.gzdzss.learn.rpc.serialization;

import java.io.IOException;

public interface Serialization {
    
    <T> byte[] serialize(T obj) throws IOException;
    
    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;
}
