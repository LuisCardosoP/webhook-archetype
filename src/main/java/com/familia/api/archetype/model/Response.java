package com.familia.api.archetype.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private int status;
    private String userMessage;
    private String developerMessage;
    private String errorCode;
    private T data;
}
