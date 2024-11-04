package com.example.demo.domain;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseMongoRepository extends MongoRepository<License, String> {

  License findByEmailAndTypeAndLicenseGrade(String email, String type, String grade);

  List<License> findByEmailAndType(String email, String type);

  List<License> findByEmail(String email);


}