package com.geekhub.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.TreeSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class User implements Comparable<User> {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @JsonIgnore
    @Column
    private String password;

    @Column(unique = true)
    private String login;

    @Column(unique = true)
    private String email;

    @Column
    private String country;

    @Column
    private String state;

    @Column
    private String city;

    @Column
    private Date registrationDate;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recipient_id")
    private Set<Event> events = new TreeSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "owner_id")
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
    private Set<User> friends = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private Set<UserDocument> userDocuments = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private Set<UserDirectory> userDirectories = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private Set<RemovedDocument> removedDocuments = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private Set<RemovedDirectory> removedDirectories = new HashSet<>();

    public User() {}

    public User(String firstName, String lastName, String password, String login) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.login = login;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = (TreeSet<Event>) events;
    }

    public Set<FriendsGroup> getFriendsGroups() {
        return friendsGroups;
    }

    public void setFriendsGroups(Set<FriendsGroup> ownerGroupSet) {
        this.friendsGroups = ownerGroupSet;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    public Set<UserDocument> getUserDocuments() {
        return userDocuments;
    }

    public void setUserDocuments(Set<UserDocument> userDocuments) {
        this.userDocuments = userDocuments;
    }

    public Set<RemovedDocument> getRemovedDocuments() {
        return removedDocuments;
    }

    public Set<UserDirectory> getUserDirectories() {
        return userDirectories;
    }

    public void setUserDirectories(Set<UserDirectory> userDirectories) {
        this.userDirectories = userDirectories;
    }

    public void setRemovedDocuments(Set<RemovedDocument> removedDocuments) {
        this.removedDocuments = removedDocuments;
    }

    public Set<RemovedDirectory> getRemovedDirectories() {
        return removedDirectories;
    }

    public void setRemovedDirectories(Set<RemovedDirectory> removedDirectories) {
        this.removedDirectories = removedDirectories;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id)
                && firstName.equals(user.firstName)
                && lastName.equals(user.lastName)
                && login.equals(user.login);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + login.hashCode();
        return result;
    }

    @Override
    public int compareTo(User o) {
        return this.getFirstName().compareTo(o.getFirstName());
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
