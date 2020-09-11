package com.gb.bullyelection;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommunicationService implements NodeUpdater {

    private Member self;
    private ServerSocket serverSocket;

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
        //System.out.println("Registering to cluster");
        if (clusterMember == null) {
            System.out.println("ready to receive members for " + self.getId());
            self.getPeers().putIfAbsent(self.getId(), self);
            while (true) {
                Socket socket = serverSocket.accept();
                ObjectInputStream memRequest = new
                        ObjectInputStream(socket.getInputStream());
                Member member = (Member) memRequest.readObject();
                System.out.println("here");
                if (member.getId() != self.getId()) {
                    if (!self.getPeers().containsKey(member.getId()))
                        System.out.println("Discovered new member " + member.getId());

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
                } else {
                    System.out.println(member.getId());
                }
            }
        } else {
            System.out.println("Registering to cluster");
            Socket clSock = new Socket(clusterMember.getHost(), clusterMember.getDiscoveryPort());
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

    @Override
    public void nodeAdded(Member member) throws IOException {
        DatagramSocket datagramSocket = null;
        byte[] receivedBuffer = new byte[1024];
        DatagramPacket receivePacket =
                new DatagramPacket(receivedBuffer, receivedBuffer.length);
        try {
            datagramSocket = new DatagramSocket(member.getPort());
        } catch (SocketException e) {
            System.out.println("Could not create socket connection");
            e.printStackTrace();
        }

        for (Member m : member.getPeers().values()) {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(m.getHost(), m.getPort());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(m);
            oos.flush();
            byte[] buffer = baos.toByteArray();
            DatagramPacket packet = new DatagramPacket
                    (buffer, buffer.length, inetSocketAddress.getAddress(), m.getPort());
            try {
                datagramSocket.send(packet);
            } catch (IOException e) {
                System.out.println("Fatal error trying to send: "
                        + packet + " to [" + m.getHost() + ":" + m.getPort() + "]");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void nodeRemoved(Member member) {

    }

    public void receiveNodeAddedMessage() {
        try {
            DatagramSocket datagramSocket = null;
            byte[] receivedBuffer = new byte[1024];
            DatagramPacket receivePacket =
                    new DatagramPacket(receivedBuffer, receivedBuffer.length);
            try {
                datagramSocket = new DatagramSocket(self.getPort());
            } catch (SocketException e) {
                System.out.println("Could not create socket connection");
                e.printStackTrace();
            }

            datagramSocket.receive(receivePacket);
            ObjectInputStream objectInputStream =
                    new ObjectInputStream(
                            new ByteArrayInputStream(receivePacket.getData()));
            Member member = null;
            try {
                member = (Member) objectInputStream.readObject();
                System.out.println("Received Member message from [" + member.getHost()
                        + ":" + member.getPort() + "]");
                self.getPeers().putIfAbsent(member.getId(), member);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                objectInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiver(Socket socket) throws IOException, ClassNotFoundException {
        while (true) {
            ObjectInputStream memRequest = new ObjectInputStream(socket.getInputStream());
            Member member = (Member) memRequest.readObject();
            System.out.println("Discovered new member " + member.getId());
            if (member.getId() != self.getId()) {
                self.getPeers().putIfAbsent(member.getId(), member);
                new Thread(() -> {
                    try {
                        this.nodeAdded(member);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }


}
