package com.geekhub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class FriendsGroup {
    @Id
    @GeneratedValue
    @Column
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @Column
    private String name;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "userToGroupRelation",
            joinColumns = {
                    @JoinColumn(name = "groupId")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "userId")
            }
    )
    private Set<User> friends = new HashSet<>();

    public FriendsGroup() {
    }

    public FriendsGroup(String name) {
        this.name = name;
    }

    public FriendsGroup(String name, Set<User> friends) {
        this.name = name;
        this.friends = friends;
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

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friendsSet) {
        this.friends = friendsSet;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FriendsGroup group = (FriendsGroup) o;

        if (id != null ? !id.equals(group.id) : group.id != null) return false;
        if (owner != null ? !owner.equals(group.owner) : group.owner != null) return false;
        return !(name != null ? !name.equals(group.name) : group.name != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
