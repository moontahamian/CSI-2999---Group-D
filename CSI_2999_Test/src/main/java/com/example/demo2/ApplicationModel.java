package com.example.demo2;

public class ApplicationModel {

    private int id;
    private int userId;
    private String companyName;
    private String jobTitle;
    private String dateApplied;
    private String deadline;
    private String status;
    private String location;
    private String description;
    private String notes;

    public ApplicationModel(int id, int userId, String companyName, String jobTitle,
                            String dateApplied, String deadline, String status,
                            String location, String description, String notes) {

        this.id = id;
        this.userId = userId;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.dateApplied = dateApplied;
        this.deadline = deadline;
        this.status = status;
        this.location = location;
        this.description = description;
        this.notes = notes;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getCompanyName() { return companyName; }
    public String getJobTitle() { return jobTitle; }
    public String getDateApplied() { return dateApplied; }
    public String getDeadline() { return deadline; }
    public String getStatus() { return status; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getNotes() { return notes; }
}