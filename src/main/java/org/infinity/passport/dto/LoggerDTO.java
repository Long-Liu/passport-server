package org.infinity.passport.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;

import ch.qos.logback.classic.Logger;

public class LoggerDTO implements Serializable {

    private static final long serialVersionUID = -5699573384509642057L;

    private String            name;

    private String            level;

    public LoggerDTO(Logger logger) {
        this.name = logger.getName();
        this.level = logger.getEffectiveLevel().toString();
    }

    @JsonCreator
    public LoggerDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "LoggerDTO [name=" + name + ", level=" + level + "]";
    }
}
