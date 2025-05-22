package com.umm.app.valid;


import com.umm.app.impl.CustomUserDetails;
import com.umm.exception.BaseException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CommonValidation {

    public static void validateCustomUser(CustomUserDetails customUserDetails){
        if (customUserDetails == null){
            throw new BaseException(401, "로그인이 필요한 정보입니다.");
        }
    }

    public static void validateUUID(String stringUUID){
        try {
            UUID tempId = UUID.fromString(stringUUID);
        } catch (IllegalArgumentException e) {
            throw new BaseException(400, stringUUID + "는 Id 형식이 UUID와 일치하지 않습니다. \n 상세 내용 :" + e.getMessage());
        }
    }
}
