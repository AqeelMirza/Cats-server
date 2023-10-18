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

    @GetMapping(path = ["/allCats"])
    fun index(): List<Cat> = service.getAllCats()

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

    fun getAllCats(): List<Cat> {
        val fromDb = db.query("select * from cats") { rs, _ ->
            Cat(rs.getString("id"), rs.getString("name"), rs.getString("image"), rs.getString("description"))
        }

        return someDefaultCats() + fromDb
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

private fun someDefaultCats(): List<Cat> = listOf(
    Cat("1", "Whiskers", "https://example.com/cat1.jpg", "A cute fluffy cat."),
    Cat("2", "Mittens", "https://example.com/cat2.jpg", "Mittens has soft paws."),
    Cat("3", "Fluffy", "https://example.com/cat3.jpg", "Fluffy is the fluffiest cat."),
    Cat("4", "Snowball", "https://example.com/cat4.jpg", "Snowball loves to play in the snow."),
    Cat("5", "Socks", "https://example.com/cat5.jpg", "Socks has colorful socks."),
    Cat("6", "Oreo", "https://example.com/cat6.jpg", "Oreo has black and white fur."),
    Cat("7", "Tiger", "https://example.com/cat7.jpg", "Tiger is a fierce hunter."),
    Cat("8", "Simba", "https://example.com/cat8.jpg", "Simba is the king of the jungle."),
    Cat("9", "Garfield", "https://example.com/cat9.jpg", "Garfield loves lasagna."),
    Cat("10", "Felix", "https://example.com/cat10.jpg", "Felix is always getting into mischief.")
)