package com.example.demo.service;

import com.example.demo.domain.License;
import com.example.demo.domain.LicenseCode;
import com.example.demo.domain.LicenseCodeMongoRepository;
import com.example.demo.domain.LicenseDto;
import com.example.demo.domain.LicenseMongoRepository;
import com.example.demo.domain.Member;
import com.example.demo.domain.MemberDto;
import com.example.demo.domain.MemberMongoRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiService {

  private final MemberMongoRepository memberMongoRepository;
  private final PasswordEncoder passwordEncoder;
  private final LicenseMongoRepository licneseMongoRepository;
  private final LicenseCodeMongoRepository licenseCodeMongoRepository;
  private final MongoTemplate mongoTemplate;


  public List<License> getLicense(String email, String type) {
    return licneseMongoRepository.findByEmailAndType(email, type);
  }


  public License getLicense(String email, String type, String grade) {
    return licneseMongoRepository.findByEmailAndTypeAndLicenseGrade(email, type,grade);
  }


  // 받은 email을 DTO 리스트로 만들어 주는 함수
  public List<LicenseDto> getEmailDto(LicenseDto dto){

    List<LicenseDto> dtos = new ArrayList<>();
    String front = dto.getEmail().split("@")[0];
    String after = dto.getEmail().split("@")[1];

    for (int i = 0; i < dto.getLicenseNum(); i++) {
      LicenseDto dto2 = new LicenseDto();

      if(dto.getType().equals("00")) {

        if (dto.getStartNum() + i < 10) {
          dto2.setEmail(front + "0" + (dto.getStartNum() + i) + "@" + after);
          dto2.setNickName(dto.getNickName()+ "0" + (dto.getStartNum() + i));
        } else {
          // 10~ 1000 이상
          dto2.setEmail(front + (dto.getStartNum() + i) + "@" + after);
          dto2.setNickName(dto.getNickName() + (dto.getStartNum() + i));
        }

      }else {

        if (dto.getStartNum()+i < 10) {
          dto2.setEmail(front + "00" + (dto.getStartNum() + i) + "@" + after);
          dto2.setNickName(dto.getNickName()+ "00" + (dto.getStartNum() + i));
        } else if (dto.getStartNum()+i < 100) {
          // 100 보다 작을때
          dto2.setEmail(front + "0" + (dto.getStartNum() + i) + "@" + after);
          dto2.setNickName(dto.getNickName()+ "0" + (dto.getStartNum() + i));
        } else {
          // 100~ 1000 이상
          dto2.setEmail(front + (dto.getStartNum() + i) + "@" + after);
          dto2.setNickName(dto.getNickName() + (dto.getStartNum() + i));
        }

      }

      log.info("00 email" + dto2.getEmail());
      dto2.setGroup(dto.getGroup());
      dto2.setExpireDate(dto.getExpireDate());
      dto2.setLicenseGrade(dto.getLicenseGrade());
      dto2.setLicenseType(dto.getLicenseType());
      dto2.setChangeGrade(dto.getChangeGrade());


      dtos.add(dto2);
    }
    return dtos;
  }



  // api 로 라이선스 입력하는 함수
  public String licenseInsertApi(LicenseDto dto){
    // 2개 테스트
    List<LicenseDto> dtos;

    dtos = getEmailDto(dto);

    WebClient webClient =
        WebClient.builder()
            .baseUrl("https://api.vrware.us")
            .build();

    for (LicenseDto licenseDto : dtos) {
      log.info("licenseDto.getEmail()={}", licenseDto.getEmail());

      String licenseMono = webClient.post()
          .uri("/api/v1.1/licenses")
          .bodyValue(licenseDto)
          .headers(httpHeaders -> {
            httpHeaders.add("clientId", "VAHQA1PIWDZQ7OHFNQFJ");
            httpHeaders.add("clientSecretKey", "o1M176Dmbcx05cqyV2sVRZktdx6wmwBK3Lpas0Ck");
          })
          .retrieve()
          .onStatus(status -> status.is4xxClientError(), clientResponse -> {
            if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
              return Mono.error(new RuntimeException("Resource not found"));
            }
            return Mono.error(new RuntimeException("Client error"));
          })
          .bodyToMono(String.class)
          .block();



    }
    return "success";
  }


  //패스워드 변경하는 함수
  public String changePassword(MemberDto dto){
    List<LicenseDto> dtos = new ArrayList<>();
    String front = dto.email().split("@")[0];
    String after = dto.email().split("@")[1];

    if (dto.memberNum() != 1) {
      for (int i = 0; i < dto.memberNum(); i++) {
        LicenseDto dto2 = new LicenseDto();

        if(dto.type().equals("00")) {

          if (dto.startNum() + i < 10) {
            dto2.setEmail(front + "0" + (dto.startNum() + i) + "@" + after);
          } else {
            // 10~ 1000 이상
            dto2.setEmail(front + (dto.startNum() + i) + "@" + after);
          }

        }else {

          if (dto.startNum()+i < 10) {
            dto2.setEmail(front + "00" + (dto.startNum() + i) + "@" + after);
          } else if (dto.startNum()+i < 100) {
            // 100 보다 작을때
            dto2.setEmail(front + "0" + (dto.startNum() + i) + "@" + after);
          } else {
            // 100~ 1000 이상
            dto2.setEmail(front + (dto.startNum() + i) + "@" + after);
          }

        }
        dtos.add(dto2);
      }
    } else {
      // 변경할 게 한개인 경우
      if(dto.memberNum() == 1){
        String ecodePw = passwordEncoder.encode(dto.changePassword());
        Query query = new Query(Criteria.where("email").is(dto.email()));
        Update update = new Update().set("password", ecodePw);
        mongoTemplate.updateFirst(query, update, Member.class);

      }
      return "패스워드 변경 완료(단일)";

    }

    for (LicenseDto licenseDto : dtos) {

      String ecodePw = passwordEncoder.encode(dto.changePassword());
      Query query = new Query(Criteria.where("email").is(licenseDto.getEmail()));
      Update update = new Update().set("password", ecodePw);

      mongoTemplate.updateFirst(query, update, Member.class);

    }
    return "패스워드 변경 완료(대량)";
  }



  //라이선스 타입을 변경 하는 함수
  public String licenseChange(LicenseDto dto){
    List<LicenseDto> dtos;

    if (dto.getLicenseNum() != 1) {
      dtos = getEmailDto(dto);
    } else {
      // 변경할 게 한개인 경우
      if(dto.getLicenseNum() == 1){
        log.info("email={}" + dto.getEmail());
        log.info("dto = {}"+ dto.getLicenseType());

        License license = getLicense(dto.getEmail(), dto.getLicenseType(),dto.getLicenseGrade());
        license.gradeChange(dto.getChangeGrade());

        licneseMongoRepository.save(license);

      }
      return "라이선스 변경 완료(단일)";

    }

    for (LicenseDto licenseDto : dtos) {

      License license = getLicense(licenseDto.getEmail(), dto.getLicenseType(),dto.getLicenseGrade());
      log.info("id={}", license.getId());
      log.info("nickname={}", license.getLicenseGrade());
      log.info("nickname={}", license.getType());

      license.gradeChange(dto.getChangeGrade());

      licneseMongoRepository.save(license);
    }
    return "라이선스 변경 완료(대량)";
  }


  // 라이선스 입력
  public String licenseInsertDB(LicenseDto dto){
    List<LicenseDto> dtos;

    if (dto.getLicenseNum() != 1) {
      dtos = getEmailDto(dto);
    } else {
      // 변경할 게 한개인 경우
      if(dto.getLicenseNum() == 1){

        Member member = memberMongoRepository.findByEmail(dto.getEmail());

        License license =
            License.builder()
                .email(dto.getEmail())
                .type(dto.getLicenseType())
                .licenseGrade(dto.getLicenseGrade())
                .purchaseDate(LocalDate.now())
                .expireDate(dto.getExpireDate())
                .memberId(member.getId())
                .group(dto.getGroup())
                .__v(dto.get__v())
                .build();

        licneseMongoRepository.save(license);

      }
      return "라이선스 입력 완료(단일)";

    }

    List<License> list = new ArrayList<>();
    for (LicenseDto licenseDto : dtos) {

      Member member = memberMongoRepository.findByEmail(licenseDto.getEmail());
      log.info("member id={}", member.getId());

      License newLicense =
          License.builder()
              .email(licenseDto.getEmail())
              .type(dto.getLicenseType())
              .licenseGrade(dto.getLicenseGrade())
              .purchaseDate(LocalDate.now())
              .expireDate(dto.getExpireDate())
              .memberId(member.getId())
              .group(dto.getGroup())
              .__v(dto.get__v())
              .build();

      log.info("id={}", newLicense.getId());
      log.info("grade ={}", newLicense.getLicenseGrade());
      log.info("email ={}", newLicense.getEmail());
      log.info("new member id ={}", newLicense.getMemberId());

      list.add(newLicense);
    }

    //  이유는 모르겠으나.. 저렇게 return 값 주고 또 됨... 하.. 모르겠네..
    // nosql이라 값을 지우고 다시 넣다보면 좀 뭔가 꼬이는건지.. 아니면 return이 꼭 있어야하는건지..

    return licneseMongoRepository.saveAll(list).get(0).getId();
  }


  public String licenseDueChange(LicenseDto dto){
    List<LicenseDto> dtos;

    if (dto.getLicenseNum() != 1) {
      dtos = getEmailDto(dto);
    } else {
      // 변경할 게 한개인 경우

      log.info("email={}" + dto.getEmail());
      log.info("dto = {}"+ dto.getLicenseType());

      License license = getLicense(dto.getEmail(), dto.getLicenseType(),dto.getLicenseGrade());
      license.expireDateChange(dto.getExpireDate());

      licneseMongoRepository.save(license);

      return "라이선스 기간 변경(단일)";

    }

    for (LicenseDto licenseDto : dtos) {

      License license = getLicense(licenseDto.getEmail(), dto.getLicenseType(), dto.getLicenseGrade());
      log.info("id={}", license.getId());
      log.info("nickname={}", license.getLicenseGrade());
      log.info("nickname={}", license.getType());

      license.expireDateChange(dto.getExpireDate());

      licneseMongoRepository.save(license);
    }
    return "라이선스 기간 변경(대량)";
  }


  public String nickNameInsert(LicenseDto dto){
    List<LicenseDto> dtos;

    dtos = getEmailDto(dto);


    for (LicenseDto licenseDto : dtos) {

      Query query = new Query(Criteria.where("email").is(licenseDto.getEmail()));
      Update update = new Update().set("nickname", licenseDto.getNickName());

      mongoTemplate.updateFirst(query, update, Member.class);

    }
    return "닉네임 변경 완료(대량)";
  }



}
