package com.geekhub.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
public class FriendsGroup extends MappedEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    @Column
    private String name;
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "friendsGroupSet")
    private Set<User> friendsSet;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getFriendsSet() {
        return friendsSet;
    }

    public void setFriendsSet(Set<User> friendsSet) {
        this.friendsSet = friendsSet;
    }
}
