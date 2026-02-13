package com.lifebalanceapp.dto;

import jakarta.validation.constraints.Size;

public class ProfileUpdateRequestDto {

    @Size(max = 100, message = "Full name must be at most 100 characters")
    private String fullName;

    @Size(max = 2000, message = "About must be at most 2000 characters")
    private String about;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }
}
