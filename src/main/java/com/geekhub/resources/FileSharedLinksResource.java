package com.geekhub.resources;

import com.geekhub.dto.FileSharedLinkDto;
import com.geekhub.entities.User;
import com.geekhub.services.FileSharedLinkService;
import com.geekhub.services.UserService;
import com.geekhub.services.enams.FileType;
import com.geekhub.utils.FileSharedLinkUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class FileSharedLinksResource {

    @Inject
    private UserService userService;

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
    public ResponseEntity createLink(@RequestBody FileSharedLinkDto linkDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        fileSharedLinkService.create(linkDto, userId);
        return ResponseEntity.ok().build();
    }
}
