package com.cryptobank.backend.utils;

public class IdGenerator {

    public static String generate() {
        return Xid.get().toString();
    }

}
