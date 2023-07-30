package com.reifocs.playground.util;

import com.reifocs.playground.models.Author;
import com.reifocs.playground.models.Book;
import com.reifocs.playground.models.TreeNode;
import com.reifocs.playground.repository.AuthorRepository;
import com.reifocs.playground.repository.BookRepository;
import com.reifocs.playground.repository.TreeNodeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
public class DataInitializer {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final TreeNodeRepository treeNodeRepository;

    public DataInitializer(AuthorRepository authorRepository, BookRepository bookRepository, TreeNodeRepository treeNodeRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.treeNodeRepository = treeNodeRepository;
    }

    @PostConstruct
    @Transactional
    public void initializeData() {
        //createBooks();

        createTree();
    }

    private void createBooks() {
        var authors = IntStream.range(0, 1000).mapToObj(i -> {
            var author = new Author();
            author.setFullName("author_" + i);
            return author;
        }).toList();
        var authorSaved = authorRepository.saveAll(authors);
        var books = IntStream.range(0, 1000).mapToObj(i -> {
            var book = new Book();
            book.setTitle("book_" + i);
            return book;
        }).toList();
        var booksSaved = bookRepository.saveAll(books);

        for (Book book : booksSaved) {
            book.addAuthor(getRandom(authorSaved));
            book.addAuthor(getRandom(authorSaved));
            book.addAuthor(getRandom(authorSaved));
        }

        bookRepository.saveAll(booksSaved);
    }

    private void createTree() {
        TreeNode root = new TreeNode();
        for (int i = 0; i < 5; ++i) {
            createChildren(root, 100);
        }
        treeNodeRepository.save(root);

    }

    public void createChildren(TreeNode node, int depth) {
        if (depth == 0) {
            return;
        }
        var newNode = new TreeNode();
        node.addChildren(newNode);
        createChildren(newNode, depth - 1);
    }

    public static <T> T getRandom(List<T> array) {
        int rnd = new Random().nextInt(array.size());
        return array.get(rnd);
    }
}
