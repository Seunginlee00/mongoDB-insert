package com.example.demo.controller;

import com.example.demo.domain.License;
import com.example.demo.domain.LicenseCode;
import com.example.demo.domain.LicenseCodeDto;
import com.example.demo.domain.LicenseCodeMongoRepository;
import com.example.demo.domain.LicenseDto;
import com.example.demo.domain.LicenseMongoRepository;
import com.example.demo.domain.MemberDto;
import com.example.demo.domain.MemberMongoRepository;
import com.example.demo.service.ApiService;
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



  private final ApiService apiService;

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
  public ResponseEntity<String> licenseInsertApi(@RequestBody LicenseDto dto) {
    return ResponseEntity.ok(apiService.licenseInsertApi(dto));
  }


  @Transactional
  @PatchMapping("/password")
  public ResponseEntity<String> passwordChange(@RequestBody MemberDto dto) {
    return ResponseEntity.ok(apiService.changePassword(dto));
  }


  @Transactional
  @PatchMapping("/license")
  public ResponseEntity<String> licenseChange(@RequestBody LicenseDto dto) {
    return ResponseEntity.ok(apiService.licenseChange(dto));
  }


  // 라이선스 코드 입력

  @PostMapping("/license-mongo")
  public ResponseEntity<String> licenseInsertDB(@RequestBody LicenseDto dto) {
   return ResponseEntity.ok(apiService.licenseInsertDB(dto));
  }




  @Transactional
  @PatchMapping("/date-change")
  public ResponseEntity<String> licenseDueChange(@RequestBody LicenseDto dto) {


    return ResponseEntity.ok(apiService.licenseDueChange(dto));
  }


  @Transactional
  @DeleteMapping("/license")   // 쓰지 말아봐바 이거 하고 라이선스 추가가 안되네 ;;; 하 열북게 증말
  public ResponseEntity<String> licenseDel(@RequestBody LicenseDto dto) {
    return ResponseEntity.ok(apiService.licenseDueChange(dto));
  }


  @Transactional
  @DeleteMapping("/license2")   // 쓰지 말아봐바 이거 하고 라이선스 추가가 안되네 ;;; 하 열북게 증말
  public ResponseEntity<String> licenseDel2(@RequestBody LicenseDto dto) {
    return ResponseEntity.ok(apiService.licenseDel2(dto));
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

    return ResponseEntity.ok(columnData);

  }



  @Transactional
  @PostMapping("/nickname")
  public ResponseEntity<String> ninckNamePost(@RequestBody LicenseDto dto) {

    return ResponseEntity.ok(apiService.nickNameInsert(dto));

  }


  @Transactional
  @PostMapping("/user")
  public ResponseEntity<String> createUser(@RequestBody MemberDto dto) {

    return ResponseEntity.ok(apiService.createUser(dto));

  }


}