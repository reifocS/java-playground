package com.reifocs.playground.service;

import com.reifocs.playground.dto.BookDto;
import com.reifocs.playground.mapper.BookMapper;
import com.reifocs.playground.models.Book;
import com.reifocs.playground.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Slf4j
@Service
@Transactional
public class BookService {
    private final BookRepository repository;
    private final BookMapper bookMapper;

    public BookService(BookRepository repository, BookMapper bookMapper) {
        this.repository = repository;
        this.bookMapper = bookMapper;
    }

    public BookDto save(BookDto bookDto) {
        Book entity = bookMapper.toEntity(bookDto);
        return bookMapper.toDto(repository.save(entity));
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }


    public List<BookDto> saveAll(List<BookDto> bookDtos) {
        var saved = repository.saveAll(bookMapper.toEntity(bookDtos));
        return bookMapper.toDto(saved);
    }

    public BookDto findById(Long id) {
        var book = repository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        return bookMapper.toDto(book);
    }

    public Page<BookDto> findByCondition(BookDto bookDto, Pageable pageable) {
        Page<Book> entityPage = repository.findAll(pageable);
        List<Book> entities = entityPage.getContent();
        return new PageImpl<>(bookMapper.toDto(entities), pageable, entityPage.getTotalElements());
    }

    public BookDto update(BookDto bookDto, Long id) {
        BookDto data = findById(id);
        Book entity = bookMapper.toEntity(bookDto);
        BeanUtils.copyProperties(data, entity);
        return save(bookMapper.toDto(entity));
    }


    public List<BookDto> saveAllSync(List<BookDto> books) {
        var saved = books.stream().map(bookDto -> repository.save(bookMapper.toEntity(bookDto))).toList();
        return bookMapper.toDto(saved);
    }

    public List<BookDto> saveAllParallel(List<BookDto> books) {
        var saved = books.parallelStream().map(bookDto -> repository.save(bookMapper.toEntity(bookDto))).toList();
        return bookMapper.toDto(saved);
    }

    public List<BookDto> findALl() {
        return bookMapper.toDto(repository.findAll());
    }
}