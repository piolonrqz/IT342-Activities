package cit.edu.studyspace.Model;

import java.util.ArrayList;
import java.util.List;

public class Contact {
    private String name;
    private List<String> emails = new ArrayList<>();
    private List<String> phones = new ArrayList<>();

    // getters and setters
    public String getName() { 
        return name; 
    }

    public void setName(String name) { 
        this.name = name; 
    }
    public List<String> getEmails() { 
        return emails; 
    }
    public void setEmails(List<String> emails) { 
        this.emails = emails; 
    }
    public List<String> getPhones() { 
        return phones; 
    }
    public void setPhones(List<String> phones) { 
        this.phones = phones; 
    }
}