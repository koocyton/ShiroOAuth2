package com.doopp.gauss.common.mapper;

import com.doopp.gauss.common.dto.UserDTO;
import com.doopp.gauss.common.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    // @Mapping(target = "loginTime", ignore = true)
    UserDTO userToUserDTO(User user);
}
