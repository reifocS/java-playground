package com.reifocs.playground.util;

import com.reifocs.playground.models.Author;
import com.reifocs.playground.models.Book;
import com.reifocs.playground.repository.AuthorRepository;
import com.reifocs.playground.repository.BookRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Component
public class DataInitializer {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public DataInitializer(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @PostConstruct
    @Transactional
    public void initializeData() {
        var authors = IntStream.range(0, 1000).mapToObj(i -> {
            var author = new Author();
            author.setFullName("author_" + i);
            return author;
        }).toList();
        var authorSaved = authorRepository.saveAll(authors);
        var books = IntStream.range(0, 1000).mapToObj(i -> {
            var book = new Book();
            book.setTitle("book_" + i);
            book.addAuthor(authorSaved.get(i));
            return book;
        }).toList();
        var booksSaved = bookRepository.saveAll(books);
    }

}
