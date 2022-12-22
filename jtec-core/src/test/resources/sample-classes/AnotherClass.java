package com.example;

public class AnotherClass {
    private int bar;

    public AnotherClass(int bar) {
        this.bar = bar;
    }

    public int getBar() {
        return bar;
    }

    public static int printBaz() {
        System.out.println("baz");
    }
}