package com.example.demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloControllerTest {

    @Test
    public void testHello() {

        HelloController controller = new HelloController();

        assertEquals("Hello from Spring Boot",
                controller.hello());

    }

}
