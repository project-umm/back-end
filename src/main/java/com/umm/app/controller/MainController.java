package com.umm.app.controller;

import com.umm.app.dto.MainDashboardResponse;
import com.umm.app.impl.CustomUserDetails;
import com.umm.app.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class MainController {

    private final MainService mainService;

    @GetMapping("/dashboard")
    public ResponseEntity<MainDashboardResponse> getDashboard(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        MainDashboardResponse response = mainService.getDashboard(customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
