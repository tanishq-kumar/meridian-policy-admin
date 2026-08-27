package com.meridian.policy.web;

import com.meridian.policy.domain.PolicyEndorsementTransaction;
import com.meridian.policy.dto.CreateEndorsementRequest;
import com.meridian.policy.dto.CreatePolicyRequest;
import com.meridian.policy.dto.PolicyResponse;
import com.meridian.policy.service.PolicyAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/policies")
public class PolicyAdminController {

    private final PolicyAdminService service;

    public PolicyAdminController(PolicyAdminService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PolicyResponse create(@Valid @RequestBody CreatePolicyRequest req) {
        return service.createPolicy(req);
    }

    @GetMapping("/{policyNumber}")
    public PolicyResponse get(@PathVariable String policyNumber) {
        return service.getByNumber(policyNumber);
    }

    @PostMapping("/{policyNumber}/endorsements")
    @ResponseStatus(HttpStatus.CREATED)
    public PolicyEndorsementTransaction endorse(@PathVariable String policyNumber,
                                                @Valid @RequestBody CreateEndorsementRequest req) {
        return service.addEndorsement(policyNumber, req);
    }

    @PostMapping("/{policyNumber}/cancel")
    public PolicyResponse cancel(@PathVariable String policyNumber,
                                 @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reasonCode") : null;
        String date = body != null ? body.get("cancellationDate") : null;
        return service.cancelPolicy(policyNumber, date, reason);
    }

    @GetMapping("/health")
    public Map<String, String> health() { return Map.of("status", "UP", "service", "meridian-policy-admin"); }
}
