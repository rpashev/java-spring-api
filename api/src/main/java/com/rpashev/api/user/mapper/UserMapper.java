package com.rpashev.api.user.mapper;

import com.rpashev.api.user.dto.RegisterUserDTO;
import com.rpashev.api.user.dto.UserDTO;
import com.rpashev.api.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    UserDTO toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterUserDTO dto);
}
