package com.example.demo.domain;

import java.time.LocalDate;
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
@Document(collection = "license_codes")
public class LicenseCode {
  @Id
  private String id;
  private String licenseType;   //라이선스 타입
  private String licenseGrade;
  private String licenseCode;
  private Integer licenseDate;
  private LocalDate startDate;
  private LocalDate endDate;
  private LocalDate regDate;
  private LocalDate modDate;
  private Integer usageLimit;
  private Integer __v;




}
