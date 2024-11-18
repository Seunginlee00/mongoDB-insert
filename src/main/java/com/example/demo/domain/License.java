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
@Document(collection = "licenses")
public class License {
  @Id
  private String id;
  private String email;
  private String type;
  private String licenseGrade;
  private LocalDate purchaseDate;
  private LocalDate expireDate;
  private String group;
  private String memberId;
  private String licenseCode;
  private String typee;
  private String licenseGradee;
  private Integer __v;

  public void typeChange(String type){
    this.typee = type;
  }


  public void expireDateChange(LocalDate expireDate){
    this.expireDate = expireDate;
  }


  public void gradeChange(String grade){
    this.licenseGrade = grade;
  }

}
