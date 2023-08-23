package com.lee.seckill.controller;

import com.lee.seckill.pojo.User;

import com.lee.seckill.service.IGoodsService;
import com.lee.seckill.service.IUserService;
import com.lee.seckill.vo.GoodsVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @description: 商品相关
 */

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * @description: 跳转到商品列表页面
     * MacM1 优化前 QPS：1592
     * Linux 优化前 QPS：902
     *
     * @param:
     * @return:
     */

    @RequestMapping("/toList")
    //@RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    //@ResponseBody
    public String toList(Model model, User user,HttpServletRequest request, HttpServletResponse response){
        // Redis中获取页面，如果不为空，则直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodVo());
        return "goodsList";

        // 但是如果Redis获取页面为空的话，则手动渲染，并且存入Redis中并返回。
        //WebContext context = new WebContext(request, response);
        //html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);

        // 并将手动渲染好的页面放置Redis缓存中
        //if(!StringUtils.isEmpty(html)){
        //    valueOperations.set("goodsList",html,60, TimeUnit.SECONDS);
        //}
        //
        //return html;
    }

    /**
     * @description: 跳转商品详情页面
     *
     * @param:
     * @return:
     */
    @RequestMapping("/toDetail/{goodsId}")
    public String toDetail(Model model, User user, @PathVariable Long goodsId){
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();

        // 秒杀状态码
        int secKillStatus = 0;
        // 秒杀倒计时
        int remainSeconds = 0;

        // 秒杀未开始（包含倒计时）
        if(nowDate.before(startDate)){
            remainSeconds = (int)((startDate.getTime() - nowDate.getTime())/1000);
        }
        // 秒杀已结束
        else if(nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        }
        // 秒杀进行中
        else{
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("goods", goodsVo);
        return "goodsDetail";
    }
}
