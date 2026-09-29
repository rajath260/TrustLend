package com.trustlend.api.repaymentrecord;

import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{userId}/repayment-record")
public class RepaymentRecordController {
    private final RepaymentRecordService service;
    public RepaymentRecordController(RepaymentRecordService service) { this.service = service; }

    @GetMapping
    public RepaymentRecord get(@PathVariable UUID userId) {
        return service.get(userId);
    }
}
