package com.closecircuit.strava.controller;

import com.closecircuit.strava.config.StravaProperties;
import com.closecircuit.strava.service.StravaTokenService;
import com.closecircuit.strava.util.TokenPersistenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class StravaOAuthController {

    private static final Logger log = LoggerFactory.getLogger(StravaOAuthController.class);

    private final StravaProperties properties;
    private final StravaTokenService tokenService;
    private final TokenPersistenceUtil tokenPersistence;

    public StravaOAuthController(StravaProperties properties, 
                                  StravaTokenService tokenService,
                                  TokenPersistenceUtil tokenPersistence) {
        this.properties = properties;
        this.tokenService = tokenService;
        this.tokenPersistence = tokenPersistence;
    }

    /**
     * Step 1: Initiate OAuth flow - redirects user to Strava authorization page
     * GET /oauth/authorize
     */
    @GetMapping("/authorize")
    public RedirectView authorize() {
        String redirectUri = properties.getRedirectUri() != null 
            ? properties.getRedirectUri() 
            : "http://localhost:8080/oauth/callback";
            
        String authUrl = String.format(
            "https://www.strava.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=activity:read_all",
            properties.getClientId(),
            redirectUri
        );
        log.info("Redirecting to Strava authorization: {}", authUrl);
        return new RedirectView(authUrl);
    }

    /**
     * Step 2: Handle OAuth callback from Strava
     * GET /oauth/callback?code=...
     */
    @GetMapping(value = "/callback", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> callback(@RequestParam(required = false) String code,
                                           @RequestParam(required = false) String error) {
        
        if (error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(generateErrorHtml("Authorization Error", 
                    "Authorization was denied or failed: " + error));
        }

        if (code == null || code.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(generateErrorHtml("Missing Code", 
                    "Authorization code is missing from the callback."));
        }

        try {
            log.info("Exchanging authorization code for tokens...");
            // Exchange code for tokens
            Map<String, Object> tokenResponse = tokenService.exchangeAuthorizationCode(code);
            
            if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generateErrorHtml("Token Exchange Failed", 
                        "Failed to retrieve tokens from Strava. Please try again."));
            }

            String accessToken = (String) tokenResponse.get("access_token");
            String refreshToken = (String) tokenResponse.get("refresh_token");
            long expiresAt = ((Number) tokenResponse.get("expires_at")).longValue();

            // Try to persist tokens to application.properties
            boolean persisted = tokenPersistence.saveTokens(accessToken, refreshToken, expiresAt);
            
            String html = generateSuccessHtml(accessToken, refreshToken, expiresAt, persisted);
            return ResponseEntity.ok(html);
            
        } catch (Exception e) {
            log.error("Error exchanging authorization code", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(generateErrorHtml("Token Exchange Error", 
                    "Failed to exchange code for tokens: " + e.getMessage()));
        }
    }

    private String generateSuccessHtml(String accessToken, String refreshToken, long expiresAt, boolean persisted) {
        String expiresDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            .format(new java.util.Date(expiresAt * 1000));
            
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>Strava OAuth - Success</title>\n" +
            "    <style>\n" +
            "        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; background: #f5f5f5; }\n" +
            "        .container { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n" +
            "        h1 { color: #FC4C02; margin-top: 0; }\n" +
            "        .success { color: #28a745; font-weight: bold; margin: 20px 0; }\n" +
            "        .token-box { background: #f8f9fa; padding: 15px; border-radius: 5px; margin: 10px 0; border-left: 4px solid #FC4C02; }\n" +
            "        .token-label { font-weight: bold; color: #666; font-size: 0.9em; }\n" +
            "        .token-value { font-family: 'Courier New', monospace; color: #333; word-break: break-all; margin-top: 5px; }\n" +
            "        .copy-btn { background: #FC4C02; color: white; border: none; padding: 8px 15px; border-radius: 5px; cursor: pointer; margin-left: 10px; }\n" +
            "        .copy-btn:hover { background: #d93d00; }\n" +
            "        .info { background: #e7f3ff; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #2196F3; }\n" +
            "        .warning { background: #fff3cd; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #ffc107; }\n" +
            "        code { background: #f4f4f4; padding: 2px 6px; border-radius: 3px; font-family: 'Courier New', monospace; }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <h1>✅ Strava OAuth - Success!</h1>\n" +
            "        <div class=\"success\">Tokens retrieved successfully!</div>\n" +
            (persisted ? 
                "<div class=\"info\">✅ <strong>Tokens have been automatically saved to application.properties</strong></div>\n" :
                "<div class=\"warning\">⚠️ <strong>Could not automatically save tokens.</strong> Please manually update application.properties with the values below.</div>\n") +
            "        <div class=\"token-box\">\n" +
            "            <div class=\"token-label\">Access Token:</div>\n" +
            "            <div class=\"token-value\" id=\"accessToken\">" + accessToken + "</div>\n" +
            "            <button class=\"copy-btn\" onclick=\"copyToClipboard('accessToken')\">Copy</button>\n" +
            "        </div>\n" +
            "        <div class=\"token-box\">\n" +
            "            <div class=\"token-label\">Refresh Token:</div>\n" +
            "            <div class=\"token-value\" id=\"refreshToken\">" + refreshToken + "</div>\n" +
            "            <button class=\"copy-btn\" onclick=\"copyToClipboard('refreshToken')\">Copy</button>\n" +
            "        </div>\n" +
            "        <div class=\"token-box\">\n" +
            "            <div class=\"token-label\">Expires At (Unix timestamp):</div>\n" +
            "            <div class=\"token-value\" id=\"expiresAt\">" + expiresAt + "</div>\n" +
            "            <button class=\"copy-btn\" onclick=\"copyToClipboard('expiresAt')\">Copy</button>\n" +
            "            <div style=\"margin-top: 5px; font-size: 0.9em; color: #666;\">(" + expiresDate + ")</div>\n" +
            "        </div>\n" +
            (!persisted ? 
                "<div class=\"info\">\n" +
                "            <strong>To update application.properties manually:</strong><br>\n" +
                "            Update these lines in <code>src/main/resources/application.properties</code>:<br><br>\n" +
                "            <code>strava.auth.access-token=" + accessToken + "</code><br>\n" +
                "            <code>strava.auth.refresh-token=" + refreshToken + "</code><br>\n" +
                "            <code>strava.auth.expires-at=" + expiresAt + "</code>\n" +
                "        </div>\n" : "") +
            "        <div class=\"info\" style=\"margin-top: 30px;\">\n" +
            "            <strong>Next Steps:</strong><br>\n" +
            "            1. Restart your Spring Boot application if tokens were saved automatically<br>\n" +
            "            2. The application will automatically refresh tokens when they expire<br>\n" +
            "            3. You can now use the Strava API endpoints\n" +
            "        </div>\n" +
            "    </div>\n" +
            "    <script>\n" +
            "        function copyToClipboard(elementId) {\n" +
            "            const element = document.getElementById(elementId);\n" +
            "            const text = element.textContent;\n" +
            "            navigator.clipboard.writeText(text).then(() => {\n" +
            "                alert('Copied to clipboard!');\n" +
            "            }).catch(err => {\n" +
            "                console.error('Failed to copy:', err);\n" +
            "            });\n" +
            "        }\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>";
    }

    private String generateErrorHtml(String title, String message) {
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>Strava OAuth - Error</title>\n" +
            "    <style>\n" +
            "        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; background: #f5f5f5; }\n" +
            "        .container { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n" +
            "        h1 { color: #dc3545; margin-top: 0; }\n" +
            "        .error { background: #f8d7da; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #dc3545; color: #721c24; }\n" +
            "        a { color: #FC4C02; text-decoration: none; }\n" +
            "        a:hover { text-decoration: underline; }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <h1>❌ Strava OAuth - Error</h1>\n" +
            "        <div class=\"error\">\n" +
            "            <strong>" + title + "</strong><br>\n" +
            "            " + message + "\n" +
            "        </div>\n" +
            "        <p><a href=\"/oauth/authorize\">Try again</a></p>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
    }
}
