package com.reifocs.playground.dto;

import java.util.Set;

public class BookDto {
    private Long id;
    private String title;

    private Set<AuthorDto> authors;

    public BookDto() {
    }

    public Set<AuthorDto> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<AuthorDto> authors) {
        this.authors = authors;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}