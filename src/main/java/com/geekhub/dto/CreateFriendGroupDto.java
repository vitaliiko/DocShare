package com.geekhub.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

public class CreateFriendGroupDto {

    @Getter @Setter
    private long id;

    @NotNull
    @Getter @Setter
    private String groupName;

    @Getter @Setter
    private Long[] friends;

}
