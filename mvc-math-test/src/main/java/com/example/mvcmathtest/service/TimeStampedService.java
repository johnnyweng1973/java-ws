package com.example.mvcmathtest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mvcmathtest.model.TimeStampedModel;
import com.example.mvcmathtest.repository.TimeStampedRepository;


@Service
public class TimeStampedService {

    @Autowired
    private TimeStampedRepository recordRepository;

    public TimeStampedModel getLatestRecord() {
        return recordRepository.findTopByOrderByCreatedAtDesc();
    }
}
