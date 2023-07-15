package com.reifocs.playground.mapper;

import com.reifocs.playground.dto.BookDto;
import com.reifocs.playground.models.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class})
public interface BookMapper extends EntityMapper<BookDto, Book> {

}