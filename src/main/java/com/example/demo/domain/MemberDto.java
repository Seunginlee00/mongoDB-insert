package com.example.demo.domain;

public record MemberDto(
    String type,
    int memberNum, // 개수
    int startNum, // 시작 숫자
    String email,
    String changePassword
) {

}
