package com.gb.bullyelection;

import lombok.Getter;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommunicationService implements NodeUpdater {

    private Member self;
    private DatagramSocket datagramSocket;
    byte[] receivedBuffer = new byte[2048];
    DatagramPacket receivePacket;

    public CommunicationService(Member member) throws IOException {
        self = member;
        try {
            InetAddress address = InetAddress.getByName(self.getHost());
            datagramSocket = new DatagramSocket(member.getDiscoveryPort(), address);
        } catch (SocketException e) {
            System.out.println("Could not create socket connection");
            e.printStackTrace();
        }
        receivePacket =
                new DatagramPacket(receivedBuffer, receivedBuffer.length);
        this.receiver();
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

    public void membershipAdditions(Member clusterMember) {
        System.out.println("Registering to cluster initial node ["
                + clusterMember.getHost() + ":" + clusterMember.getDiscoveryPort() + "]");
        self.getPeers().putIfAbsent(self.getId(), self);
        if (clusterMember == null) {
            System.out.println("ready to receive members for " + self.getId());
        } else {
            System.out.println("Registering to cluster " + clusterMember.getDataPort());
            self.getPeers().putIfAbsent(clusterMember.getId(), clusterMember);
        }
        this.receiver();
        nodeAdded(clusterMember);
    }

    @Override
    public void nodeAdded(Member member) {
        List<Member> members = new ArrayList(self.getPeers().values());
        for (Member m : members) {
            System.out.println("Sending message to ["
                    + m.getHost() + ":" + m.getDiscoveryPort() + "]");
            advertiseSelf(m);
        }
        for (int i = 0; i < members.size(); i++) {
            for (int j = 0; j < members.size(); j++) {
                advertisePeer(members.get(i), members.get(j));
            }
        }
    }

    @Override
    public void nodeRemoved(Member member) {

    }

    public void receiveNodeAddedMessage() {
        System.out.println("Receiver started");
        {
            System.out.println("starting........");

            try {
                datagramSocket.receive(receivePacket);
                ObjectInputStream objectInputStream =
                        new ObjectInputStream(
                                new ByteArrayInputStream(receivePacket.getData()));
                System.out.println("received");
                Member member = null;
                try {
                    member = (Member) objectInputStream.readObject();
                    System.out.println("Received Member message from [" + member.getHost()
                            + ":" + member.getDiscoveryPort() + "]");
                    if (self.getPeers().get(member.getId()) == null) {
                        self.getPeers().putIfAbsent(member.getId(), member);
                        nodeAdded(member);
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    for (Member m : member.getPeers().values()) {
                        self.getPeers().putIfAbsent(m.getId(), m);
                    }
                    if (self.getPeers() != null) {
                        for (Member m : self.getPeers().values()) {
                            System.out.println("I have knowledge about peer [" + m.getHost()
                                    + ":" + m.getDiscoveryPort() + "]");
                        }
                    }
                    objectInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void advertiseSelf(Member member) {
        try {
            InetSocketAddress inetSocketAddress =
                    new InetSocketAddress(member.getHost(), member.getDiscoveryPort());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(self);
            objectOutputStream.flush();
            byte[] buffer = byteArrayOutputStream.toByteArray();
            DatagramPacket packet = new DatagramPacket
                    (buffer, buffer.length, inetSocketAddress.getAddress(), member.getDiscoveryPort());
            try {
                datagramSocket.send(packet);
                System.out.println("Sent info to member "
                        + member.getHost() + ":" + member.getDiscoveryPort()
                );
            } catch (IOException e) {
                System.out.println("Fatal error trying to send: "
                        + packet + " to [" + member.getHost() + ":" + member.getPort() + "]");
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void advertisePeer(Member to, Member about) {
        try {
            InetSocketAddress inetSocketAddress =
                    new InetSocketAddress(to.getHost(), to.getDiscoveryPort());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(about);
            objectOutputStream.flush();
            byte[] buffer = byteArrayOutputStream.toByteArray();
            DatagramPacket packet = new DatagramPacket
                    (buffer, buffer.length, inetSocketAddress.getAddress(), to.getDiscoveryPort());
            try {
                datagramSocket.send(packet);
                System.out.println("Sent info to member "
                        + to.getHost() + ":" + to.getDiscoveryPort()
                );
            } catch (IOException e) {
                System.out.println("Fatal error trying to send: "
                        + packet + " to [" + to.getHost() + ":" + to.getPort() + "]");
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiver() {
        new Thread(() -> {
            {
                while (true)
                    receiveNodeAddedMessage();
            }
        }).start();
    }
}
