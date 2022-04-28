package com.hua.community;

/**
 * @create 2022-04-28 10:02
 */
public class TestInt {

    public static void main(String[] args) {
        Integer x = 110;
        Integer y  = new Integer(110);

        System.out.println(x == y); //false
        System.out.println(x.equals(y));    //true
    }
}
