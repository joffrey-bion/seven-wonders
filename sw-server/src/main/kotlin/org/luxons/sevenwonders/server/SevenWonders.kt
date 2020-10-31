package org.luxons.sevenwonders.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SevenWonders

fun main(args: Array<String>) {
    runApplication<SevenWonders>(*args)
}
