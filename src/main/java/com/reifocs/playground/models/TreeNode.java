package com.reifocs.playground.models;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class TreeNode {
    // Other attributes and mappings

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "treenode_seq")
    @SequenceGenerator(name = "treenode_seq")
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private TreeNode parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @BatchSize(size = 100)
    private Set<TreeNode> children = new LinkedHashSet<>();

    // Getters and setters

    public void addChildren(TreeNode child) {
        children.add(child);
        child.setParent(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public Set<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(Set<TreeNode> children) {
        this.children = children;
    }
}