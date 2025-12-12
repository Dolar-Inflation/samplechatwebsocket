package com.messenger.samplechatwebsocket.Mapper;

import com.messenger.samplechatwebsocket.DTO.MessageDTO;
import com.messenger.samplechatwebsocket.Entity.Message;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
@Component
public class EntityToDTO {

    private final ObjectMapper mapper = new ObjectMapper();




    public Object toEntity(Object dto, Object entity) {

      return mapper.convertValue(dto, entity.getClass());

    }



}
