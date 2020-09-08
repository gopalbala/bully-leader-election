package com.gb.bullyelection;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Member implements Serializable {
    private final InetSocketAddress address;
    private final int id;
    private final int electionPort;
    private final int keepAlivePort;
    private final int discoveryPort;
    private boolean coordinator;
    @Setter
    private Map<Integer,Member> peers;
    public Member(InetSocketAddress address, int id,
                  int electionPort,
                  int keepAlivePort, int discoveryPort) {
        this.address = address;
        this.id = id;
        this.electionPort = electionPort;
        this.keepAlivePort = keepAlivePort;
        this.discoveryPort = discoveryPort;
        peers = new HashMap<>();
    }
}
