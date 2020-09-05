package com.gb.bullyelection;

import lombok.Getter;

import java.io.Serializable;
import java.net.InetSocketAddress;

@Getter
public class Member implements Serializable {
    private final InetSocketAddress address;
    private final int id;

    public Member(InetSocketAddress address, int id) {
        this.address = address;
        this.id = id;
    }
}
