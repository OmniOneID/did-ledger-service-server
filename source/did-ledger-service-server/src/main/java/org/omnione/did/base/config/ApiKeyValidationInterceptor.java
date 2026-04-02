package org.omnione.did.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.response.ErrorResponse;
import org.omnione.did.base.service.ApiKeyValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * API Key validation interceptor
 * Performs validation only for APIs annotated with @ApiKeyRequired
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ApiKeyValidationInterceptor implements HandlerInterceptor {
    
    private final ApiKeyValidationService apiKeyValidationService;
    private final ObjectMapper objectMapper;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true; // Skip if not a HandlerMethod
        }
        
        ApiKeyRequired annotation = handlerMethod.getMethodAnnotation(ApiKeyRequired.class);
        
        if (annotation == null) {
            return true; // Skip validation if annotation is not present
        }
        
        // Perform API key validation when annotation is present
        log.debug("API Key validation required for: {} {} (required role: {})", 
                 request.getMethod(), request.getRequestURI(), annotation.role());
        
        try {
            ApiKeyValidationService.ValidationResult result = apiKeyValidationService.validateApiKey(
                annotation.role(),
                request
            );
            
            if (!result.isValid()) {
                writeErrorResponse(response, result.getErrorCode());
                return false;
            }
            
            return true;
            
        } catch (OpenDidException e) {
            log.error("Error during API key validation", e);
            writeErrorResponse(response, ErrorCode.API_KEY_VALIDATION_ERROR);
            return false;
        }
    }
    
    /**
     * Writes error response with proper ErrorCode format
     * 
     * @param response HTTP response object
     * @param errorCode error code to return
     */
    private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode) {
        try {
            response.setStatus(errorCode.getHttpStatus());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            ErrorResponse errorResponse = new ErrorResponse(errorCode);
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(jsonResponse);
            
        } catch (OpenDidException | IOException e) {
            log.error("Failed to write error response", e);
        }
    }
}
