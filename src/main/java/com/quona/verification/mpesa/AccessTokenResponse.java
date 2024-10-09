package com.quona.verification.mpesa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenResponse {
    private String access_token;
    private String token_type;
    private String expires_in;
}