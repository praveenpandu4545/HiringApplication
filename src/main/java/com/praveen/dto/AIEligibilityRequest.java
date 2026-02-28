package com.praveen.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIEligibilityRequest {

    @JsonProperty("resume_text")
    private String resumeText;

    @JsonProperty("drive_requirements")
    private String driveRequirements;

}