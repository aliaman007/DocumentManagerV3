package com.main.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;

class UserTests {

    @Test
    void testUserGettersAndSetters() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedpassword");
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        user.setRoles(roles);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedpassword", user.getPassword());
        assertTrue(user.getRoles().contains("USER"));
    }
}