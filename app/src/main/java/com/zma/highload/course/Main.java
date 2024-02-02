package com.zma.highload.course;

import com.zma.highload.course.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class Main implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int TARGET_STRING_LENGTH = 10;// letter 'z'
    private static final int RIGHT_LIMIT = 122;// letter 'a'
    private static final int LEFT_LIMIT = 97;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        Random random = new Random();
        List<Book> books = new ArrayList<>();
        int count = 1000000;
        int initialId = 0;
        for (int i = 0; i < count; i++) {
            books.add(createBook(random, initialId+i));
        }
        logger.info("Started insertion");
        long start = System.currentTimeMillis();

        for (Book book : books) {
            jdbcTemplate.update(
                    "INSERT INTO books VALUES (?, ?, ?, ?, ?)",
                    book.getId(), book.getCategoryId(), book.getAuthor(), book.getTitle(), book.getYear());
        }
        long time = System.currentTimeMillis() - start;

        logger.info("insertion of {} rows took {} ms", count, time);
    }

    private static Book createBook(Random random, long id) {
        String generatedString = random.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
                .limit(TARGET_STRING_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        Book book = new Book();
        book.setId(id);
        book.setAuthor(generatedString);
        book.setTitle(generatedString);
        book.setYear(random.nextInt(1900, 2000));

        book.setCategoryId(random.nextInt(10000) + 1);
        return book;
    }
}