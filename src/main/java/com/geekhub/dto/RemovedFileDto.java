package com.geekhub.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@EqualsAndHashCode(of = "id")
public class RemovedFileDto implements Comparable<RemovedFileDto> {

    @Getter @Setter
    private long id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String type;

    @Getter @Setter
    private Date removalDate;

    @Getter @Setter
    private String removerName;

    @Override
    public int compareTo(RemovedFileDto o) {
        return this.getName().compareTo(o.getName());
    }

}
