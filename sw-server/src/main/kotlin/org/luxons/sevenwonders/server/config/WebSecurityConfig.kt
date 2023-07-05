package org.luxons.sevenwonders.server.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class WebSecurityConfig {

    // this disables default authentication settings
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain? = http.cors {}.csrf { it.disable() }.build()
}
