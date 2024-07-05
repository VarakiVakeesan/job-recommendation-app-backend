package com.mora.jobrecommendationapp.services;

import com.mora.jobrecommendationapp.DTO.CreateJobProviderRequestDTO;
import com.mora.jobrecommendationapp.DTO.CreateJobProviderResponseDTO;
import com.mora.jobrecommendationapp.DTO.LoginJobProviderRequestDTO;
import com.mora.jobrecommendationapp.DTO.LoginResponse;
import com.mora.jobrecommendationapp.JwtAuthenticationConfig.JWTAuthentication;
import com.mora.jobrecommendationapp.entities.JobProvider;
import com.mora.jobrecommendationapp.entities.JobSeeker;
import com.mora.jobrecommendationapp.repositories.JobProviderRepository;
import com.mora.jobrecommendationapp.repositories.JobSeekerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobProviderService {
    @Autowired
    JobProviderRepository jobProviderRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private JWTAuthentication jwtA;

    @Autowired
    public JobProviderService(JobProviderRepository jobProviderRepository , BCryptPasswordEncoder bCryptPasswordEncoder, JWTAuthentication jwtA) {
        this.jobProviderRepository = jobProviderRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtA = jwtA;
    }

    public CreateJobProviderResponseDTO createJobProvider(CreateJobProviderRequestDTO jobProviderRequest) {
        String encryptedPassword = bCryptPasswordEncoder.encode(jobProviderRequest.getPassword());

        JobProvider jobProvider = JobProvider.builder()
                .companyName(jobProviderRequest.getCompanyName())
                .industry(jobProviderRequest.getIndustry())
                .email(jobProviderRequest.getEmail())
                .userName(jobProviderRequest.getUserName())
                .password(encryptedPassword)
                .phoneNumber(jobProviderRequest.getPhoneNumber())
                .address(jobProviderRequest.getAddress())
                .registeredDate(jobProviderRequest.getRegisteredDate())
                .build();
        jobProviderRepository.save(jobProvider);
        CreateJobProviderResponseDTO createJobProviderResponseDTO = CreateJobProviderResponseDTO.builder().
                message("Job Provider Created Successfully for the company "+jobProviderRequest.getCompanyName())
                .build();
        return createJobProviderResponseDTO;
    }

    public List<JobProvider> getAllJobProvider() {
        return jobProviderRepository.findAll();
    }

    public LoginResponse performlogin(LoginJobProviderRequestDTO loginJobProviderRequestDTO) {
        JobProvider jobProvider = jobProviderRepository.findByUserName(loginJobProviderRequestDTO.getUserName());
        String userName = loginJobProviderRequestDTO.getUserName();
        String password = loginJobProviderRequestDTO.getPassword();

        if (jobProvider == null) {
            return new LoginResponse("User not found", false,null);
        } else {
            boolean isPasswordMatched = bCryptPasswordEncoder.matches(password, jobProvider.getPassword());
            if (isPasswordMatched) {
                String token= jwtA.generateTokenForJobProvider(userName);
                return new LoginResponse("Login Successful", true,token);
            } else {
                return new LoginResponse("Incorrect Password", false,null);
            }
        }
    }
}
