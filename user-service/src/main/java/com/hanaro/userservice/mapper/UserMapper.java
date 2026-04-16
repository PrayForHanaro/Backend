package com.hanaro.userservice.mapper;

import com.hanaro.userservice.domain.User;
import com.hanaro.userservice.dto.UserGivingResponseDTO;
import com.hanaro.userservice.dto.UserHomeResponseDTO;
import com.hanaro.userservice.dto.UserSimpleResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userName", source = "name")
    @Mapping(target = "myPoint", expression = "java(user.getPoints().stream().mapToInt(com.hanaro.userservice.domain.Point::getAmount).sum())")
    UserHomeResponseDTO toUserHomeResponseDTO(User user);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "maxPoint", expression = "java(java.math.BigDecimal.valueOf(user.getPoints().stream().mapToInt(com.hanaro.userservice.domain.Point::getAmount).sum()))")
    @Mapping(target = "bankAccount", expression = "java(user.getDefaultAccount() != null ? user.getDefaultAccount().getAccountNumber() : \"계좌 없음\")")
    @Mapping(target = "accountId", expression = "java(user.getDefaultAccount() != null ? user.getDefaultAccount().getAccountId() : null)")
    UserGivingResponseDTO toUserGivingResponseDTO(User user);

    UserSimpleResponseDTO toUserSimpleResponseDTO(User user);
}
