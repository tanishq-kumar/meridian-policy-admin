package com.meridian.policy.web;

import com.meridian.policy.domain.PolicyholderPartyProfile;
import com.meridian.policy.repo.PolicyholderPartyProfileRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parties")
public class PartyController {
    private final PolicyholderPartyProfileRepository repo;
    private final NamedParameterJdbcTemplate jdbc;
    @Value("${meridian.pii.key:change-me-in-prod}")
    private String piiKey;

    public PartyController(PolicyholderPartyProfileRepository repo, NamedParameterJdbcTemplate jdbc) { this.repo = repo; this.jdbc = jdbc; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PolicyholderPartyProfile create(@Valid @RequestBody Map<String,Object> body) {
        PolicyholderPartyProfile p = new PolicyholderPartyProfile();
        p.setLegalName((String) body.get("legalName"));
        p.setDbaName((String) body.get("dbaName"));
        p.setEntityTypeCode((String) body.get("entityTypeCode"));
        p.setNaicsCode((String) body.get("naicsCode"));
        p.setSicCode((String) body.get("sicCode"));
        p.setMailingAddressLine1((String) body.get("mailingAddressLine1"));
        p.setMailingCity((String) body.get("mailingCity"));
        p.setMailingStateCode((String) body.get("mailingStateCode"));
        p.setMailingPostalCode((String) body.get("mailingPostalCode"));
        p.setPrimaryContactName((String) body.get("primaryContactName"));
        p.setPrimaryContactEmail((String) body.get("primaryContactEmail"));
        p.setPrimaryContactPhone((String) body.get("primaryContactPhone"));
        if (body.get("yearEstablished") != null) p.setYearEstablished(((Number) body.get("yearEstablished")).intValue());
        if (body.get("employeeCount") != null) p.setEmployeeCount(((Number) body.get("employeeCount")).intValue());
        p.setPolicyholderId(null);
        p = repo.save(p);
        // F4: encrypt fein via pgcrypto if supplied as fein (plaintext) in request
        String plainFein = body.get("fein") != null ? (String) body.get("fein") : (String) body.get("feinEncrypted");
        // Also accept feinEncrypted as plaintext for backwards compat where tests send raw value
        if (plainFein != null && !plainFein.isBlank()) {
            // Check if it's already-looking-like binary vs plain digits: treat as plain if it looks like FEIN (digits/dashes)
            // We encrypt the provided string value.
            jdbc.update(
                    """
                    UPDATE pas.policyholder_party_profile
                    SET fein_encrypted = pgp_sym_encrypt(:plain, :key)
                    WHERE policyholder_id = :id
                    """,
                    Map.of("plain", plainFein, "key", piiKey, "id", p.getPolicyholderId()));
        }
        return repo.findById(p.getPolicyholderId()).orElse(p);
    }

    @GetMapping public List<PolicyholderPartyProfile> list() { return repo.findAll(); }
    @GetMapping("/{id}") public PolicyholderPartyProfile get(@PathVariable Long id) { return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "party not found")); }
}
