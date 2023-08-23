package com.lee.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 公共返回对象
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {

    private long code;
    private String message;
    private Object obj;

    /**
     * @description: 成功情况下返回的结果
     *
     * @param:
     * @return:
     */
    public static RespBean success(){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), null);
    }
    public static RespBean success(Object obj){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), obj);
    }

    /**
     * @description: 失败情况下返回的结果
     *
     * @param:
     * @return:
     */
    //public static RespBean error(RespBeanEnum respBeanEnum){
    //    return new RespBean(RespBeanEnum.ERROR.getCode(), RespBeanEnum.ERROR.getMessage(), null);
    //}
    //public static RespBean error(RespBeanEnum respBeanEnum, Object obj){
    //    return new RespBean(RespBeanEnum.ERROR.getCode(), RespBeanEnum.ERROR.getMessage(), obj);
    //}
    public static RespBean error(RespBeanEnum respBeanEnum){
        return new RespBean(respBeanEnum.getCode(), respBeanEnum.getMessage(), null);
    }
    public static RespBean error(RespBeanEnum respBeanEnum, Object obj){
        return new RespBean(respBeanEnum.getCode(), respBeanEnum.getMessage(), obj);
    }

}
