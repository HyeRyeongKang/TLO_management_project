package com.example.httpservice;

public class ProblemData {
    private String id;
    private String name;
    private String level;

    public String get_id() {
        return id;
    }
    public void set_id(String id) {
        this.id=id;
    }

    public String get_name() {
        return name;
    }
    public void set_name(String name) {
        this.name=name;
    }

    public String get_level() {
        return level;
    }
    public void set_level(String level) {
        this.level=level;
    }
}
