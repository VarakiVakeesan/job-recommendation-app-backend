package com.mora.jobrecommendationapp.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CreateApplicationRequestDTO {

    private String applicationStatus;
    private Date jobAppliedDate;
    private Long jobSeekerId;
    private Long jobId;

}
