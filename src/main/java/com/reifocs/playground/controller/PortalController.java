package com.reifocs.playground.controller;

import com.reifocs.playground.service.PortalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/portal")
public class PortalController {

    private final PortalService portalService;

    public PortalController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/seek/{id}")
    public String seekBook(@PathVariable int id) {
        // Define a list of library server URLs
        // Iterate over the library server URLs and perform GET requests
        Optional<String> dest = portalService.getBook(id);
        // Book not found in any library
        assert dest.isPresent();
        return dest.get();
    }

    @PostMapping("/algo/{algo}")
    public ResponseEntity<String> seekBook(@PathVariable String algo) {
        return ResponseEntity.ok(this.portalService.switchAlgorithm(algo));
    }
}
