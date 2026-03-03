package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Singleton {
    private static final Singleton instance = new Singleton();

    private Singleton() {}

    ObjectWriter writer;
    ObjectMapper mapper;
    ArrayNode output;

    public void setOutput(ArrayNode output) {
        this.output = output;
    }

    public ArrayNode getOutput() {
        return output;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void setWriter(ObjectWriter writer) {
        this.writer = writer;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public ObjectWriter getWriter() {
        return writer;
    }

    public static Singleton getInstance() {
        return instance;
    }
}
