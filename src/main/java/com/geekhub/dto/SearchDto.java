package com.geekhub.dto;

import java.util.HashMap;
import java.util.Map;

public class SearchDto {

    private String name;
    private String country;
    private String region;
    private String city;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Map<String, String> toMap() {
        Map<String, String> parametersMap = new HashMap<>();
        if (!country.isEmpty()) {
            parametersMap.put("country", country);
        }
        if (!region.isEmpty()) {
            parametersMap.put("state", region);
        }
        if (!city.isEmpty()) {
            parametersMap.put("city", city);
        }
        return parametersMap;
    }
}
