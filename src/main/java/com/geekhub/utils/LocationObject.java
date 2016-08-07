package com.geekhub.utils;

import com.geekhub.entities.UserDirectory;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class LocationObject {

    @Setter
    private List<String> dirNames = new ArrayList<>();

    @Setter
    private List<String> dirHashes = new ArrayList<>();

    public void addDirectory(UserDirectory directory) {
        dirNames.add(directory.getName());
        dirHashes.add(directory.getHashName());
    }

    public String getLocation() {
        String location = "";
        for (String name : dirNames) {
            location = name + "/" + location;
        }
        return location;
    }

    public boolean containHash(String dirHash) {
        return dirHashes.contains(dirHash);
    }
}
