package com.baeldung.crud.repositories;

import com.baeldung.crud.entities.TestUser;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<TestUser, Long> {
    
    List<TestUser> findByName(String name);
    
}
