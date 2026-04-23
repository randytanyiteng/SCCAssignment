package com.favorites.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("URLValidator Tests")
class URLValidatorTest {

    @BeforeEach
    void setUp() {
        // Setup if needed
    }

    // ==================== isValidUrl() Tests ====================

    @Test
    @DisplayName("Valid URLs should return true")
    void testValidUrls() {
        assertTrue(URLValidator.isValidUrl("https://www.google.com"));
        assertTrue(URLValidator.isValidUrl("http://example.com"));
        assertTrue(URLValidator.isValidUrl("ftp://files.example.com"));
        assertTrue(URLValidator.isValidUrl("https://example.com/path/to/page"));
        assertTrue(URLValidator.isValidUrl("https://sub.domain.example.com"));
        assertTrue(URLValidator.isValidUrl("https://example.com/path?query=value"));
        assertTrue(URLValidator.isValidUrl("https://example.com:8080/path"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "https://example.com",
        "http://localhost.example.com",
        "https://test-domain.co.uk",
        "ftp://ftp.example.org"
    })
    @DisplayName("Various valid URL formats")
    void testValidUrlFormats(String url) {
        assertTrue(URLValidator.isValidUrl(url));
    }

    @Test
    @DisplayName("Null URL should return false")
    void testNullUrl() {
        assertFalse(URLValidator.isValidUrl(null));
    }

    @Test
    @DisplayName("Empty URL should return false")
    void testEmptyUrl() {
        assertFalse(URLValidator.isValidUrl(""));
    }

    @Test
    @DisplayName("Whitespace-only URL should return false")
    void testWhitespaceOnlyUrl() {
        assertFalse(URLValidator.isValidUrl("   "));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "not-a-url",
        "example.com",
        "www.example.com",
        "htp://example.com",  // typo in protocol
        "https://",
        "ftp://"
    })
    @DisplayName("Invalid URL formats")
    void testInvalidUrlFormats(String url) {
        assertFalse(URLValidator.isValidUrl(url));
    }

    @Test
    @DisplayName("URL exceeding 2048 characters should return false")
    void testUrlExceeding2048Characters() {
        StringBuilder longUrl = new StringBuilder("https://example.com/");
        for (int i = 0; i < 2050; i++) {
            longUrl.append("a");
        }
        assertFalse(URLValidator.isValidUrl(longUrl.toString()));
    }

    @Test
    @DisplayName("URL with exactly 2048 characters should be valid if format is correct")
    void testUrlWith2048Characters() {
        StringBuilder url = new StringBuilder("https://example.com/");
        // Fill up to 2048 characters
        while (url.length() < 2048) {
            url.append("a");
        }
        assertTrue(URLValidator.isValidUrl(url.toString()));
    }

    @Test
    @DisplayName("URL with special characters should be valid")
    void testUrlWithSpecialCharacters() {
        assertTrue(URLValidator.isValidUrl("https://example.com/path~with-special_chars!"));
    }

    @Test
    @DisplayName("URL with leading/trailing whitespace should be valid after trim")
    void testUrlWithWhitespace() {
        assertTrue(URLValidator.isValidUrl("  https://example.com  "));
    }

    // ==================== isValidUrlSecure() Tests ====================

    @Test
    @DisplayName("Valid public URL with both flags false")
    void testValidPublicUrlSecure() {
        assertTrue(URLValidator.isValidUrlSecure("https://www.google.com", false, false));
    }

    @Test
    @DisplayName("Localhost rejected when allowLocalhost is false")
    void testLocalhostRejectedWhenNotAllowed() {
        assertFalse(URLValidator.isValidUrlSecure("http://localhost/path", false, false));
    }

    @Test
    @DisplayName("Localhost accepted when allowLocalhost is true")
    void testLocalhostAcceptedWhenAllowed() {
        assertTrue(URLValidator.isValidUrlSecure("http://localhost/path", true, false));
    }

    @Test
    @DisplayName("127.0.0.1 rejected when allowLocalhost is false")
    void testLoopbackRejectedWhenNotAllowed() {
        assertFalse(URLValidator.isValidUrlSecure("http://127.0.0.1/path", false, false));
    }

    @Test
    @DisplayName("127.0.0.1 accepted when allowLocalhost is true")
    void testLoopbackAcceptedWhenAllowed() {
        assertTrue(URLValidator.isValidUrlSecure("http://127.0.0.1/path", true, false));
    }

    @ParameterizedTest
    @CsvSource({
        "http://192.168.1.1, false, false",
        "http://10.0.0.1, false, false",
        "http://172.16.0.1, false, false",
        "http://172.31.255.255, false, false"
    })
    @DisplayName("Private IPs rejected when allowPrivateIPs is false")
    void testPrivateIPsRejectedWhenNotAllowed(String url, boolean allowLocalhost, boolean allowPrivateIPs) {
        assertFalse(URLValidator.isValidUrlSecure(url, allowLocalhost, allowPrivateIPs));
    }

    @ParameterizedTest
    @CsvSource({
        "http://192.168.1.1, false, true",
        "http://10.0.0.1, false, true",
        "http://172.16.0.1, false, true"
    })
    @DisplayName("Private IPs accepted when allowPrivateIPs is true")
    void testPrivateIPsAcceptedWhenAllowed(String url, boolean allowLocalhost, boolean allowPrivateIPs) {
        assertTrue(URLValidator.isValidUrlSecure(url, allowLocalhost, allowPrivateIPs));
    }

    @Test
    @DisplayName("Invalid URL rejected by isValidUrlSecure")
    void testInvalidUrlInSecureValidation() {
        assertFalse(URLValidator.isValidUrlSecure("not-a-url", false, false));
    }

    @Test
    @DisplayName("Null URL rejected by isValidUrlSecure")
    void testNullUrlInSecureValidation() {
        assertFalse(URLValidator.isValidUrlSecure(null, false, false));
    }

    // ==================== extractDomain() Tests ====================

    @ParameterizedTest
    @CsvSource({
        "https://www.google.com, www.google.com",
        "http://example.com, example.com",
        "https://sub.domain.example.com, sub.domain.example.com",
        "https://example.com:8080/path, example.com",
        "http://localhost, localhost"
    })
    @DisplayName("Domain extraction from valid URLs")
    void testExtractDomain(String url, String expectedDomain) {
        assertEquals(expectedDomain, URLValidator.extractDomain(url));
    }

    @Test
    @DisplayName("Extract domain from valid URL with path and query")
    void testExtractDomainWithPathAndQuery() {
        assertEquals("example.com", URLValidator.extractDomain("https://example.com/path?query=value#fragment"));
    }

    @Test
    @DisplayName("Extract domain returns null for invalid URL")
    void testExtractDomainInvalidUrl() {
        assertNull(URLValidator.extractDomain("not-a-url"));
    }

    @Test
    @DisplayName("Extract domain returns null for null input")
    void testExtractDomainNullInput() {
        assertNull(URLValidator.extractDomain(null));
    }

    @Test
    @DisplayName("Extract domain returns null for empty input")
    void testExtractDomainEmptyInput() {
        assertNull(URLValidator.extractDomain(""));
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("URL with port number")
    void testUrlWithPortNumber() {
        assertTrue(URLValidator.isValidUrl("https://example.com:443/path"));
        assertTrue(URLValidator.isValidUrl("http://example.com:8080"));
    }

    @Test
    @DisplayName("URL with hyphenated domain")
    void testUrlWithHyphenatedDomain() {
        assertTrue(URLValidator.isValidUrl("https://my-domain.example-site.com"));
    }

    @Test
    @DisplayName("URL with different TLDs")
    void testUrlWithDifferentTLDs() {
        assertTrue(URLValidator.isValidUrl("https://example.co.uk"));
        assertTrue(URLValidator.isValidUrl("https://example.org"));
        assertTrue(URLValidator.isValidUrl("https://example.info"));
    }
}
