package com.example.demo.controller;


import com.example.demo.service.FileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/files")
public class FileController {

    private static final Logger logger = LogManager.getLogger(FileController.class);

    private static final String workingDir = System.getProperty("user.dir")+ File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"static"+File.separator+"upload"+File.separator;
    @Autowired
    FileService fileService;
    /*
    //https://stackoverflow.com/questions/28039709/what-is-difference-between-requestbody-and-requestparam
    //different used in ItemController and FileController
    //@RequestBody annotated parameter is expected to hold the entire body of the request and bind to one object, so you essentially will have to go with your option
    //so at here we use @RequestParam("file")
    @RequestMapping(value = "/addFile", method = RequestMethod.POST,consumes = "multipart/form-data")
    public void addFile(@RequestParam("file") MultipartFile item){
       // fileService.storeFile(item);
    }
    */
    @RequestMapping(value = "/addFiles", method = RequestMethod.POST,consumes = "multipart/form-data")
    public void addFiles(@RequestParam("files") MultipartFile[] items){
        fileService.storeFiles(items);
    }


    //wait 10 seconds to get resonse
    //if send two request at same time,
    // the controller method will be excute in separate thread
    //https://stackoverflow.com/questions/46223363/spring-boot-handle-multiple-requests-concurrently?rq=1
    @RequestMapping(value = "/handler", method = RequestMethod.POST, consumes = "application/json; charset=utf-8")
    public String handler(@RequestBody String jsonData){
        logger.info("Handler STARTED:"+new Date());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("Handler COMPLETED:"+new Date());
        return new Date().toString();
    }


    //works fine when open http://localhost:8080/files/511531243880_.pic.jpg
    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public void download(@PathVariable(value="name") String name, HttpServletResponse response){
        response.setHeader("Content-Disposition", "attachment; filename="+name);
        try {
            fileService.downloadFile(workingDir+name, response);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
