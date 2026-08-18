package com.meridian.policy.web;

import com.meridian.policy.domain.PolicyholderPartyProfile;
import com.meridian.policy.repo.PolicyholderPartyProfileRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parties")
public class PartyController {
    private final PolicyholderPartyProfileRepository repo;
    public PartyController(PolicyholderPartyProfileRepository repo) { this.repo = repo; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PolicyholderPartyProfile create(@Valid @RequestBody PolicyholderPartyProfile p) {
        p.setPolicyholderId(null);
        return repo.save(p);
    }

    @GetMapping public List<PolicyholderPartyProfile> list() { return repo.findAll(); }
    @GetMapping("/{id}") public PolicyholderPartyProfile get(@PathVariable Long id) { return repo.findById(id).orElseThrow(); }
}
