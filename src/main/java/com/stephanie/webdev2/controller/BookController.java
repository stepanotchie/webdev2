package com.stephanie.webdev2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.stephanie.webdev2.model.Book;

@Controller
@RequestMapping("/books") // every method here starts with /books
public class BookController {

    private final List<Book> books = new ArrayList<>(); // just storing books in a list, no db
    private final AtomicLong idCounter = new AtomicLong(); // gives each book a unique id

    public BookController() {
        // adding some sample books so the list isn't empty
        books.add(new Book(idCounter.incrementAndGet(), "Harry Potter", "Rowling"));
        books.add(new Book(idCounter.incrementAndGet(), "Never Grow Old", "Anchola"));
        books.add(new Book(idCounter.incrementAndGet(), "1984", "George"));
        books.add(new Book(idCounter.incrementAndGet(), "The Hobbit", "Tolkien"));

    }

    // task 1 / task 2
    @GetMapping // handles GET /books
    @ResponseBody // sends the result back as json
    public List<Book> listBooks(@RequestParam(required = false) String author) {
        if (author == null || author.isBlank()) { // if no author was typed in
            return books; // just give back all the books
        }
        return books.stream()
                .filter(b -> b.getAuthor().equalsIgnoreCase(author)) // only keep books by that author
                .toList(); // turn it into a list
    }

    // task 3
    @GetMapping("/{id}/view") // handles GET /books/{id}/view
    public String getBookDetailView(@PathVariable Long id, Model model) {
        Book found = findById(id).orElse(null); // find the book, or null if not there
        model.addAttribute("book", found); // send the book to the view as "book"
        return "book-detail"; // name of the page to show (no actual page yet)
    }

    // task 1 / task 5
    @GetMapping("/{id}") // handles GET /books/{id}
    @ResponseBody
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> found = findById(id); // try to find the book
        return found
                .map(ResponseEntity::ok) // found it, send 200 with the book
                .orElseGet(() -> ResponseEntity.notFound().build()); // not found, send 404
    }

    // task 4 / task 5
    @PostMapping // handles POST /books
    @ResponseStatus(HttpStatus.CREATED) // makes the response status 201
    public String createBook(@RequestParam String title, @RequestParam String author) {
        Book newBook = new Book(idCounter.incrementAndGet(), title, author); // make the new book
        books.add(newBook); // add it to the list
        return "redirect:/books"; // send the user back to the books list after saving
    }

    // used by both the view and the detail endpoint to find a book
    private Optional<Book> findById(Long id) {
        return books.stream().filter(b -> b.getId().equals(id)).findFirst(); // look for matching id
    }
}