package com.guide.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final String UPLOAD_DIR = "src/main/resources/img/avatar/";

    @PostMapping("/avatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        String projectRoot = System.getProperty("user.dir");
        File uploadDir = new File(projectRoot, UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String fileName = System.currentTimeMillis()
                + "_" + (int) (Math.random() * 1000)
                + extension;
        
        File targetFile = new File(uploadDir, fileName);
        file.transferTo(targetFile);
        
        return "/img/avatar/" + fileName;
    }
}