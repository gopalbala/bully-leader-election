package com.gb.bullyelection;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Member implements Serializable {
    private final String host;
    private final int id;
    private final int port;
    private final int electionPort;
    private final int keepAlivePort;
    private final int discoveryPort;
    private boolean coordinator;
    @Setter
    private Map<Integer,Member> peers;
    public Member(String host, int id,
                  int port,
                  int electionPort,
                  int keepAlivePort, int discoveryPort) {
        this.host = host;
        this.id = id;
        this.port = port;
        this.electionPort = electionPort;
        this.keepAlivePort = keepAlivePort;
        this.discoveryPort = discoveryPort;
        peers = new HashMap<>();
    }
}
