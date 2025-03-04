package cit.edu.studyspace.Controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.*;

@Controller // Ensure we're serving Thymeleaf templates
public class UserController {

    
    @GetMapping("/user")
    public String userInfo(@AuthenticationPrincipal OAuth2User principal, Model model) {
        model.addAttribute("user", principal.getAttributes());
        return "user"; // Ensure user.html exists in resources/templates
    }

    @GetMapping("/contacts")
    public String getGoogleContacts(
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
            Model model) {

        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        List<Contact> contacts = fetchGoogleContacts(accessToken);

        model.addAttribute("contacts", contacts);
        return "contacts"; // Ensure contacts.html exists in resources/templates
    }

    private List<Contact> fetchGoogleContacts(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String apiUrl = "https://people.googleapis.com/v1/people/me/connections"
                + "?personFields=names,emailAddresses,phoneNumbers";

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}
        );

        Map<String, Object> body = response.getBody();
        List<Contact> contacts = new ArrayList<>();

        if (body != null && body.containsKey("connections")) {
            List<Map<String, Object>> connections = (List<Map<String, Object>>) body.get("connections");

            for (Map<String, Object> connection : connections) {
                contacts.add(parseContact(connection));
            }
        }

        return contacts;
    }

    private Contact parseContact(Map<String, Object> connection) {
        Contact contact = new Contact();

        // Extract Name
        if (connection.containsKey("names")) {
            List<Map<String, Object>> names = (List<Map<String, Object>>) connection.get("names");
            if (!names.isEmpty() && names.get(0).containsKey("displayName")) {
                contact.setName((String) names.get(0).get("displayName"));
            }
        }

        // Extract Emails
        if (connection.containsKey("emailAddresses")) {
            List<Map<String, Object>> emails = (List<Map<String, Object>>) connection.get("emailAddresses");
            for (Map<String, Object> emailData : emails) {
                if (emailData.containsKey("value")) {
                    contact.getEmails().add((String) emailData.get("value"));
                }
            }
        }

        // Extract Phone Numbers
        if (connection.containsKey("phoneNumbers")) {
            List<Map<String, Object>> phones = (List<Map<String, Object>>) connection.get("phoneNumbers");
            for (Map<String, Object> phoneData : phones) {
                if (phoneData.containsKey("value")) {
                    contact.getPhones().add((String) phoneData.get("value"));
                }
            }
        }

        return contact;
    }

    static class Contact {
        private String name;
        private List<String> emails = new ArrayList<>();
        private List<String> phones = new ArrayList<>();

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<String> getEmails() { return emails; }
        public void setEmails(List<String> emails) { this.emails = emails; }
        public List<String> getPhones() { return phones; }
        public void setPhones(List<String> phones) { this.phones = phones; }
    }
}
