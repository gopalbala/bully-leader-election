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
    private ServerSocket socket;
    private byte[] receivedBuffer = new byte[1024];
    private DatagramPacket receivePacket =
            new DatagramPacket(receivedBuffer, receivedBuffer.length);

    public CommunicationService(Member member, int portToListen) throws IOException {
        try {
            this.self = member;
            socket = new ServerSocket(portToListen);
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

    public void membershipAdditions() throws IOException, ClassNotFoundException {

        ServerSocket serverSocket = new ServerSocket( self.getDiscoveryPort());
        while (true) {
//            Socket socket = serverSocket.accept();
            System.out.println("ready to receive members");
            ObjectInputStream memRequest = new ObjectInputStream(socket.accept().getInputStream());
            ObjectOutputStream memResponse = new ObjectOutputStream(socket.accept().getOutputStream());
            Member member = (Member) memRequest.readObject();
            self.getPeers().putIfAbsent(member.getId(), member);
            memResponse.writeObject(self.getPeers().values());
        }
    }

    private void notifyMemberAddition(Member newMember) throws IOException {
        for (Member member: self.getPeers().values()) {
            if (self.getId() != member.getId()) {
                ServerSocket serverSocket = new ServerSocket(self.getDiscoveryPort());
                System.out.println("ready to receive members");
                ObjectInputStream memRequest = new ObjectInputStream(socket.accept().getInputStream());
                ObjectOutputStream memResponse = new ObjectOutputStream(socket.accept().getOutputStream());
            }
        }
    }
}
