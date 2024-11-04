package com.example.demo.domain;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberMongoRepository extends MongoRepository<Member, String> {

  Member findByEmail(String email);



}