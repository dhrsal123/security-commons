# Security commons 

A shared library designed to provide standardized security configurations, including JWT handling and role-based access 
control for the Cinema ecosystem.

# Key features

- Standardized Security config: Pre-configured SecurityConfig to handle OAuth2 resource server settings.
- JWT Processing: Custom JwtConverted to map IdP claims to internal application roles.
- Centralized constants: Shared security related constants and property mappings.

# Prerequisites

- Java 21 (Amazon corretto)
- Docker & Docker compose

# Environment configuration

Create a .env file in the root directory based on the example below:

```text
# --- Keycloak ---
KEYCLOAK_HOST=localhost
```

# Usage 
### Role annotations

```java
@RestController
public class YourController {
    @HasManagerRole
    @GetMapping("/admin/data")
    public ResponseEntity<String> getSecureData() {
        ResponseEntity.ok("Your data here");
    }
}

```