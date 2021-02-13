package org.luxons.sevenwonders.server

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import java.net.InetAddress

@SpringBootApplication
class SevenWonders {

    @Bean
    fun additionalConverters(): HttpMessageConverters? {
        return HttpMessageConverters(KotlinSerializationJsonHttpMessageConverter())
    }

    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry>? = MeterRegistryCustomizer { registry ->
        registry.config()
            .commonTags("application", "seven-wonders")
            .commonTags("instance", InetAddress.getLocalHost().hostAddress)
    }
}

fun main(args: Array<String>) {
    runApplication<SevenWonders>(*args)
}
