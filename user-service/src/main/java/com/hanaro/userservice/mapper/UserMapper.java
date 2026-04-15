package com.hanaro.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hanaro.userservice.domain.User;
import com.hanaro.userservice.dto.UserGivingOnceDTO;

@Mapper
public interface UserMapper {
	@Mapping(target = "bankAccount", ignore = true) // Account 에서 불러오기
	@Mapping(target = "churchName", ignore=true) // org_service 에서 불러오기
	@Mapping(target = "maxPoint", ignore = true) // Point 에서 가져와서 넣어줘야함
	UserGivingOnceDTO toDTO(User user);
}
