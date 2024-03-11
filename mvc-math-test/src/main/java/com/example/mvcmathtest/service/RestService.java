package com.example.mvcmathtest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.example.mvcmathtest.dto.MathProblemDTO;
import com.example.mvcmathtest.model.TestProblem;

@Service
public class RestService {
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${math.service.url}")
	private String mathProblemServiceUrl;
    // Create ObjectMapper instance
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<TestProblem> fetchMathProblems(String excludeListString) {
    	List<TestProblem> testProblems = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(excludeListString, headers);

        try {
            ResponseEntity<String> problemListResponse = restTemplate.postForEntity(mathProblemServiceUrl, requestEntity, String.class);
            if (problemListResponse.getStatusCode() == HttpStatus.OK) {
            	String jsonString = problemListResponse.getBody(); 
            	 // Deserialize JSON string into MathProblemDTO object
                List<MathProblemDTO> mathProblems= objectMapper.readValue(jsonString, new TypeReference<List<MathProblemDTO>>(){});
                for(MathProblemDTO problem : mathProblems) {
                	TestProblem testProblem = new TestProblem(problem);
                	testProblems.add(testProblem);
                }
            } else {
                throw new RestClientException("Failed to retrieve problem list from math problem service");
            }
        } catch (RestClientException e) {
            throw new RestClientException("Error occurred while retrieving problem list from math problem service", e);
        } catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return testProblems;
    }
}
