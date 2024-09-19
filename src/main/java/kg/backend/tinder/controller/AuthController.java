package kg.backend.tinder.controller;


import io.swagger.v3.oas.annotations.Operation;
import kg.backend.tinder.dto.AuthRequest;
import kg.backend.tinder.dto.AuthResponse;
import kg.backend.tinder.service.security.IdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final IdentityService identityService;

    // ------------------------------
    @PostMapping("/register")
    // ------------------------------
    @Operation(summary = "User registration", description = "Login user  (Get JWT token)")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(identityService.registerUser(request));
    }

    // ------------------------------
    @PostMapping("/login")
    // ------------------------------
    @Operation(summary = "User authentication", description = "Login user  (Get JWT token)")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(identityService.login(authRequest));
    }

    // ------------------------------
    @PostMapping("/logout")
    // ------------------------------
    @Operation(summary = "User logout", description = "Logout user")
    public ResponseEntity<AuthResponse> logout(@RequestParam String token) {
        return ResponseEntity.ok(identityService.logout(token));
    }

    // ------------------------------
    @GetMapping("/refresh-token")
    @Operation(summary = "Refresh   token", description = "Get access token by refresh token")
    // ------------------------------
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeaderValue) {

        if (authHeaderValue != null && authHeaderValue.startsWith("Bearer")) {
            return ResponseEntity.ok(identityService.refreshToken(authHeaderValue.substring(7)));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(null);
    }

    @PostMapping("/reset-password")
    public void sendResetPasswordLink(@RequestParam String email) {
        identityService.sendResetPasswordLink(email);
    }

    @PutMapping("/save-password")
    public void savePassword(@RequestParam String token, @RequestParam String password) {
        identityService.saveNewPassword(token, password);
    }

    @PostMapping("/confirm-email")
    public void sendConfirmationCode(@RequestParam String email) {
        identityService.sendConfirmationCode(email);
    }

    @PutMapping("/confirm-email")
    public void confirmEmail(@RequestParam long userId, @RequestParam String code) {
        identityService.confirmEmail(userId, code);
    }


}
