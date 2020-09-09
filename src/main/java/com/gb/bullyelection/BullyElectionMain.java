package com.gb.bullyelection;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;

public class BullyElectionMain {
    public static void main(String[] args) throws IOException {
        ElectionConfig electionConfig = new ElectionConfig(
                Duration.ofSeconds(5),
                Duration.ofSeconds(3),
                Duration.ofSeconds(5));

        Member member = new Member(new InetSocketAddress("127.0.0.1", 19000),
                3,19001,19002,19003);

//        CommunicationService communicationService =
//                new CommunicationService(member,19003);

        Member member1 = new Member(new InetSocketAddress("127.0.0.1", 19300),
                213,19301,19302,19303);

        CommunicationService communicationService1 =
                new CommunicationService(member1,19303);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    communicationService1.membershipAdditions(member);
//                    communicationService.membershipAdditions(null);
                } catch (IOException e) {
                   // e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
