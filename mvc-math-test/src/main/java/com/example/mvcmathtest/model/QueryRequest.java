package com.example.mvcmathtest.model;

import com.example.mvcmathtest.util.TestSubjectType;

public class QueryRequest {
	private String queryType;
    private String startDate;
    private String endDate;
    private String category;
    private String subCategory;
    private TestSubjectType subject;
    
    // Getters and setters
    public String getQueryType() {
    	return queryType;
    }
    
    public void setQueryType(String queryType) {
    	this.queryType = queryType;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getEndDate() {
        return endDate;
    }
    
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }

	public String getSubCategory() {
		return subCategory;
	}
	
	public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
	
	public TestSubjectType getSubject() {
		return subject;
	}
	
	public void setSubject(TestSubjectType subject) {
		this.subject = subject;
	}
}
