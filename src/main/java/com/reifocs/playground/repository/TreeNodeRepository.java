package com.reifocs.playground.repository;

import com.reifocs.playground.models.TreeNode;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TreeNodeRepository extends JpaRepository<TreeNode, Long> {

    @EntityGraph(value = "TreeNode.childrenRecursive", type = EntityGraph.EntityGraphType.LOAD)
    Optional<TreeNode> findById(long id);

    @Query("select distinct t from TreeNode t left join fetch t.children")
    List<TreeNode> findAll(long id);

    @Query(value = "WITH RECURSIVE TreeCTE AS (" +
            "  SELECT * FROM tree_node WHERE id = :parentId" +
            "  UNION ALL" +
            "  SELECT t.* FROM tree_node t JOIN TreeCTE c ON c.id = t.parent_id" +
            ")" +
            "SELECT DISTINCT t.* FROM TreeCTE t", nativeQuery = true)
    List<TreeNode> findTreeWithChildren(@Param("parentId") Long parentId);
}