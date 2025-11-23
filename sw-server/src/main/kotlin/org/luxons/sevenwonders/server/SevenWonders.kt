package org.luxons.sevenwonders.server

import io.micrometer.core.instrument.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.micrometer.metrics.autoconfigure.*
import org.springframework.context.annotation.*
import org.springframework.http.converter.json.*
import java.net.*

@SpringBootApplication
class SevenWonders {

    @Bean
    fun additionalConverters() = KotlinSerializationJsonHttpMessageConverter()

    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry>? = MeterRegistryCustomizer { registry ->
        registry.config()
            .commonTags("application", "seven-wonders")
            .commonTags("instance", findInstanceName())
    }

    private fun findInstanceName(): String {
        val hostname = System.getenv("HOSTNAME")?.takeIf { it.isNotBlank() }
        if (hostname != null) {
            return hostname
        }
        val computerName = System.getenv("COMPUTERNAME")?.takeIf { it.isNotBlank() }
        if (computerName != null) {
            return computerName
        }
        return InetAddress.getLocalHost().hostName
    }
}

fun main(args: Array<String>) {
    runApplication<SevenWonders>(*args)
}
