package com.geekhub.resources;

import com.geekhub.dto.FileSharedLinkDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.FileSharedLink;
import com.geekhub.services.FileSharedLinkService;
import com.geekhub.utils.FileSharedLinkUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FileSharedLinksResource {

    @Inject
    private FileSharedLinkService fileSharedLinkService;

    @RequestMapping(value = "/links", method = RequestMethod.POST)
    public ResponseEntity<FileSharedLinkDto> createLink(@RequestBody FileSharedLinkDto linkDto, HttpSession session)
            throws IOException {

        FileSharedLinkUtil.checkSharedLinkDto(linkDto);
        Long userId = (Long) session.getAttribute("userId");
        FileSharedLink sharedLink = fileSharedLinkService.createOrUpdate(linkDto, userId);
        return ResponseEntity.ok().body(EntityToDtoConverter.convert(sharedLink));
    }

    @RequestMapping(value = "/links/{fileHash}", method = RequestMethod.GET)
    public ResponseEntity<FileSharedLinkDto> getLink(@PathVariable String fileHash) {
        FileSharedLink sharedLink = fileSharedLinkService.getByFileHashName(fileHash);
        return ResponseEntity.ok().body(EntityToDtoConverter.convert(sharedLink));
    }
}
