package com.umm.app.util;

import com.umm.aspect.Logging;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
@Slf4j
public class ClientUtil {

    private static final String[] IP_HEADERS = {
            "X-Forwarded-For",
            "X-Real-IP",
            "X-Original-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * 클라이언트의 실제 IP 주소를 획득합니다.
     * 프록시, 로드밸런서, CDN 등을 고려한 안전한 IP 획득
     */
    public String getClientIp(HttpServletRequest request) {
        if (request == null) {
            log.info("HttpServletRequest is null");
            return UNKNOWN;
        }

        String clientIp = null;

        // 1. 각 헤더를 순차적으로 확인
        for (String header : IP_HEADERS) {
            clientIp = extractIpFromHeader(request, header);
            if (isValidIp(clientIp)) {
                break;
            }
        }

        // 2. 헤더에서 찾지 못한 경우 기본 방법 사용
        if (!isValidIp(clientIp)) {
            clientIp = request.getRemoteAddr();
        }

        // 3. localhost인 경우 실제 IP 획득 시도
        if (isLocalhost(clientIp)) {
            clientIp = getLocalHostIp();
        }

        // 4. 최종 검증
        if (!isValidIp(clientIp)) {
            clientIp = UNKNOWN;
        }

        log.debug("Client IP: {}", clientIp);
        return clientIp;
    }

    /**
     * 헤더에서 IP 추출 (다중 IP 처리)
     */
    private String extractIpFromHeader(HttpServletRequest request, String headerName) {
        String headerValue = request.getHeader(headerName);

        if (headerValue == null || headerValue.trim().isEmpty() || UNKNOWN.equalsIgnoreCase(headerValue)) {
            return null;
        }

        // X-Forwarded-For는 여러 IP가 콤마로 구분될 수 있음 (첫 번째가 원본 클라이언트)
        if (headerValue.contains(",")) {
            headerValue = headerValue.split(",")[0].trim();
        }

        return headerValue;
    }

    /**
     * 유효한 IP인지 검증
     */
    private boolean isValidIp(String ip) {
        if (ip == null || ip.trim().isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            return false;
        }

        // 기본적인 IP 형식 검증
        return ip.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$") ||
                ip.matches("^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
    }

    /**
     * localhost 여부 확인
     */
    private boolean isLocalhost(String ip) {
        return LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip) || "localhost".equals(ip);
    }

    /**
     * 로컬 호스트의 실제 IP 획득
     */
    private String getLocalHostIp() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("Failed to get localhost IP", e);
            return LOCALHOST_IPV4;
        }
    }

    /**
     * 개발/테스트용: 헤더 정보 전체 로그 출력
     */
    public void logAllHeaders(HttpServletRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("=== Client IP Headers ===");
            for (String header : IP_HEADERS) {
                String value = request.getHeader(header);
                if (value != null) {
                    log.debug("{}: {}", header, value);
                }
            }
            log.debug("RemoteAddr: {}", request.getRemoteAddr());
            log.debug("========================");
        }
    }
}
