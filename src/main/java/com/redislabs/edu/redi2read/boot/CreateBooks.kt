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
import java.util.function.Function
import java.util.stream.Collectors

@Component
@Order(3)
@Slf4j
class CreateBooks : CommandLineRunner {
    @Autowired
    private var bookRepository: BookRepository? = null

    @Autowired
    private var categoryRepository: CategoryRepository? = null

    @Throws(Exception::class)
    override fun run(vararg args: String) {
        if (bookRepository!!.count() == 0L) {
            var mapper = ObjectMapper()

            var typeReference: TypeReference<List<Book?>?> = object : TypeReference<List<Book?>?>() {}

            var files =  //
                Files.list(Paths.get(javaClass.getResource("/data/books")!!.toURI())) //
                    .filter { path: Path? -> Files.isRegularFile(path) } //
                    .filter { path: Path -> path.toString().endsWith(".json") } //
                    .map(Path::toFile)//
                    .collect(Collectors.toList())

            var categories: MutableMap<String, Category?> = HashMap()

            files.forEach(Consumer { file: File ->
                try {
                    println(">>>> Processing Book File: " + file.path)

                    var categoryName = file.name.substring(0, file.name.lastIndexOf("_"))

                    println(">>>> Category: $categoryName")

                    var category: Category?

                    if (!categories.containsKey(categoryName)) {
                        category = Category()
                            category.name = categoryName
                        categoryRepository!!.save(category)
                        categories[categoryName] = category
                    } else {
                        category = categories[categoryName]
                    }

                    var inputStream: InputStream = FileInputStream(file)

                    var books = mapper.readValue(inputStream, typeReference)

                    books?.stream()?.forEach { book: Book? ->
                        if (category != null) {
                            book?.addCategory(category)
                        }
                        bookRepository!!.save(book)
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