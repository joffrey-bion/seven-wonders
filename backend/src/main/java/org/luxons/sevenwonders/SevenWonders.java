package org.luxons.sevenwonders;

import org.hildan.livedoc.spring.boot.starter.EnableJSONDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableJSONDoc
public class SevenWonders {

    public static void main(String[] args) {
        SpringApplication.run(SevenWonders.class, args);
    }
}
