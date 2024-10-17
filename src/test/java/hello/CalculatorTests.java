package hello;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class CalculatorTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String extractResult(String body) {
        Pattern pattern = Pattern.compile("<span>(.*?)</span>");
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return body;
    }

    @Test
    public void testHomePage() throws Exception {
        ResponseEntity<String> entity = restTemplate
                .getForEntity("http://localhost:" + this.port + "/", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue(entity.getBody().contains("Калькулятор"), "Home page does not contain 'Калькулятор'");
        System.out.println("Home page test passed. Body contains 'Калькулятор'");
    }

    @Test
    public void testSimpleAddition() throws Exception {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("expression", "2 + 2");
        ResponseEntity<String> entity = restTemplate
                .postForEntity("http://localhost:" + this.port + "/calculate", formData, String.class);
        
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        String result = extractResult(entity.getBody());
        System.out.println("Simple addition result: " + result);
        assertEquals("4.0", result, "Simple addition failed");
    }
    

    
//    @Test
//    public void testFailingExpression() throws Exception {
//        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//        formData.add("expression", "5 - 1");
//        ResponseEntity<String> entity = restTemplate
//                .postForEntity("http://localhost:" + this.port + "/calculate", formData, String.class);
//
//        assertEquals(HttpStatus.OK, entity.getStatusCode());
//        String result = extractResult(entity.getBody());
//        System.out.println("Failing expression result: " + result);
//        assertEquals("2.0", result, "The expression '5 - 1' should have returned 4, but we're asserting for 2.");
//    }



}