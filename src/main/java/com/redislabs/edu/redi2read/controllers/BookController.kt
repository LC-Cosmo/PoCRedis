package com.redislabs.edu.redi2read.controllers

import com.redislabs.edu.redi2read.models.Book
import com.redislabs.edu.redi2read.models.Category
import com.redislabs.edu.redi2read.repositories.BookRepository
import com.redislabs.edu.redi2read.repositories.CategoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Suppress("UNCHECKED_CAST")
@RestController
@RequestMapping("/api/books")
class BookController {
    @Autowired
    private var bookRepository: BookRepository? = null

    @Autowired
    private var categoryRepository: CategoryRepository? = null
    @GetMapping
    fun all(
        @RequestParam(defaultValue = "0") page: Int?,
        @RequestParam(defaultValue = "10") size: Int?
    ): ResponseEntity<Map<String, Any>> {
        var paging: Pageable = PageRequest.of(page!!, size!!)
        var pagedResult = bookRepository!!.findAll(paging)
        var books = if (pagedResult.hasContent()) pagedResult.content else emptyList()
        var response: MutableMap<String, Any> = HashMap()
        response["books"] = books
        response["page"] = pagedResult.number
        response["pages"] = pagedResult.totalPages
        response["total"] = pagedResult.totalElements
        return ResponseEntity(response, HttpHeaders(), HttpStatus.OK)
    }

    @get:GetMapping("/categories")
    val categories: Iterable<Category>
        get() = categoryRepository!!.findAll() as Iterable<Category>

    @GetMapping("/{isbn}")
    operator fun get(@PathVariable("isbn") isbn: String): Book {
        return bookRepository!!.findById(isbn).get()
    }
}