package com.geekhub.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class FriendsGroup {
    @Id
    @GeneratedValue
    @Column
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @Column
    private String name;

    @ManyToMany
    @JoinTable(name = "userToGroupRelation",
            joinColumns = {
                    @JoinColumn(name = "groupId")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "userId")
            }
    )
    private Set<User> friendsSet = new HashSet<>();

    public FriendsGroup() {
    }

    public FriendsGroup(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return name;
    }
}
