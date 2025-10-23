package com.kaaj.api.controller;

import org.springframework.http.HttpStatus;

import com.kaaj.api.dto.PanelResponse;

public class ResponseEntity {

    public static ResponseEntity ok(PanelResponse response) {

        throw new UnsupportedOperationException("Unimplemented method 'ok'");
    }

    public static Object status(HttpStatus unauthorized) {

        throw new UnsupportedOperationException("Unimplemented method 'status'");
    }

}
