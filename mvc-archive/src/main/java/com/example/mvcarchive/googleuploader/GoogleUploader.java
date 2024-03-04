package com.example.mvcarchive.googleuploader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

@Component
public class GoogleUploader {

    private static final String CSV_FILE_PATH = "data.csv";

    public void uploadJsonToGoogleDrive(String jsonData) throws IOException, JSONException {
        // Convert JSON data to CSV format
        List<String[]> csvData = convertJsonToCsv(jsonData);

        // Write CSV data to a file
        writeDataToCSV(csvData);

        // Upload CSV file to Google Drive
       // uploadCSVToGoogleDrive();
    }

    private List<String[]> convertJsonToCsv(String jsonData) throws JSONException {
        List<String[]> csvData = new ArrayList<>();

        // Parse JSON data
        JSONArray jsonArray = new JSONArray(jsonData);
        if (jsonArray.length() > 0) {
            // Extract header row from the first object
            JSONObject firstObject = jsonArray.getJSONObject(0);
            Iterator<String> keys = firstObject.keys();
            List<String> headers = new ArrayList<>();
            while (keys.hasNext()) {
                headers.add(keys.next());
            }
            csvData.add(headers.toArray(new String[0]));

            // Extract data rows
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                List<String> row = new ArrayList<>();
                for (String header : headers) {
                    row.add(jsonObject.optString(header, ""));
                }
                csvData.add(row.toArray(new String[0]));
            }
        }
        return csvData;
    }

    private void writeDataToCSV(List<String[]> data) throws IOException {
		synchronized (this) {
			try (FileWriter writer = new FileWriter(CSV_FILE_PATH)) {
				for (String[] row : data) {
					writer.append(String.join(",", row));
					writer.append("\n");
				}
			}
		}
    }

//    private void uploadCSVToGoogleDrive() throws IOException {
//        // Initialize Google Drive service
//        Drive service = GoogleDriveService.getDriveService();
//
//        // Define the file metadata
//        File fileMetadata = new File();
//        fileMetadata.setName("data.csv");
//        fileMetadata.setMimeType("text/csv");
//
//        // Set the file content
//        java.io.File filePath = new java.io.File(CSV_FILE_PATH);
//        FileContent mediaContent = new FileContent("text/csv", filePath);
//
//        // Upload the file to Google Drive
//        File file = service.files().create(fileMetadata, mediaContent)
//                .setFields("id")
//                .execute();
//
//        System.out.println("File ID: " + file.getId());
//    }
}
