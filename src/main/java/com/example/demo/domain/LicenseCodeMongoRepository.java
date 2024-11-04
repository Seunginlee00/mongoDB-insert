package com.example.demo.domain;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseCodeMongoRepository extends MongoRepository<LicenseCode, String> {

  LicenseCode findByLicenseCode(String licenseCode);

}