package com.reifocs.playground.mapper;

import com.reifocs.playground.dto.AuthorDto;
import com.reifocs.playground.models.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper extends EntityMapper<AuthorDto, Author> {
}
