package com.example.securegatewaybackendservice.dto;

// We'll use Lombok annotations later for brevity. For now, manual getters/setters.
// Make sure to add `private String username;` and `private String password;`

public class AuthRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Optional: Add a no-arg constructor if you explicitly define another
    public AuthRequest() {
    }

    // Optional: Add a constructor for convenience
    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}