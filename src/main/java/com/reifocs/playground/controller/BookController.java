package com.reifocs.playground.controller;

import com.reifocs.playground.dto.BookDto;
import com.reifocs.playground.service.BookService;
import com.reifocs.playground.util.AsyncUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RequestMapping("/api/book")
@RestController
@Slf4j
public class BookController {
    private final BookService bookService;
    private final AsyncUtil asyncUtil;

    public BookController(BookService bookService, AsyncUtil asyncUtil) {
        this.bookService = bookService;
        this.asyncUtil = asyncUtil;
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> save(@RequestBody BookDto bookDto) {
        return asyncUtil.asyncMono(() -> {
            bookService.save(bookDto);
            return ResponseEntity.ok().build();
        });
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<BookDto>> findById(@PathVariable("id") Long id) {
        return asyncUtil.asyncMono(() -> {
            BookDto book = bookService.findById(id);
            return ResponseEntity.ok(book);
        });
    }

    @GetMapping()
    public ResponseEntity<List<BookDto>> findAll() {
        List<BookDto> books = bookService.findALl();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/all")
    public Mono<ResponseEntity<List<BookDto>>> saveAll() {
        return asyncUtil.asyncMono(() -> {
            List<BookDto> books = getBookDtos();
            var saved = bookService.saveAll(books);
            return ResponseEntity.ok(saved);
        });
    }

    @GetMapping("/allSync")
    public ResponseEntity<List<BookDto>> saveAllSync() {
        List<BookDto> books = getBookDtos();
        var saved = bookService.saveAllSync(books);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/allParallel")
    public ResponseEntity<List<BookDto>> saveAllParallel() {
        List<BookDto> books = getBookDtos();
        var saved = bookService.saveAllParallel(books);
        return ResponseEntity.ok(saved);
    }

    private static List<BookDto> getBookDtos() {
        return IntStream.range(0, 10_000).mapToObj(i -> {
            var newBook = new BookDto();
            newBook.setTitle(String.valueOf(i));
            return newBook;
        }).toList();
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") Long id) {
        return asyncUtil.asyncMono(() -> {
            Optional.ofNullable(bookService.findById(id)).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
            bookService.deleteById(id);
            return ResponseEntity.ok().build();
        });
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> update(@RequestBody BookDto bookDto, @PathVariable("id") Long id) {
        return asyncUtil.asyncMono(() -> {
            bookService.update(bookDto, id);
            return ResponseEntity.ok().build();
        });
    }
}