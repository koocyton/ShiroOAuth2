package com.doopp.gauss.common.mapper;

import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.SessionUser;
import com.doopp.gauss.common.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "identity", ignore = true)
    @Mapping(target = "room_id", ignore = true)
    @Mapping(target = "seat", ignore = true)
    Player userToPlayer(User user);

    @Mapping(target = "session_token", expression = "java(java.util.UUID.randomUUID().toString())")
    SessionUser userToSessionUser(User user);
}
