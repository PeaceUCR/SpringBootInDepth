package com.example.demo.service;




import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.nio.file.Files;


@Service
public class FileService {


    /*
    public void init(){

    }
    */
    //java io vs nio
    //https://dzone.com/articles/java-nio-vs-io
    /*
    //private static final String workingDir = System.getProperty("user.dir")+ File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"static"+File.separator+"upload"+File.separator;
    public void storeFile(MultipartFile file){
        if (file.isEmpty()) {
            logger.info("message", "Please select a file to upload");
        }

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
    */

    //use thread pool handle multi file upload
    public void storeFiles(MultipartFile[] file){
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for( int i=0; i<file.length; i++){
            executorService.execute(new Task(file[i]));
        }
        executorService.shutdown();
    }


//https://stackoverflow.com/questions/48622208/convert-object-from-java-nio-file-path-to-java-io-file
//http://tutorials.jenkov.com/java-nio/channels.html#channel-implementations
    //https://stackoverflow.com/questions/858980/file-to-byte-in-java
    public void downloadFile(String name, HttpServletResponse response) throws IOException{
        Path path = Paths.get(name);
        if(Files.exists(path)){

           FileChannel inChannel = FileChannel.open(path);

            int size = (int) inChannel.size();

            MappedByteBuffer buf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, size);

            byte[] bytes = new byte[size];
            buf.get(bytes);
            //outputstream can't write buffer
           OutputStream ros = response.getOutputStream();
           ros.write(bytes);
        }
    }
    /*
    public Stream<Path> loadAll(){

    };

    public Path load(String filename){

    };

    public Resource loadAsResource(String filename){

    };

    public void deleteAll(){

    };
    */
}