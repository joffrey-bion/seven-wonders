package org.luxons.sevenwonders.server.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport

@Configuration
class WebMvcConfig : WebMvcConfigurationSupport() {

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>?>) {
        converters.add(KotlinSerializationJsonHttpMessageConverter())
        addDefaultHttpMessageConverters(converters)
    }
}
