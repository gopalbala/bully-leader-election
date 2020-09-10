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
        System.out.println("Registering to cluster");
        if (clusterMember == null) {
            System.out.println("ready to receive members");
            self.getPeers().putIfAbsent(self.getId(), self);
            while (true) {
                Socket socket = serverSocket.accept();
                ObjectInputStream memRequest = new
                        ObjectInputStream(socket.getInputStream());
                Member member = (Member) memRequest.readObject();
                System.out.println("Discovered new member " + member.getId());
                if (member.getId() != self.getId()) {
                    self.getPeers().putIfAbsent(member.getId(), member);
                    for (Member m : self.getPeers().values()) {
                        try {
                            ObjectOutputStream memResponse = new
                                    ObjectOutputStream(socket.getOutputStream());
                            memResponse.writeObject(m);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
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
        }
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

}
