package com.trustlend.api.payment;
import jakarta.validation.Valid;
import com.trustlend.api.api.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/loans/{loanId}/payments")
public class PaymentController {
    private final PaymentService service;
    public PaymentController(PaymentService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse record(@PathVariable UUID loanId, @Valid @RequestBody PaymentRequest request) {
        return PaymentResponse.from(service.record(loanId, request.amount(), request.idempotencyKey(), request.providerReference()));
    }

    @GetMapping
    public List<PaymentResponse> get(@PathVariable UUID loanId) {
        return service.getPayments(loanId).stream().map(PaymentResponse::from).toList();
    }
}
