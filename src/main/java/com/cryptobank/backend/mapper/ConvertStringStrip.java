package com.cryptobank.backend.mapper;

import org.springframework.stereotype.Component;

@Component
public class ConvertStringStrip {

    public String asString(String string) {
        return string == null ? null : string.strip();
    }

}
