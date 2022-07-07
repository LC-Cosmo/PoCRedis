package com.redislabs.edu.redi2read.boot

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.redislabs.edu.redi2read.models.*
import com.redislabs.edu.redi2read.repositories.BookRepository
import com.redislabs.edu.redi2read.repositories.CategoryRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Consumer
import java.util.stream.Collectors

@Component
@Order(3)
@Slf4j
class CreateBooks : CommandLineRunner {
    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Throws(Exception::class)
    override fun run(vararg args: String) {
        if (bookRepository.count() == 0L) {
            val mapper = ObjectMapper()

            val typeReference: TypeReference<List<Book?>?> = object : TypeReference<List<Book?>?>() {}

            val files =  //
                Files.list(Paths.get(javaClass.getResource("/data/books")!!.toURI())) //
                    .filter { path: Path? -> Files.isRegularFile(path!!) } //
                    .filter { path: Path -> path.toString().endsWith(".json") } //
                    .map(Path::toFile)//
                    .collect(Collectors.toList())

            val categories: MutableMap<String, Category?> = HashMap()

            files.forEach(Consumer { file: File ->
                try {
                    println(">>>> Processing Book File: " + file.path)

                    val categoryName = file.name.substring(0, file.name.lastIndexOf("_"))

                    println(">>>> Category: $categoryName")

                    val category: Category?

                    if (!categories.containsKey(categoryName)) {
                        category = Category()
                            category.name = categoryName
                        categoryRepository.save(category)
                        categories[categoryName] = category
                    } else {
                        category = categories[categoryName]
                        println(category)
                    }

                    val inputStream: InputStream = FileInputStream(file)

                    val books : List<Book> = mapper.readValue(inputStream, typeReference) as List<Book>

                    books?.stream()?.forEach { book: Book? ->
                        book?.addCategory(category!!)
                        println(book!!)
                        bookRepository.save(book!!)
                    }

                    if (books != null) {
                        println((">>>> " + books.size + " Books Saved!"))
                    }
                } catch (e: IOException) {
                    println("Unable to import books: " + e.message)
                }
            })
            println(">>>> Loaded Book Data and Created books...")
        }
    }
}