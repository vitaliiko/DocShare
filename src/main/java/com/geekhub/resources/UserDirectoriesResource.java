package com.geekhub.resources;

import com.geekhub.dto.DirectoryContentDto;
import com.geekhub.dto.FileAccessDto;
import com.geekhub.dto.SharedDto;
import com.geekhub.dto.UserFileDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import com.geekhub.utils.UserFileUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class UserDirectoriesResource {

    @Inject
    private UserService userService;

    @Inject
    private UserDirectoryService userDirectoryService;

    @Inject
    private UserDocumentService userDocumentService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @RequestMapping(value = "/directories/{dirHashName}/documents/upload", method = RequestMethod.POST)
    public ModelAndView uploadDocuments(@RequestParam("files[]") MultipartFile[] files,
                                        @PathVariable("dirHashName") String parentDirectoryHash,
                                        HttpSession session) throws IOException {

        User user = getUserFromSession(session);
        if (UserFileUtil.isValidFileUploading(files)) {
            for (MultipartFile file : files) {
                userDocumentService.saveOrUpdateDocument(file, parentDirectoryHash, user);
            }
        }

        return new ModelAndView("redirect:/api/documents");
    }

    @RequestMapping(value = "/directories/{dirId}/recover", method = RequestMethod.POST)
    public ModelAndView recoverDirectory(@PathVariable("dirId") Long removedDirId) {
        userDirectoryService.recover(removedDirId);
        return new ModelAndView("redirect:/api/documents");
    }

    @RequestMapping(value = "/directories/{dirHashName}/make-dir", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> makeDir(@PathVariable("dirHashName") String parentDirHash,
                                               @RequestParam String dirName,
                                               HttpSession session) {

        User owner = getUserFromSession(session);
        UserDirectory existingDirectory = userDirectoryService.getByFullNameAndOwner(owner, parentDirHash, dirName);
        if (existingDirectory != null || !UserFileUtil.validateDirectoryName(dirName)) {
            return ResponseEntity.badRequest().body(null);
        }
        UserDirectory newDirectory = userDirectoryService.createDirectory(owner, parentDirHash, dirName);
        return ResponseEntity.ok(EntityToDtoConverter.convert(newDirectory));
    }

    @RequestMapping(value = "/directories/{dirId}/access", method = RequestMethod.GET)
    public ResponseEntity<FileAccessDto> getUserDirectoryAccessAttributes(@PathVariable Long dirId) {
        FileAccessDto accessDto = userDirectoryService.findAllRelations(dirId);
        return ResponseEntity.ok(accessDto);
    }

    @RequestMapping(value = "/directories/{dirId}/rename", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> renameDirectory(@PathVariable Long dirId,
                                                       @RequestParam String newDirName,
                                                       HttpSession session) {

        UserDirectory directory = userDirectoryService.getById(dirId);
        User user = getUserFromSession(session);
        if (directory.getName().equals(newDirName)
                || !userDocumentService.isDocumentNameValid(directory.getParentDirectoryHash(), newDirName, user)) {
            return ResponseEntity.badRequest().body(null);
        }
        UserDirectory directoryWithNewName = userDirectoryService.renameDirectory(directory, newDirName, user);
        return ResponseEntity.ok(EntityToDtoConverter.convert(directoryWithNewName));
    }

    @RequestMapping(value = "/directories/{dirId}/share", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> shareUserDirectory(@PathVariable Long dirId,
                                                          @RequestBody SharedDto shared, HttpSession session) {

        User user = getUserFromSession(session);
        UserDirectory directory = userDirectoryService.getById(dirId);
        UserDirectory sharedDirectory = userDirectoryService.shareDirectory(directory, shared, user);
        return ResponseEntity.ok(EntityToDtoConverter.convert(sharedDirectory));
    }

    @RequestMapping(value = "/directories/{dirHashName}/content", method = RequestMethod.GET)
    public ResponseEntity<DirectoryContentDto> getDirectoryContent(@PathVariable String dirHashName, HttpSession session) {
        User user = getUserFromSession(session);
        return ResponseEntity.ok(userDirectoryService.getDirectoryContent(dirHashName, user));
    }
}
