package com.lee.seckill.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.seckill.pojo.User;
import com.lee.seckill.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description: 生成多用户的工具类
 */
public class UserUtil {
    private static void createUser(int count) throws Exception {
        List<User> users = new ArrayList<User>(count);
        for(int i=0; i<count; i++){
            User user = new User();
            user.setId(13000000000L + i);
            user.setNickname("user"+i);
            user.setSalt("1a2b3c");
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            user.setLoginCount(1);
            user.setRegisterDate(new Date());
            users.add(user);
        }
        System.out.println("create user");

        // 插入数据库

        // 获取连接池
        Connection conn = getConn();
        String sql = "insert into t_user(login_count, nickname, register_date, salt, password, id) values(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for(int i=0; i<users.size(); i++){
            User user = users.get(i);
            pstmt.setInt(1, user.getLoginCount());
            pstmt.setString(2, user.getNickname());
            pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
            pstmt.setString(4, user.getSalt());
            pstmt.setString(5, user.getPassword());
            pstmt.setLong(6, user.getId());
            pstmt.addBatch();
        }
        // 执行sql语句，插入数据库
        pstmt.executeBatch();
        pstmt.close();
        conn.close();
        System.out.println("insert to db");

        // 文件操作
        String urlString = "http://localhost:8080/login/doLogin";
        File file  =new File("/Users/lisen/Desktop/config.txt");
        if (file.exists()) {
            file.delete();
        }
        // 登录，生成token（cookie）
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for(int i=0; i<users.size(); i++){
            User user = users.get(i);
            // 根据生成的用户进行模拟登录，从而获取相应cookie，并写入文件。
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToFormPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            // 获取cookie
            String userTicket = ((String) respBean.getObj());
            System.out.println("create userTicket : " + user.getId());
            String row = user.getId() + "," + userTicket;
            // 将相应的数据写入文件
            raf.seek(raf.length()); // seek()方法是移动文件读写指针至相应的位置
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());
        }
        raf.close();
        System.out.println("over");
    }
    // 获取连接池函数
    private static Connection getConn() throws Exception {
        String url = "jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "111111";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }
    public static void main(String[] args) throws Exception{
        createUser(5000);
    }
}
