package org.omnione.did.example;

import org.omnione.did.base.config.ApiKeyRequired;
import org.omnione.did.base.db.constant.ApiKeyRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example controller demonstrating API key validation usage
 */
@RestController
@RequestMapping("/api/example")
public class ExampleController {
    
    // API without annotation - no validation performed
    @GetMapping("/public")
    public ResponseEntity<String> publicApi() {
        return ResponseEntity.ok("This is a public API");
    }
    
    // READ level API (allows TAS, ISSUER, READ roles)
    @GetMapping("/data")
    @ApiKeyRequired(role = ApiKeyRole.READ)
    public ResponseEntity<String> getData() {
        return ResponseEntity.ok("Data retrieved successfully");
    }
    
    // ISSUER level API (allows TAS, ISSUER roles)
    @PostMapping("/issue")
    @ApiKeyRequired(role = ApiKeyRole.ISSUER)
    public ResponseEntity<String> issueCredential() {
        return ResponseEntity.ok("Credential issued successfully");
    }
    
    // TAS level API (allows only TAS role)
    @PostMapping("/admin")
    @ApiKeyRequired(role = ApiKeyRole.TAS)
    public ResponseEntity<String> adminOperation() {
        return ResponseEntity.ok("TAS operation completed");
    }
}
