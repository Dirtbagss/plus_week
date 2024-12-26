package com.example.demo.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordEncoderTests {

    @Test
    void testEncodePassword() {
        String rawPassword = "test123!@";
        String encodedPassword = PasswordEncoder.encode(rawPassword);

        assertNotNull(encodedPassword, "암호화된 비밀번호는 null이 될 수 없습니다.");
        assertNotEquals(rawPassword, encodedPassword, "암호화된 비밀번호는 평문 비밀번호와 같을 수 없습니다.");
    }

    @Test
    void testEncodedPasswordMatchesRawPassword() {
        String rawPassword = "sdjgijstj!#$@3253";
        String encodedPassword = PasswordEncoder.encode(rawPassword);

        assertTrue(PasswordEncoder.matches(rawPassword, encodedPassword),
                "평문 비밀번호는 암호화된 비밀번호와 matches로 비교했을 때 True를 리턴해야 합니다.");
    }

    @Test
    void testEncodedPasswordsAreDifferent() {
        String rawPassword = "sdjgijs_-~tj!#$@3253";
        String encodedPassword1 = PasswordEncoder.encode(rawPassword);
        String encodedPassword2 = PasswordEncoder.encode(rawPassword);

        assertNotEquals(encodedPassword1, encodedPassword2, "암호화된 비밀번호는 서로 달라야 합니다.");
    }

    @Test
    void testPasswordDoesNotMatch() {
        String rawPassword = "asdfzxcv09832@";
        String wrongPassword = "SkDoClGm3425";

        String encodedPassword = PasswordEncoder.encode(rawPassword);

        assertFalse(PasswordEncoder.matches(wrongPassword, encodedPassword),
                "wrongPassword는 encodedPassword와 matches로 비교했을 때 False를 리턴해야 합니다.");
    }
}
