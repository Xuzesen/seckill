package com.leo.seckill.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leo.seckill.pojo.TUser;
import com.leo.seckill.vo.RespBean;

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
 * 生成用户工具类
 * @author Leo
 * @create 2021-08-13 15:28
 */
public class UserUtil {

    private static void createUser(int count) throws Exception {
        List<TUser> tUsers = new ArrayList<>(count);
        for(int i=0;i<count;i++){
            TUser tUser = new TUser();
            tUser.setId(13000000000L+i);
            tUser.setLoginCount(1);
            tUser.setNickname("tUser"+i);
            tUser.setRegisterDate(new Date());
            tUser.setSalt("1a2b3c4d");
            tUser.setPassword(MD5Utils.inputPassToDBPass("123456",tUser.getSalt()));
            tUsers.add(tUser);
        }
        System.out.println("创建用户");
        //插入数据库
        Connection conn = getConn();
        String sql = "insert into t_user(login_count, nickname, register_date, salt, password, id)values(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for(int i=0;i<tUsers.size();i++){
            TUser tUser = tUsers.get(i);
            pstmt.setInt(1, tUser.getLoginCount());
            pstmt.setString(2, tUser.getNickname());
            pstmt.setTimestamp(3, new Timestamp(tUser.getRegisterDate().getTime()));
            pstmt.setString(4, tUser.getSalt());
            pstmt.setString(5, tUser.getPassword());
            pstmt.setLong(6, tUser.getId());
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        pstmt.close();
        conn.close();
        System.out.println("插入数据库");

        //登录，生成userTicket
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("C:\\Users\\asus\\Desktop\\config.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for(int i = 0;i<tUsers.size();i++){
           TUser tUser = tUsers.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
           String params = "mobile=" + tUser.getId() + "&password=" + MD5Utils.inputPassToFromPass("123456");
           out.write(params.getBytes());
           out.flush();
           InputStream inputStream = co.getInputStream();
           ByteArrayOutputStream bout = new ByteArrayOutputStream();
           byte[] buff = new byte[1024];
           int len = 0;
           while ((len = inputStream.read(buff)) >= 0) {
               bout.write(buff, 0, len);
           }
           inputStream.close();
           bout.close();
           String response = new String(bout.toByteArray());
           ObjectMapper mapper = new ObjectMapper();
           RespBean respBean = mapper.readValue(response, RespBean.class);
           String userTicket = (String) respBean.getObj();
           System.out.println("创建 userTicket:" + tUser.getId());

           String row = tUser.getId()+","+userTicket;
           raf.seek(raf.length());
           raf.write(row.getBytes());
           raf.write("\r\n".getBytes());
           System.out.println("写入文件:"+tUser.getId());
        }
        raf.close();
        System.out.println("结束");
    }

    private static Connection getConn() throws Exception {
        String url = "jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "13480303758a";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) throws Exception {
        createUser(500);
    }

}
