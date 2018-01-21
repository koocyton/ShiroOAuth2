package com.doopp.gauss.common.mapper;

import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    Player userToPlay(User user);
}
