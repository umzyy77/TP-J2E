package org.example.tpj2eannonces.features.user.mapper;

import java.util.List;

import org.example.tpj2eannonces.features.user.dto.UserResponseDTO;
import org.example.tpj2eannonces.features.user.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponseDTO(User user);

    List<UserResponseDTO> toResponseDTOList(List<User> users);
}
