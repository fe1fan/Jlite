package io.xka.jlite.web;

import org.junit.jupiter.api.Test;

class JliteTest {

    @Test
    void serv() throws InterruptedException {
//        JliteApp app = Jlite.create().port(8080);
//        app.get("/get", ctx -> {
//            String name = ctx.getQuery("name");
//            System.out.println(name);
//            ctx.result("Hello World!" + name);
//        });
//        app.get("/json", ctx -> {
//            ctx.json();
//        });
//        new Thread(() -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            app.stop();
//        }).start();
//        app.start();
        new Thread(() -> {
            while (true) {
                System.out.println("im alive");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        System.out.println("end");
    }

    @Test
    public void t2() {
        new Thread(() -> {
            while (true) {
                System.out.println("im alive");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        System.out.println("end");
    }
}