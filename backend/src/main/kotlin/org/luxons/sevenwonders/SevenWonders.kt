package org.luxons.sevenwonders

import org.hildan.livedoc.spring.boot.starter.EnableJSONDoc
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableJSONDoc
class SevenWonders

fun main(args: Array<String>) {
    runApplication<SevenWonders>(*args)
}
