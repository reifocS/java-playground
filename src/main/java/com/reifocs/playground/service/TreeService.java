package com.reifocs.playground.service;

import com.reifocs.playground.dto.TreeNodeDTO;
import com.reifocs.playground.mapper.TreeNodeMapper;
import com.reifocs.playground.models.TreeNode;
import com.reifocs.playground.repository.TreeNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TreeService {

    Logger logger = LoggerFactory.getLogger(TreeService.class);
    private final TreeNodeRepository treeNodeRepository;
    private final TreeNodeMapper treeNodeMapper;

    public TreeService(SeekFunction seekFunction, BeanFactory beanFactory, TreeNodeRepository treeNodeRepository, TreeNodeMapper treeNodeMapper) {

        this.treeNodeRepository = treeNodeRepository;
        this.treeNodeMapper = treeNodeMapper;
    }

    public void explorerGraphe(List<TreeNode> nodeList) {
        Set<TreeNode> nodes = new HashSet<>();
        for (TreeNode directedNode : nodeList) {
            if (!nodes.contains(directedNode)) {
                explorerSommet(directedNode, nodes);
            }
        }
    }

    public static List<TreeNode> explorerSommet(TreeNode sommet, Set<TreeNode> nodeSet) {
        nodeSet.add(sommet);
        List<TreeNode> directedNodes = new ArrayList<>();
        directedNodes.add(sommet);
        for (TreeNode voisin : sommet.getChildren()) {
            if (!nodeSet.contains(voisin)) {
                directedNodes.addAll(explorerSommet(voisin, nodeSet));
            }
        }
        return directedNodes;
    }

    @Transactional
    public String dfs(long id) {
        var tree = treeNodeRepository.findById(id).orElseThrow();
        explorerGraphe(List.of(tree));
        return "";
    }

    @Transactional
    public String dfsWithEfficientBatching(long id) {
        var tree = treeNodeRepository.findTreeWithChildren(id);
        explorerGraphe(tree);
        return "";
    }

    @Transactional
    public TreeNodeDTO findTreeNodeByIdWithCTE(long id) {
        var tree = treeNodeRepository.findTreeWithChildren(id);
        return treeNodeMapper.toDto(tree.parallelStream().filter(treeNode -> treeNode.getId().equals(id)).findAny().orElseThrow());
    }

    @Transactional
    public TreeNodeDTO findTreeNodeById(long id) {
        return treeNodeMapper.toDto(treeNodeRepository.findById(id).orElseThrow());
    }
}
