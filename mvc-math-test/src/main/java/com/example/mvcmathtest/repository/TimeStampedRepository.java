package com.example.mvcmathtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.mvcmathtest.model.TimeStampedModel;
import java.util.List;

@Repository
public interface TimeStampedRepository extends JpaRepository<TimeStampedModel, Long> {

    TimeStampedModel findTopByOrderByCreatedAtDesc();
}
