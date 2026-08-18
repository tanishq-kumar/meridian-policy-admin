package com.meridian.policy.web;

import com.meridian.policy.domain.ProducerAgencyContract;
import com.meridian.policy.repo.ProducerAgencyContractRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/producers")
public class ProducerController {
    private final ProducerAgencyContractRepository repo;
    public ProducerController(ProducerAgencyContractRepository repo) { this.repo = repo; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProducerAgencyContract create(@Valid @RequestBody ProducerAgencyContract p) {
        p.setProducerId(null);
        return repo.save(p);
    }

    @GetMapping public List<ProducerAgencyContract> list() { return repo.findAll(); }
    @GetMapping("/{id}") public ProducerAgencyContract get(@PathVariable Long id) { return repo.findById(id).orElseThrow(); }
}
