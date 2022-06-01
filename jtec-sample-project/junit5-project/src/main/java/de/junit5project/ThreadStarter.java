package de.junit5project;

public class ThreadStarter {
    static boolean doSomething() {
        final Thread thread = new Thread() {
            public void run() {
                System.out.println("Thread Running");
            }
        };
        return true;
    }
}
