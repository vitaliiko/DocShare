package com.geekhub.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class User extends MappedEntity {
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String password;
    @Column(unique = true)
    private String login;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private Set<Message> messageSet = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<FriendsGroup> friendsGroupSet = new HashSet<>();
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<FriendsGroup> foreignGroupSet = new HashSet<>();

//    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinTable(name = "user_friend", joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "friend_id"))
//    private Set<User> friends = new HashSet<>();
//
//    @ManyToMany(mappedBy = "friends", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private Set<User> friendsOf = new HashSet<>();

    public User() {
    }

    public User(String firstName, String lastName, String password, String login) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.login = login;
    }

    //    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }

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

    public Set<Message> getMessageSet() {
        return messageSet;
    }

    public void setMessageSet(Set<Message> messageSet) {
        this.messageSet = messageSet;
    }

    public Set<FriendsGroup> getFriendsGroupSet() {
        return friendsGroupSet;
    }

    public void setFriendsGroupSet(Set<FriendsGroup> friendsGroupSet) {
        this.friendsGroupSet = friendsGroupSet;
    }

    public Set<FriendsGroup> getForeignGroupSet() {
        return foreignGroupSet;
    }

    public void setForeignGroupSet(Set<FriendsGroup> foreignGroupSet) {
        this.foreignGroupSet = foreignGroupSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return firstName.equals(user.firstName) && lastName.equals(user.lastName) && login.equals(user.login);
    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + login.hashCode();
        return result;
    }
}
