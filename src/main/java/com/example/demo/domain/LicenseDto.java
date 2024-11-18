package com.example.demo.domain;

import java.time.LocalDate;
import lombok.Data;

@Data
public class LicenseDto {
  private String type;
  private int licenseNum; // 개수
  private int startNum; // 시작 숫자

  private String licenseType;
  private String nickName;
  private String changeGrade;
  private String licenseGrade;
  private String email;
  private LocalDate expireDate;
  private String group;
  private Integer __v;




}
