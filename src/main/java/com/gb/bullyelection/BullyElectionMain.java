package com.gb.bullyelection;

import java.io.IOException;
import java.time.Duration;

public class BullyElectionMain {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        ElectionConfig electionConfig = new ElectionConfig(
                Duration.ofSeconds(5),
                Duration.ofSeconds(3),
                Duration.ofSeconds(5));


        Member m1 = initNode("127.0.0.1", 3, 19000, 190001,
                19002, 19003, 19004);
        Member m2 = initNode("127.0.0.1", 13, 19100, 190101,
                19102, 19103, 19104);
        Member m3 = initNode("127.0.0.1", 23, 19200, 190201,
                19202, 19203, 19204);
        Member m4 = initNode("127.0.0.1", 33, 19300, 190301,
                19302, 19303, 19304);

//        startFirstNode(m1);
//        joinToCluster(m1, m2);
//        joinToCluster(m1, m3);
        joinToCluster(m2, m4);
    }

    private static Member initNode(String host,
                                   int id, int port, int electionPort,
                                   int keepAlivePort, int discoveryPort, int dataPort) {
        Member member = new Member(host, id, port,
                electionPort, keepAlivePort, discoveryPort, dataPort);

        return member;
    }

    private static void startFirstNode(Member member) throws IOException, ClassNotFoundException {
        CommunicationService communicationService =
                new CommunicationService(member);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    communicationService.membershipAdditions(null);
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

    private static Member joinToCluster(Member cluster, Member member) throws IOException, ClassNotFoundException {
        CommunicationService communicationService1 =
                new CommunicationService(member);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    communicationService1.membershipAdditions(cluster);
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
        return member;
    }
}
