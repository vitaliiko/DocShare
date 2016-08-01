package com.geekhub.resources;

import com.geekhub.dto.FileSharedLinkDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.FileSharedLink;
import com.geekhub.services.FileSharedLinkService;
import com.geekhub.services.enams.FileType;
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

    @RequestMapping(value = "/links/generate-hash/{fileId}", method = RequestMethod.GET)
    public ResponseEntity<String> generateLinkHash(@PathVariable Long fileId, @RequestParam FileType fileType,
                                                   HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        String shareURL = FileSharedLinkUtil.createFileShareURL(fileId, fileType, userId);
        return ResponseEntity.ok(shareURL);
    }

    @RequestMapping(value = "/links", method = RequestMethod.POST)
    public ResponseEntity createLink(@RequestBody FileSharedLinkDto linkDto, HttpSession session)
            throws IOException {

        Long userId = (Long) session.getAttribute("userId");
        fileSharedLinkService.createOrUpdate(linkDto, userId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/links/{fileHash}", method = RequestMethod.GET)
    public ResponseEntity<FileSharedLinkDto> getLink(@PathVariable String fileHash) {
        FileSharedLink sharedLink = fileSharedLinkService.getByFileHashName(fileHash);
        return ResponseEntity.ok().body(EntityToDtoConverter.convert(sharedLink));
    }
}
