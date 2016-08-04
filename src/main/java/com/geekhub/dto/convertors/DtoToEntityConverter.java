package com.geekhub.dto.convertors;

import com.geekhub.dto.ExtendedUserDto;
import com.geekhub.dto.FileSharedLinkDto;
import com.geekhub.entities.FileSharedLink;
import com.geekhub.entities.User;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.utils.DateTimeUtils;
import org.springframework.beans.BeanUtils;

public class DtoToEntityConverter {
    
    public static User merge(ExtendedUserDto userDto, User user) {
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setLogin(userDto.getLogin());
        user.setEmail(userDto.getEmail());
        user.setCountry(userDto.getCountry());
        user.setState(userDto.getState());
        user.setCity(userDto.getCity());
        return user;
    }

    public static FileSharedLink convert(FileSharedLinkDto linkDto) {
        String[] ignoredProperties = new String[] {
                "url", "clickNumber", "token", "lastDate", "relationType"
        };
        FileSharedLink fileSharedLink = new FileSharedLink();
        BeanUtils.copyProperties(linkDto, fileSharedLink, ignoredProperties);
        fileSharedLink.setLastDate(DateTimeUtils.convertDate(linkDto.getLastDate()));
        fileSharedLink.setRelationType(linkDto.getRelationType() == null ? FileRelationType.READ : linkDto.getRelationType());
        return fileSharedLink;
    }
}
