package com.springdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.util.*

@SpringBootApplication
class SpringDemoApplication

fun main(args: Array<String>) {
    runApplication<SpringDemoApplication>(*args)
}

@RestController
class CatResource(val service: CatService) {

    @GetMapping
    fun index(): List<Cat> = service.findCats()

    @GetMapping("/{id}")
    fun index(@PathVariable id: String): List<Cat> = service.findCatsById(id)


    @PostMapping
    fun post(@RequestBody cat: Cat) {
        service.post(cat)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) = service.deleteCatById(id)
}

@Service
class CatService(val db: JdbcTemplate) {

    fun findCats(): List<Cat> = db.query("select * from cats") { rs, _ ->
        Cat(rs.getString("id"), rs.getString("name"), rs.getString("image"), rs.getString("description"))
    }

    fun findCatsById(id: String): List<Cat> = db.query("select * from cats where id = ?", id) { rs, _ ->
        Cat(rs.getString("id"), rs.getString("name"), rs.getString("image"), rs.getString("description"))
    }

    fun post(cat: Cat) {
        db.update(
            "insert into cats values (?, ?, ?, ? )", cat.id ?: cat.name.uuid(), cat.name, cat.image, cat.description
        )
    }

    fun deleteCatById(id: String) {
        db.update("DELETE FROM cats WHERE id = ?", id)
    }
}

data class Cat(val id: String?, val name: String, val image: String, val description: String)

fun String.uuid(): String = UUID.nameUUIDFromBytes(this.encodeToByteArray()).toString()