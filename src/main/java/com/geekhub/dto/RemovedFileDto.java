package com.geekhub.dto;

import com.geekhub.services.enams.FileType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(of = "fileId")
public class RemovedFileDto implements Comparable<RemovedFileDto> {

    @Getter @Setter
    private long fileId;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private FileType type;

    @Getter @Setter
    private String removalDate;

    @Getter @Setter
    private String removerName;

    @Override
    public int compareTo(RemovedFileDto o) {
        return this.getName().compareTo(o.getName());
    }

}
