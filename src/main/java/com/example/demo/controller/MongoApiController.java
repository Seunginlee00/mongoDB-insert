package com.example.demo.controller;

import com.example.demo.domain.License;
import com.example.demo.domain.LicenseCode;
import com.example.demo.domain.LicenseCodeDto;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MongoApiController {

  private final MemberMongoRepository memberMongoRepository;
  private final PasswordEncoder passwordEncoder;
  private final LicenseMongoRepository licneseMongoRepository;
  private final LicenseCodeMongoRepository licenseCodeMongoRepository;
  private final MongoTemplate mongoTemplate;



  @RequestMapping("/index")
  public String index() {
    return "index";
  }


  @GetMapping("/test")
  public String testGet() {

    WebClient webClient =
        WebClient.builder()
            .baseUrl("https://api.vrware.us")
            .build();

// 빌더
    String baseUrl = "https://api.vrware.us";

    Mono<License> licenseMono = webClient.get()
        .uri(builder -> builder
            .path("/api/v1.1/licenses")
            .queryParam("email", "si.lee@globepoint.co.kr")
            .build())
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
        .bodyToMono(License.class);

    System.out.println(licenseMono);

    return null;

  }

  @PostMapping("/license")
  public String testPost(@RequestBody LicenseDto dto) {

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

    return null;

  }


  @Transactional
  @PatchMapping("/password")
  public String passwordChange(@RequestBody MemberDto dto) {
    // 2개 테스트
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
      return null;

    }

    for (LicenseDto licenseDto : dtos) {

      String ecodePw = passwordEncoder.encode(dto.changePassword());
      Query query = new Query(Criteria.where("email").is(licenseDto.getEmail()));
      Update update = new Update().set("password", ecodePw);

      mongoTemplate.updateFirst(query, update, Member.class);

    }

    return null;
  }


  @Transactional
  @PatchMapping("/license")
  public String licenseChange(@RequestBody LicenseDto dto) {
    // 2개 테스트
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
      return null;

    }

    for (LicenseDto licenseDto : dtos) {

      License license = getLicense(licenseDto.getEmail(), dto.getLicenseType(),dto.getLicenseGrade());
      log.info("id={}", license.getId());
      log.info("nickname={}", license.getLicenseGrade());
      log.info("nickname={}", license.getType());

      license.gradeChange(dto.getChangeGrade());

      licneseMongoRepository.save(license);
    }

    return null;
  }


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



  @Transactional
  @PatchMapping("/date-change")
  public String licenseDueChange(@RequestBody LicenseDto dto) {
    // 2개 테스트
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

      return null;

    }

    for (LicenseDto licenseDto : dtos) {

      License license = getLicense(licenseDto.getEmail(), dto.getLicenseType(), dto.getLicenseGrade());
      log.info("id={}", license.getId());
      log.info("nickname={}", license.getLicenseGrade());
      log.info("nickname={}", license.getType());

      license.expireDateChange(dto.getExpireDate());

      licneseMongoRepository.save(license);
    }

    return null;
  }


  @Transactional
  @DeleteMapping("/license")   // 쓰지 말아봐바 이거 하고 라이선스 추가가 안되네 ;;; 하 열북게 증말
  public String licenseDel(@RequestBody LicenseDto dto) {
    // 2개 테스트
    List<LicenseDto> dtos;

    if (dto.getLicenseNum() != 1) {
      dtos = getEmailDto(dto);
    } else {
      // 변경할 게 한개인 경우
      if(dto.getLicenseNum() == 1){
        log.info("email={}" + dto.getEmail());
        log.info("dto = {}"+ dto.getLicenseType());

        List<License> license = getLicense(dto.getEmail(), dto.getLicenseType());

        licneseMongoRepository.deleteAll(license);

      }
      return null;

    }

    for (LicenseDto licenseDto : dtos) {

      List<License> license = getLicense(licenseDto.getEmail(), dto.getLicenseType());

      licneseMongoRepository.deleteAll(license);

    }

    return null;
  }




  @PostMapping(value = "/license-code")
  public ResponseEntity<List<String>> codePost(
      @RequestPart MultipartFile file,
      @RequestPart LicenseCodeDto dto) throws IOException {

    List<String> columnData = new ArrayList<>();
    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트를 읽습니다.

      // 헤더 제외하고 데이터만 읽기
      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row != null) {
          Cell cell = row.getCell(1); // 원하는 컬럼 인덱스 설정 (여기서는 첫 번째 컬럼)
          if (cell != null) {
            columnData.add(cell.toString().trim());
          }
        }
      }
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    log.info("grade = {}",dto.licenseGrade());
    log.info("type = {}",dto.licenseType());

    for (String columnDatum : columnData) {

      licenseCodeMongoRepository.save(
          LicenseCode.builder()
              .licenseType(dto.licenseType())
              .licenseGrade(dto.licenseGrade())
              .licenseCode(columnDatum)
              .licenseDate(dto.licenseDate())
              .startDate(LocalDate.now())
              .regDate(LocalDate.now())
              .endDate(dto.endDate())
              .modDate(null)
              .usageLimit(dto.usageLimit())
              .__v(0)
              .build()
      );


    }


//    return licenseCodeMongoRepository.save(dto.toEntity()).getId();

    return ResponseEntity.ok(columnData);

  }



  @DeleteMapping(value = "/license-code", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<List<String>> codeDelete(
      @RequestParam MultipartFile file,
      @ModelAttribute LicenseCodeDto dto) throws IOException {

    List<String> columnData = new ArrayList<>();
    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트를 읽습니다.

      // 헤더 제외하고 데이터만 읽기
      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row != null) {
          Cell cell = row.getCell(1); // 원하는 컬럼 인덱스 설정 (여기서는 첫 번째 컬럼)
          if (cell != null) {
            columnData.add(cell.toString().trim());
          }
        }
      }
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    for (String columnDatum : columnData) {

        LicenseCode code = licenseCodeMongoRepository.findByLicenseCode(columnDatum);
      licenseCodeMongoRepository.delete(code);

    }


//    return licenseCodeMongoRepository.save(dto.toEntity()).getId();

    return ResponseEntity.ok(columnData);

  }



  @Transactional
  @PostMapping("/nickname")
  public String ninckNamePost(@RequestBody LicenseDto dto) {

    // 2개 테스트
    List<LicenseDto> dtos;

    dtos = getEmailDto(dto);


    for (LicenseDto licenseDto : dtos) {

        Query query = new Query(Criteria.where("email").is(licenseDto.getEmail()));
        Update update = new Update().set("nickname", licenseDto.getNickName());

        mongoTemplate.updateFirst(query, update, Member.class);

    }

    return null;

  }








  public Member getMemberByEmail(String email) {
    return memberMongoRepository.findByEmail(email);
  }


  public List<License> getLicense(String email, String type) {
    return licneseMongoRepository.findByEmailAndType(email, type );
  }


  public License getLicense(String email, String type, String grade) {
    return licneseMongoRepository.findByEmailAndTypeAndLicenseGrade(email, type,grade);
  }

  public List<License> getEmail(String email) {
    log.info("email={}",email);
    return licneseMongoRepository.findByEmail(email);
  }

}