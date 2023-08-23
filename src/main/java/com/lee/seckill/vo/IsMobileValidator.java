package com.lee.seckill.vo;

import com.lee.seckill.utils.ValidatorUtil;
import com.lee.seckill.validator.IsMobile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Valid;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required  = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(required){
            return ValidatorUtil.isMobile(value);
        }
        else{
            if(value.isEmpty()){
                return true;
            }
            else{
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
