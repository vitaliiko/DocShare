package com.geekhub.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeSet;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements Serializable, Comparable<User> {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Column
    @Getter @Setter
    private String firstName;

    @Column
    @Getter @Setter
    private String lastName;

    @JsonIgnore
    @Column
    @Getter @Setter
    private String password;

    @Column(unique = true)
    @Getter @Setter
    private String login;

    @Column
    @Lob @Basic
    @Getter @Setter
    private byte[] avatar;

    @Column(unique = true)
    @Getter @Setter
    private String email;

    @Column
    @Getter @Setter
    private String country;

    @Column
    @Getter @Setter
    private String state;

    @Column
    @Getter @Setter
    private String city;

    @Column
    @Getter @Setter
    private Date registrationDate;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recipient_id")
    @Getter @Setter
    private Set<Event> events = new TreeSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "owner_id")
    @Getter @Setter
    private Set<FriendsGroup> friendsGroups = new HashSet<>();

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "user_to_friend_relation",
            joinColumns = {
                    @JoinColumn(name = "user_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "friend_id")
            }
    )
    @Getter @Setter
    private Set<User> friends = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "creator_id")
    @Getter @Setter
    private Set<Organization> organizations = new HashSet<>();

    public User(String firstName, String lastName, String password, String login) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.login = login;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public int compareTo(User o) {
        return this.getFirstName().compareTo(o.getFirstName());
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
