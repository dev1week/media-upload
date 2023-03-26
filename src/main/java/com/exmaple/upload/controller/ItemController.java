package com.exmaple.upload.controller;


import com.exmaple.upload.Domain.UploadFile;
import com.exmaple.upload.Service.FileStoreService;
import com.exmaple.upload.Repository.ItemRepository;
import com.exmaple.upload.Domain.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.core.io.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import org.springframework.web.util.UriUtils;

import java.io.File;
import org.springframework.http.MediaType;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final FileStoreService fileStoreService;

    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form){
        return "item-form";
    }

    //입력 받은 폼 데이터 저장하기
    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {

        UploadFile attachFile = fileStoreService.storeFile(form.getAttachFile());

        List<UploadFile> storeImageFiles = fileStoreService.storeFiles(form.getImageFiles());


        //db에 저장하기
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);
        itemRepository.save(item);


        redirectAttributes.addAttribute("itemId", item.getId());

        return "redirect:/items/{itemId}";

    }


    @GetMapping("/items/{id}")
    public String items(@PathVariable Long id, Model model){
        Item item = itemRepository.findById(id);
        if(item == null){
            log.info("npe");
        }
        model.addAttribute("item", item);
        return "item-view";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:"+fileStoreService.getFullPath(filename));
    }
    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttatch(@PathVariable Long itemId) throws MalformedURLException {
        //db에서 아이템 아이디에 해당하는 값 가져오기
        Item item = itemRepository.findById(itemId);

        //실제 db에 저장한 이름 담기
        String storeFileName = item.getAttachFile().getStoreFileName();
        //고객이 업로드한 이름 담기
        String uploadFileName = item.getAttachFile().getUploadFileName();

        //저장되어 있는 위치 url 사용하여 꺼내오기
        UrlResource resource = new UrlResource("file:"+fileStoreService.getFullPath(storeFileName));

        log.info("uploadFileName={}", uploadFileName);


        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\""+encodedUploadFileName+"\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

//    @RequestMapping("/getImage/{id}")
//    @ResponseBody
//    public ResponseEntity<byte[]> profileImage(@PathVariable Long id ) throws IOException {
//        Item item = itemRepository.findById(id);
//
//        //실제 db에 저장한 이름 담기
//        String storeFileName = item.getAttachFile().getStoreFileName();
//        //고객이 업로드한 이름 담기
//        String uploadFileName = item.getAttachFile().getUploadFileName();
//
//        //저장되어 있는 위치 url 사용하여 꺼내오기
//        UrlResource resource = new UrlResource("file:"+fileStoreService.getFullPath(storeFileName));
//
//        log.info("uploadFileName={}", uploadFileName);
//
//
//        HttpHeaders header = new HttpHeaders();
//        header.setContentType(MediaType.IMAGE_JPEG);
//        return new ResponseEntity<byte[]>(IOUtils.toByteArray(new FileInputStream(new File(String.valueOf(resource))), header, HttpStatus.CREATED));
//    }

}
