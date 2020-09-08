package com.gb.bullyelection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResponse {
    private int responseCode;
    private Member member;
}
