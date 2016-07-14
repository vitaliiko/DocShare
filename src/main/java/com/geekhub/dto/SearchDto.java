package com.geekhub.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class SearchDto {

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String country;

    @Getter @Setter
    private String region;

    @Getter @Setter
    private String city;

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
