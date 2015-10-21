package com.rc.hover.hoverx.DataInfo;

import java.nio.ByteBuffer;

public class intDataInfo extends DataInfo {
    public int _data;
    public static final int SIZE = SIZE_OF_OVERHEAD + Integer.SIZE/Byte.SIZE;

    public intDataInfo(byte id, byte type, int data) {
        _data = data;
        _id = id;
        _type = type;
    }

    @Override
    public byte[] toByteArray() {
        byte[] buffer = new byte[SIZE];
        byte[] bytes;

        buffer[0] = _id;
        buffer[1] = _type;

        bytes = ByteBuffer.allocate(Integer.SIZE/Byte.SIZE).putInt(SIZE).array();
        for (int i = 0; i < bytes.length; i++)
            buffer[i + 2] = bytes[i];

        bytes = ByteBuffer.allocate(Integer.SIZE/Byte.SIZE).putInt(_data).array();
        for (int i = 0; i < bytes.length; i++)
            buffer[i + SIZE_OF_OVERHEAD] = bytes[i];

        return buffer;
    }
}