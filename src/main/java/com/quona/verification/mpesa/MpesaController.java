package com.quona.verification.mpesa;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mpesa")
@RequiredArgsConstructor
public class MpesaController {
    private final MpesaService mpesaService;

    @PostMapping("/validate")
    public ResponseEntity<String> test(@RequestParam String partyB, @RequestParam String idNumber) {
        return mpesaService.validatePayment(partyB, idNumber);
    }
}
