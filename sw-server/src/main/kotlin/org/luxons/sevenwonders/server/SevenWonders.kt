package org.luxons.sevenwonders.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter

@SpringBootApplication
class SevenWonders {

    @Bean
    fun additionalConverters(): HttpMessageConverters? {
        return HttpMessageConverters(KotlinSerializationJsonHttpMessageConverter())
    }
}

fun main(args: Array<String>) {
    runApplication<SevenWonders>(*args)
}
