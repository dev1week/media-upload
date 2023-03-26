package com.exmaple.upload.Domain.MongoDb;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MongoDBTestService {

    @Autowired
    MongoDBTestRepository mongoDBTestRepository;

    public String selectUser(String name){
        ObjectMapper objectMapper = new ObjectMapper();

        if(mongoDBTestRepository.findByName(name)== null){
            log.info("[Service] user name : {} not exist!!", name);
            return String.format("user name : %s not exist", name);
        }else{
            try {
                return objectMapper.writeValueAsString(mongoDBTestRepository.findByName(name));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "error";
            }
        }
    }

    public void saveUser(String name, int age){
        MongoDBTestModel mongoDBTestModel = new MongoDBTestModel();
        mongoDBTestModel.setName(name);
        mongoDBTestModel.setAge(age);

        if(mongoDBTestRepository.findByName(name)!=null){
            log.info("[Service][Update] name is already exist!");
            mongoDBTestModel.setId(mongoDBTestRepository.findByName(name).getId());
        }else{
            log.info("[Service][Insert] New name received");
        }
        mongoDBTestRepository.save(mongoDBTestModel);
    }
}
