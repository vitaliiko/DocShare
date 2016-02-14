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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", targetEntity = Message.class, fetch = FetchType.EAGER)
    private Set<Message> messageSet = new HashSet<>();
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", targetEntity = FriendsGroup.class)
//    @JoinTable(name = "friendsGroups", joinColumns = {@JoinColumn(name = "owner_id")},
//            inverseJoinColumns = {@JoinColumn(name = "group_id")})
//    private Set<FriendsGroup> friendsGroupSet;
//    @ManyToMany
//    private Set<FriendsGroup> consistGroupSet;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<User> friends = new HashSet<>();

    @ManyToMany(mappedBy = "friends")
    private Set<User> inFriends = new HashSet<>();

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

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    public Set<User> getInFriends() {
        return inFriends;
    }

    public void setInFriends(Set<User> inFriends) {
        this.inFriends = inFriends;
    }

    //    public Set<FriendsGroup> getFriendsGroupSet() {
//        return friendsGroupSet;
//    }
//
//    public void setFriendsGroupSet(Set<FriendsGroup> friendsGroupSet) {
//        this.friendsGroupSet = friendsGroupSet;
//    }
//
//    public Set<FriendsGroup> getConsistGroupSet() {
//        return consistGroupSet;
//    }
//
//    public void setConsistGroupSet(Set<FriendsGroup> consistGroupSet) {
//        this.consistGroupSet = consistGroupSet;
//    }

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
