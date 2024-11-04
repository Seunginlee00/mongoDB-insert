package com.example.demo.domain;

import java.time.LocalDate;
import lombok.Data;

public record LicenseCodeDto(

    String licenseType,   //라이선스 타입
    String licenseGrade,
    String licenseCode,
    Integer licenseDate,
    LocalDate startDate,
    LocalDate endDate,
    LocalDate regDate,
    LocalDate modDate,
    Integer usageLimit,
    Integer __v
) {


  public LicenseCode toEntity() {
    return LicenseCode.builder()
        .licenseType(licenseType())
        .licenseGrade(licenseGrade())
        .licenseCode(licenseCode())
        .licenseCode(licenseCode())
        .startDate(LocalDate.now())
        .endDate(endDate())
        .regDate(LocalDate.now())
        .modDate(null)
        .usageLimit(usageLimit())
        .__v(0)
        .build();
  }

}
