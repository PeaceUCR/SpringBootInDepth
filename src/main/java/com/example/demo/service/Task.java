package com.example.demo.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//specify a file then store somewhere
public class Task implements Runnable {
    public MultipartFile file;

    private static final String workingDir = System.getProperty("user.dir")+ File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"static"+File.separator+"upload"+File.separator;

    private static final Logger logger = LogManager.getLogger(Task.class);

    /* autowired here is null
    @Autowired
    FileService fileService;
    */
    public Task(MultipartFile file){
        this.file = file;
    }

    public void storeFile(MultipartFile file){
        logger.info("Task start for  " + file.getOriginalFilename());
        logger.info("File Size " + file.getSize());
        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(workingDir + file.getOriginalFilename());
            Files.write(path, bytes);

            logger.info("You successfully uploaded '" +workingDir+ file.getOriginalFilename() + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        storeFile(file);
    }


}
