package com.exmaple.upload.Service;

import com.exmaple.upload.Domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.io.File;
@Component
public class FileStoreService {


    //환경변수 자바 변수에 등록하기
    @Value("${file.dir}")
    public String fileDir;


    public String getFullPath(String filename){
        return fileDir+filename;
    }

    //http 내 멀티파트를 불러와 uploadFile 객체로 바꿔주기
        //실제 저장이름 <-> 유저가 저장한 이름
    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();

        for(MultipartFile multipartFile : multipartFiles){
            if(!multipartFile.isEmpty()){
                storeFileResult.add(storeFile(multipartFile));
            }
        }

        return storeFileResult;
    }


    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()){
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storedFilename = createStoredFileName(originalFilename);

        //파일 업로드 처리
        multipartFile.transferTo(new File(getFullPath(storedFilename)));

        return new UploadFile(originalFilename, storedFilename);
    }


    //uuid로 파일명을 저장 -> 유일성 보장
    private String createStoredFileName(String originalFilename){
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid+"."+ext;
    }

    //확장자를 별도로 축하ㅏ여 붙여준다.
    private String extractExt(String originalFilename){
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos+1);
    }
}
