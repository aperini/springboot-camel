package com.springbootcamel.model;

public class Hello {

    private Integer id;
    private String name;

    public Hello() {
        // Required by jackson
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
