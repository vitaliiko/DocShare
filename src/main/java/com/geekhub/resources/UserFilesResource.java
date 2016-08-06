package com.geekhub.resources;

import com.geekhub.dto.FileIdsDto;
import com.geekhub.dto.RemovedFileDto;
import com.geekhub.dto.ZipDto;
import com.geekhub.entities.*;
import com.geekhub.dto.UserFileDto;
import com.geekhub.exceptions.FileOperationException;
import com.geekhub.resources.utils.FileControllersUtil;
import com.geekhub.services.*;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.validators.FileValidator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserFilesResource {

    @Inject
    private UserService userService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private UserDirectoryService userDirectoryService;

    @Inject
    private FileValidator fileValidator;

    @Inject
    private RemovedDocumentService removedDocumentService;

    @Inject
    private RemovedDirectoryService removedDirectoryService;

    @Inject
    private UserToDocumentRelationService userToDocumentRelationService;

    @Inject
    private UserToDirectoryRelationService userToDirectoryRelationService;

    @Inject
    private FriendGroupToDocumentRelationService friendGroupToDocumentRelationService;

    @Inject
    private FriendGroupToDirectoryRelationService friendGroupToDirectoryRelationService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @InitBinder("multipartFile")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(fileValidator);
    }

    @RequestMapping(value = "/files/search", method = RequestMethod.GET)
    public ResponseEntity<Set<UserFileDto>> searchFiles(@RequestParam String searchName, HttpSession session) {
        User user = getUserFromSession(session);
        Set<UserDocument> documents = null;
        Set<UserDirectory> directories = null;

        if (searchName != null) {
            documents = userDocumentService.searchByName(user, searchName);
            directories = userDirectoryService.searchByName(user, searchName);
        }

        Set<UserFileDto> dtoSet = new TreeSet<>();
        if (documents != null) {
            documents.forEach(d -> dtoSet.add(EntityToDtoConverter.convert(d)));
        }
        if (directories != null) {
            directories.forEach(d -> dtoSet.add(EntityToDtoConverter.convert(d)));
        }
        return ResponseEntity.ok(dtoSet);
    }

    @RequestMapping(value = "/files/replace", method = RequestMethod.POST)
    public ResponseEntity replaceFiles(@Valid @RequestBody FileIdsDto filesDto, HttpSession session)
            throws FileOperationException {

        User user = getUserFromSession(session);
        Set<UserDocument> documents = userDocumentService.getAllByIds(filesDto.getDocIds());
        if (FileControllersUtil.cannotReplaceDocuments(documents, filesDto.getDestinationDirHash())) {
            return ResponseEntity.badRequest().build();
        }
        userDocumentService.replace(documents, filesDto.getDestinationDirHash(), user);

        Set<UserDirectory> directories = userDirectoryService.getAllByIds(filesDto.getDirIds());
        if (FileControllersUtil.cannotReplaceDirectories(directories, filesDto.getDestinationDirHash(), user)) {
            return ResponseEntity.badRequest().build();
        }
        userDirectoryService.replace(directories, filesDto.getDestinationDirHash(), user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/files/copy", method = RequestMethod.POST)
    public ResponseEntity<Void> copyFiles(@Valid @RequestBody FileIdsDto filesDto, HttpSession session)
            throws FileOperationException {

        User user = getUserFromSession(session);
        Set<UserDocument> documents = userDocumentService.getAllByIds(filesDto.getDocIds());
        userDocumentService.copy(documents, filesDto.getDestinationDirHash(), user);

        Set<UserDirectory> directories = userDirectoryService.getAllByIds(filesDto.getDirIds());
        if (FileControllersUtil.cannotCopyDirectories(directories, filesDto.getDestinationDirHash())) {
            return ResponseEntity.badRequest().build();
        }
        userDirectoryService.copy(directories, filesDto.getDestinationDirHash(), user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/files/download", method = RequestMethod.GET)
    public ResponseEntity downloadFilesInZIP(@RequestParam(value = "dirIds[]", required = false) List<Long> dirIds,
                                             @RequestParam(value = "docIds[]", required = false) List<Long> docIds,
                                             HttpServletResponse response) throws IOException {

        ZipDto zip = userDocumentService.packDocumentsToZIP(docIds);
        openOutputStream(zip, response);
        return ResponseEntity.ok().build();
    }

    private static void openOutputStream(ZipDto zip, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/zip");
        response.setContentLength(zip.getContentLength());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + zip.getZipName() + "\"");

        ByteArrayInputStream in = new ByteArrayInputStream(zip.getZipFile());

        FileCopyUtils.copy(in, response.getOutputStream());
    }

    @RequestMapping(value = "/files/move-to-trash", method = RequestMethod.POST)
    public ResponseEntity moveFilesToTrash(@RequestParam(value = "docIds[]", required = false) Long[] docIds,
                                           @RequestParam(value = "dirIds[]", required = false) Long[] dirIds,
                                           HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (docIds != null) {
            userDocumentService.moveToTrash(docIds, userId);
        }
        if (dirIds != null) {
            userDirectoryService.moveToTrash(dirIds, userId);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/files/removed", method = RequestMethod.GET)
    public ModelAndView recoverDocument(HttpSession session) {
        ModelAndView model = new ModelAndView("recover");

        Long ownerId = (Long) session.getAttribute("userId");
        Set<RemovedFileDto> documents = new TreeSet<>();
        removedDocumentService.getAllByOwnerId(ownerId).forEach(d -> {
            User user = userService.getById(d.getRemoverId());
            documents.add(EntityToDtoConverter.convert(d, user.getFullName()));
        });

        Set<RemovedFileDto> directories = new TreeSet<>();
        removedDirectoryService.getAllByOwnerId(ownerId).forEach(d -> {
            User user = userService.getById(d.getRemoverId());
            directories.add(EntityToDtoConverter.convert(d, user.getFullName()));
        });

        model.addObject("documents", documents);
        model.addObject("directories", directories);
        return model;
    }

    @RequestMapping(value = "/files/accessible", method = RequestMethod.GET)
    public ModelAndView getAccessibleDocuments(HttpSession session) {
        User user = getUserFromSession(session);

        Set<UserDirectory> accessibleDirectories = userToDirectoryRelationService.getAllAccessibleDirectories(user);
        accessibleDirectories.addAll(friendGroupToDirectoryRelationService.getAllAccessibleDirectories(user));
        List<String> directoryHashes = accessibleDirectories.stream()
                .map(UserDirectory::getHashName).collect(Collectors.toList());

        Set<UserToDocumentRelation> documentRelations =
                userToDocumentRelationService.getAllAccessibleInRoot(user, directoryHashes);
        Set<UserToDirectoryRelation> directoryRelations =
                userToDirectoryRelationService.getAllAccessibleInRoot(user, directoryHashes);

        return prepareModel(directoryRelations, documentRelations);
    }

    private ModelAndView prepareModel(Set<UserToDirectoryRelation> directoryRelations,
                                      Set<UserToDocumentRelation> documentRelations) {

        Set<UserFileDto> documentDtos = documentRelations.stream()
                .map(EntityToDtoConverter::convert)
                .collect(Collectors.toCollection(TreeSet::new));

        Set<UserFileDto> directoryDtos = directoryRelations.stream()
                .map(EntityToDtoConverter::convert)
                .collect(Collectors.toCollection(TreeSet::new));

        ModelAndView model = new ModelAndView("accessibleDocuments");
        model.addObject("documents", documentDtos);
        model.addObject("directories", directoryDtos);
        return model;
    }

    @ExceptionHandler(FileOperationException.class)
    public ResponseEntity<String> fileOperationExceptionHandler(FileOperationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}