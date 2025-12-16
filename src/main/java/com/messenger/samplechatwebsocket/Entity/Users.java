package com.messenger.samplechatwebsocket.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class Users {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String username;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    @Column(nullable = false)
    private String role = "employee";
    @ManyToMany
    @JoinTable(
            name = "user_contacts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "contact_id")
    )
    private Set<Users> contacts = new HashSet<>();


    public void addContact(Users user) {
        this.contacts.add(user);
    }

    public void removeContact(Users user) {
        this.contacts.remove(user);
    }


}
