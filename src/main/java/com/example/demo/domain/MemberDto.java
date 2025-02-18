package com.example.demo.domain;

import java.time.LocalDate;
import lombok.Data;

@Data
public class MemberDto{
  private String type;
  private int memberNum; // 개수
  private int startNum; // 시작 숫자
  private String email;
  private String changePassword;
  private String password;
  private String nickName;
  private String memberType;
  private Boolean receiveMarketing;
  private String isVrwareEmail;
  private String group;
  private LocalDate expireDate;
  private String licenseGrade;
  private String licenseType;
  private String changeGrade;
  private String firstQuestion;
  private String secondQuestion;

}
