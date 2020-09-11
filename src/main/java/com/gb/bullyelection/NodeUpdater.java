package com.gb.bullyelection;

import java.io.IOException;

public interface NodeUpdater {
    void nodeAdded(Member member) throws IOException;
    void nodeRemoved(Member member);
}
