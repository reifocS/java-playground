package com.reifocs.playground.mapper;

import com.reifocs.playground.dto.TreeNodeDTO;
import com.reifocs.playground.models.TreeNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TreeNodeMapper extends EntityMapper<TreeNodeDTO, TreeNode> {

    @Override
    @Mapping(target = "parentId", source = "parent.id")
    TreeNodeDTO toDto(TreeNode entity);

    @Override
    @Mapping(source = "parentId", target = "parent.id")
    TreeNode toEntity(TreeNodeDTO dto);
}
