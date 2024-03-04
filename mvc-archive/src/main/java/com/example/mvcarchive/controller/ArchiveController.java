package com.example.mvcarchive.controller;

import java.io.IOException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.example.mvcarchive.googleuploader.GoogleUploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class ArchiveController {
	private static final Logger log = LoggerFactory.getLogger(ArchiveController.class);

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private GoogleUploader googleUploader;

	@Value("${memo.service.url}")
	private String memoServiceUrl;

	@GetMapping("/archive")
	@ResponseBody
	public ResponseEntity<String> archiveMemosToGoogleDrive() {

		log.info("memoServiceUrl: {}", memoServiceUrl);

		ResponseEntity<String> memoListResponse = restTemplate.getForEntity(memoServiceUrl, String.class);
		if (memoListResponse.getStatusCode() == HttpStatus.OK) {
			String memoList = memoListResponse.getBody();
			try {
				googleUploader.uploadJsonToGoogleDrive(memoList);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ResponseEntity.ok("Memo list archived successfully");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to retrieve memo list from Memo Service");
		}
	}

	private void saveMemosToGoogleDrive(String memoList) {
		// Implement the logic to save the memo list to Google Drive
		// This could involve using the Google Drive API or a client library for
		// interacting with Google Drive
		// Implement your logic here
	}
}
