package com.rc.hover.hoverx.DataInfo;

/**
 * Created by johni003 on 2015-10-21.
 */
public abstract class DataInfo {
    public byte _id;
    public byte _type;
    public static final int SIZE_OF_OVERHEAD = 2 + Integer.SIZE/Byte.SIZE;

    public abstract byte[] toByteArray();

    public static class ID {
        public static final byte LeftDrive = 0;
        public static final byte RightDrive = 1;
        public static final byte ToToast = 2;
    }

    public static class TYPE {
        public static final byte Integer = 0;
        public static final byte String = 1;
    }
}