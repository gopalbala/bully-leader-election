package com.gb.bullyelection;

import lombok.Getter;

import java.io.*;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommunicationService {

    private Member self;
    private ServerSocket serverSocket;
//    private byte[] receivedBuffer = new byte[1024];
//    private DatagramPacket receivePacket =
//            new DatagramPacket(receivedBuffer, receivedBuffer.length);

    public CommunicationService(Member member, int portToListen) throws IOException {
        try {
            this.self = member;
            serverSocket = new ServerSocket(portToListen);
        } catch (SocketException e) {
            System.out.println("Could not create socket connection");
            e.printStackTrace();
        }
    }

    public MemberResponse receiveElectionMessage() {
        return new MemberResponse();
    }

    public void elect() {
        List<Member> membersToNotify = new ArrayList<>();
        if (self.getPeers().size() == 1) {
            self.setCoordinator(true);
        } else {
            membersToNotify = new ArrayList<>();
            for (Member member : self.getPeers().values()) {
                if (member.getId() > self.getId())
                    membersToNotify.add(member);
            }
        }
        if (membersToNotify.size() > 0) {
            for (Member me : membersToNotify) {

            }
        } else {

        }
    }

    public void membershipAdditions(Member clusterMember) throws IOException, ClassNotFoundException {

        // ServerSocket serverSocket = new ServerSocket(self.getDiscoveryPort());
//        while (true) {
//            Socket socket = serverSocket.accept();

        System.out.println("Registering to cluster");

        if (clusterMember == null) {
            System.out.println("ready to receive members");

            self.getPeers().putIfAbsent(self.getId(), self);
            while (true) {
                Socket socket = serverSocket.accept();
                ObjectInputStream memRequest = new
                        ObjectInputStream(socket.getInputStream());
                Member member = (Member) memRequest.readObject();
//                memRequest.close();
                System.out.println("Discovered new member " + member.getId());
                if (member.getId() != self.getId()) {
                    self.getPeers().putIfAbsent(member.getId(), member);
//                    ObjectOutputStream memResponse = new
//                            ObjectOutputStream(socket.getOutputStream());
//                    memResponse.writeObject(self);
//                    memResponse.close();
                    for (Member m : self.getPeers().values()) {
//                        if (m.getId()!=member.getId()) {
                        try {
//                            Socket skt = new Socket("127.0.0.1", self.getDiscoveryPort());
                            ObjectOutputStream memResponse = new
                                    ObjectOutputStream(socket.getOutputStream());
                            memResponse.writeObject(m);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //memResponse.writeObject(m);
                    }

//                    }
                }
                //notifyMemberAddition(member);
            }
        } else {
            System.out.println("Registering to cluster");
            Socket clSock = new Socket("127.0.0.1", clusterMember.getDiscoveryPort());
            ObjectOutputStream memResponse = new ObjectOutputStream(clSock.getOutputStream());
            memResponse.writeObject(self);
            new Thread(() -> {
                try {
                    this.receiver(clSock);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
//            while (true) {
//                Socket socket = serverSocket.accept();

                //memResponse.writeObject(self.getPeers().values());
                //;
//            }
        }
        // System.out.println();
//        }
    }

    private void receiver(Socket socket) throws IOException, ClassNotFoundException {
        while (true) {
            ObjectInputStream memRequest = new ObjectInputStream(socket.getInputStream());
            Member member = (Member) memRequest.readObject();
            System.out.println("Discovered new member " + member.getId());
            if (member.getId() != self.getId())
                self.getPeers().putIfAbsent(member.getId(), member);
        }
    }

    private void selfReceiver() {

    }

//    private void notifyMemberAddition(Member newMember) throws IOException {
//        for (Member member : self.getPeers().values()) {
//            if (self.getId() != member.getId()) {
//                Socket socket = serverSocket.accept();
//                System.out.println("ready to receive members");
//                ObjectInputStream memRequest = new ObjectInputStream(this.serverSocket.accept().getInputStream());
//                ObjectOutputStream memResponse = new ObjectOutputStream(this.serverSocket.accept().getOutputStream());
//            }
//        }
//    }
}
