package com.quona.verification.mpesa;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class MpesaService {

    @Value("${mpesa.sandbox.url}")
    public String sandboxUrl;

    @Value("${mpesa.initiator.name}")
    private String initiatorName;

    @Value("${mpesa.security.credential}")
    private String credential;

    @Value("${mpesa.security.consumerSecret}")
    private String consumerSecret;

    @Value("${mpesa.security.consumerKey}")
    private String consumerKey;

    @Value("${mpesa.sandbox.accessTokenUrl}")
    private String accessTokenUrl;

    private final RestTemplate restTemplate;

    public String generateAccessToken() {
        String credentials = consumerKey + ":" + consumerSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedCredentials);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(accessTokenUrl, HttpMethod.GET, request, String.class);

        ResponseEntity<AccessTokenResponse> response2 = restTemplate.exchange(
                accessTokenUrl,
                HttpMethod.GET,
                request,
                AccessTokenResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            AccessTokenResponse accessTokenResponse = response2.getBody();
            assert accessTokenResponse != null;
            String accessToken = accessTokenResponse.getAccess_token();
            return accessToken;
        } else {
            return null;
        }
    }

    public ResponseEntity<String> validatePayment(String partyB, String idNumber) {
        String accessToken = generateAccessToken();
        System.out.println("Access token: " + accessToken);
        System.out.println(partyB);

        String requestBody = String.format("{\"InitiatorName\": \"%s\", \"OriginatorConversationID\": \"e0391v70-5c3c-4dc6-92c2-73b183bbb40a\", \"SecurityCredential\": \"%s\", \"CommandID\": \"BusinessPayment\", \"Amount\": \"10\", \"PartyA\": \"600996\", \"PartyB\": \"%s\", \"IDType\": \"01\", \"IDNumber\": \"%s\", \"Remarks\": \"None\", \"QueueTimeOutURL\": \"https://safaricomsacco.com\", \"ResultURL\": \"https://safaricomsacco.com\",  \"Occasion\": \"\"}",
                initiatorName, credential, partyB, idNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        System.out.println(request);
        try {
            ResponseEntity<String> response = restTemplate.exchange(sandboxUrl, HttpMethod.POST, request, String.class);
            return response;
        } catch (HttpClientErrorException e) {
            // Log error details
            System.out.println("Error Status Code: " + e.getStatusCode());
            System.out.println("Error Response Body: " + e.getResponseBodyAsString());
            throw e;
        }
    }
}
