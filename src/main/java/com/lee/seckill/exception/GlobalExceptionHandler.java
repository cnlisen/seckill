package com.lee.seckill.exception;

import com.lee.seckill.vo.RespBean;
import com.lee.seckill.vo.RespBeanEnum;
import org.apache.catalina.valves.rewrite.RewriteCond;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    // 全局异常处理函数（用于接收throw出的异常，并做出相应的处理）
    @ExceptionHandler(Exception.class)
    public RespBean ExceptionHandler(Exception e){
        // 自定义的异常
        if(e instanceof GlobalException){
            GlobalException ex = (GlobalException) e;
            return RespBean.error(ex.getRespBeanEnum());
        }
        // 参数绑定异常
        else if(e instanceof BindException){
            BindException ex = (BindException) e;
            RespBean respBean = RespBean.error(RespBeanEnum.BIND_ERROR);
            respBean.setMessage("参数校验异常：" + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        // 其余情况统统 返回 服务器异常
        else{
            RespBean respBean = RespBean.error(RespBeanEnum.ERROR);
            respBean.setMessage(e.getMessage());
            return respBean;
            //return RespBean.error(RespBeanEnum.ERROR);
        }
    }

}
