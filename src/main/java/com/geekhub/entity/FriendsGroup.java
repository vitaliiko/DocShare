package com.geekhub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
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
