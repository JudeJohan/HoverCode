package com.rc.hover.hoverx.DataInfo;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by johni003 on 2015-10-21.
 */
public class stringDataInfo extends DataInfo {

    public String _data;
    public int _size;

    public stringDataInfo(byte id, byte type, String data) {
        _data = data;
        _id = id;
        _type = type;
        _size = SIZE_OF_OVERHEAD + data.getBytes(Charset.availableCharsets().get("UTF-8")).length;
    }

    @Override
    public byte[] toByteArray() {
        byte[] buffer = new byte[_size];
        byte[] data = _data.getBytes(Charset.availableCharsets().get("UTF-8"));
        byte[] bytes;

        buffer[0] = _id;
        buffer[1] = _type;

        bytes = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(_size).array();
        for (int i = 0; i < bytes.length; i++)
            buffer[i + 2] = bytes[i];

        for(int i = 0; i < data.length; i++)
            buffer[i + SIZE_OF_OVERHEAD] = data[i];

        return buffer;
    }
}
