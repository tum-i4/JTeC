package de.junit5project;

public class ThreadStarter {
    static boolean doSomething() throws InterruptedException {
        final Thread thread = new Thread() {
            public void run() {
                System.out.println("Thread Running");
            }
        };
        thread.start();
        thread.join();
        return true;
    }
}
