package com.example.demo.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "members")
public class Member {
  @Id
  private String id;
  private String email;                            //이메일
  private String password;                        //비밀번호
  private String nickname;                        //별명
  private String memberState;                    //멤버 상태 list) 0: 이메일 미인증, 1: 정상, 2: 휴먼, 3:정지, 4:탈퇴 등
  private String memberType;                    //멤버 유형 list) 1: 일반, 2: 학생, 3: 교사
  private String group;                            //회원가입한 그룹 ex)GP, 쎄다영어, 확인영어 등
  private LocalDateTime joinDate;                        //가입 날짜 ? Date(mongodb) -> driver -> LocalDateTime
  private Integer point;                        //적립금
  private String membership;                    //멤버십 등급,
  private boolean receiveMarketing;
  private LocalDateTime lastLoginDate;                    //마지막 로그인 날짜
  private String pwdCertifiNumber;                //비밀번호 찾기 인증번호 (인증 후 삭제)
  private LocalDateTime pwdCertifiNumberExpireDate;    //비밀번호 찾기 인증번호 만료 시간 3분 (+15초 서버시간과 약 15초 정도 차이가 남)
  private String emailCertifiCode;                //이메일 인증 코드
  private LocalDateTime emailCertifiCodeExpireDate;    //이메일 승인 메일 만료 시간 하루
  private List<Object> securityQuestions;        // Array ??, //vrware.us 계정 보안질문
  private String thumbnail;                        //회원 썸네일 이미지 Url

  public void verifiedUpdate(String memberState) {
    this.memberState = memberState;
    this.emailCertifiCode = null;
    this.emailCertifiCodeExpireDate = null;
  }

  public void cleanPwd() {
    this.password = "";
  }

  public void updatePassword(String password) {
    this.password = password;
  }


  public void cleanIdAndPwd() {
    this.id = "";
    this.password = "";

  }
}
