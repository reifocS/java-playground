package com.reifocs.playground.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/portal")
public class PortalController {

    @GetMapping("/seek/{id}")
    public String seekBook(@PathVariable int id) {
        // Define a list of library server URLs
        List<String> libraryUrls = IntStream.range(0, 10)
                .mapToObj(i -> "http://localhost:%d/books/".formatted(3000 + i))
                .toList();
        // Iterate over the library server URLs and perform GET requests
        for (String libraryUrl : libraryUrls) {
            String url = libraryUrl + id;
            try {

                ResponseEntity<String> response = new RestTemplate().getForEntity(url, String.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    // Book found in the current library server
                    return "Book found in Library %s%d".formatted(libraryUrl, id);
                }
            } catch (HttpClientErrorException notFound) {
                System.out.println("Book not found in Library");
            }
        }

        // Book not found in any library
        return "Book not found";
    }
}
