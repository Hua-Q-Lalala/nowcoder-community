package com.hua.community;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.LinkedList;

/**
 * @create 2022-04-06 12:28
 */
public class WriteBug {

    //static Unsafe unsafe = Unsafe.getUnsafe();
    static Unsafe unsafe;
    static long stateOffset;
    private volatile long state = 0;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            stateOffset = unsafe.objectFieldOffset(WriteBug.class.getDeclaredField("state"));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        WriteBug bug = new WriteBug();
        boolean success = unsafe.compareAndSwapLong(bug, stateOffset, 0, 1);
        System.out.println(success);
        success = unsafe.compareAndSwapLong(bug, stateOffset, 0, 1);
        System.out.println(success);


    }
}
