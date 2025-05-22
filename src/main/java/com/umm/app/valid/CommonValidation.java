package com.umm.app.valid;


import com.umm.app.impl.CustomUserDetails;
import com.umm.exception.BaseException;
import org.springframework.stereotype.Service;

@Service
public class CommonValidation {

    public static void validateCustomUser(CustomUserDetails customUserDetails){
        if (customUserDetails == null){
            throw new BaseException(401, "로그인이 필요한 정보입니다.");
        }
    }
}
