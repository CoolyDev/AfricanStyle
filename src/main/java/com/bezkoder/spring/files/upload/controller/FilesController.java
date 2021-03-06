package com.bezkoder.spring.files.upload.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.bezkoder.spring.files.upload.message.ResponseFile;
import com.bezkoder.spring.files.upload.model.Item;
import com.bezkoder.spring.files.upload.model.Product;
import com.bezkoder.spring.files.upload.repository.ItemRepository;
import com.bezkoder.spring.files.upload.repository.ProductRepository;
import com.bezkoder.spring.files.upload.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bezkoder.spring.files.upload.message.ResponseMessage;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@CrossOrigin("*")
public class FilesController {

   @Autowired
   private FileStorageService storageService;

   @Autowired
   private ItemRepository itemRepository;

    @Autowired
    private ProductRepository productRepository;

   @PostMapping("/upload")
   public Item uploadFile(@RequestParam("file") MultipartFile file) {
     try {
         return storageService.store(file);
     } catch (Exception e) {
         System.out.println(e);
      return null;
     }
   }

   @GetMapping("/files")
   public ResponseEntity<List<ResponseFile>> getListFiles() {
     List<ResponseFile> files = storageService.getAllFiles().map(dbFile -> {
       String fileDownloadUri = ServletUriComponentsBuilder
               .fromCurrentContextPath()
               .path("/files/")
               .path(dbFile.getId())
               .toUriString();

       return new ResponseFile(
               dbFile.getName(),
               fileDownloadUri,
               dbFile.getType(),
               dbFile.getData().length);
     }).collect(Collectors.toList());

     return ResponseEntity.status(HttpStatus.OK).body(files);
   }

   @GetMapping("/items")
   public List<Item> displayItem (){
     return itemRepository.findAll();
   }



   @GetMapping("/files/{id}")
   public ResponseEntity<byte[]> getFile(@PathVariable String id) {
     Item item = storageService.getFile(id);

     return ResponseEntity.ok()
             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + item.getName() + "\"")
             .body(item.getData());
   }


/*    @PostMapping("/uploadx")
    public String uploadMultipartFile(@RequestParam("files") MultipartFile[] files, Model model) {
        List fileNames = new ArrayList();

        try {
            List storedFile = new ArrayList();

            for(MultipartFile file: files) {
                Item fileModel = itemRepository.findByName(file.getOriginalFilename());
                if(fileModel != null) {
                    // update new contents
                    fileModel.setData(file.getBytes());
                }else {
                    fileModel = new Item(file.getOriginalFilename(), file.getContentType(), file.getBytes());
                }

                fileNames.add(file.getOriginalFilename());
                storedFile.add(fileModel);
            }

            // Save all Files to database
            itemRepository.saveAll(storedFile);

            model.addAttribute("message", "Files uploaded successfully!");
            model.addAttribute("files", fileNames);
        } catch (Exception e) {
            model.addAttribute("message", "Fail!");
            model.addAttribute("files", fileNames);
        }

        return "uploadform";
    }*/
}
