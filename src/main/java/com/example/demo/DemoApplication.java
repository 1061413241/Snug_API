package com.example.demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.apache.catalina.User;

//import org.json.JSONArray;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

@RestController
@SpringBootApplication
public class DemoApplication extends SpringBootServletInitializer{

    /**
     * 注册API;自动分配系统任务
     * @param jsonParam
     * @return
     * @throws JSONException
     */
    @ResponseBody
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String register(@RequestBody JSONObject jsonParam) throws JSONException, ClassNotFoundException, SQLException {

        String name=jsonParam.getString("name");
        String telephone=jsonParam.getString("telephone");
        String password=jsonParam.getString("password");

        // 连接数据库
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://47.95.240.12/test?characterEncoding=utf8&useSSL=false&serverTimezone=GMT";
        Connection con = DriverManager.getConnection(url, "hhh", "123");
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from User_info where User_phone_num='"+telephone+"'");
//        int rowCount=resultSet.getRow();
//        System.out.println(rowCount);
        JSONObject result = new JSONObject();
        if(resultSet.next())
        {
            result.put("msg", "false");
        }
        else
        {
//            Connection con2 = DriverManager.getConnection(url, "hhh", "123");
//            Statement statement2 = con2.createStatement();
            int row=statement.executeUpdate("INSERT INTO User_info(User_name,User_password,User_phone_num) VALUES('"+name+"','"+password+"','"+telephone+"')");
            if(row==1)
            {
                result.put("msg","ok");
                ResultSet resultSet2 = statement.executeQuery("select * from User_info where User_phone_num='"+telephone+"'");
                resultSet2.next();
                int userId=resultSet2.getInt("User_id");
                for(int i=1;i<=13;i++)
                {
                    row=statement.executeUpdate("INSERT INTO User_Main_Task(User_Task_id,Task_Task_Id) VALUES("+userId+","+i+")");
                }
            }
            else
            {
                result.put("msg","false");
            }
        }
        return result.toJSONString();
    }


    /**
     * 登录API
     * @param jsonParam
     * @return
     * @throws JSONException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String login(@RequestBody JSONObject jsonParam) throws JSONException, ClassNotFoundException, SQLException {

        String telephone=jsonParam.getString("telephone");
        String password=jsonParam.getString("password");

        // 连接数据库
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://47.95.240.12/test?characterEncoding=utf8&useSSL=false&serverTimezone=GMT";
        Connection con = DriverManager.getConnection(url, "hhh", "123");
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from User_info where User_phone_num="+telephone);
//        int rowCount=resultSet.getRow();
//        System.out.println(rowCount);
        JSONObject result = new JSONObject();
        if(resultSet.next())
        {
            if(password.equals(resultSet.getString("User_password")))
            {
                result.put("msg","ok");
                JSONObject jp=new JSONObject();
                jp.put("id",resultSet.getInt("User_id"));
                jp.put("name",resultSet.getString("User_name"));
                jp.put("telephone",resultSet.getString("User_phone_num"));
                result.put("data",jp);
            }
            else
            {
                result.put("msg", "false");
            }
        }
        else
        {
            result.put("msg", "false");
        }
        return result.toJSONString();
    }

    /**
     * 查看用户信息API
     * @param jsonParam
     * @return
     * @throws JSONException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @ResponseBody
    @RequestMapping(value = "/userInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String userInfo(@RequestBody JSONObject jsonParam) throws JSONException, ClassNotFoundException, SQLException {

        int userId=jsonParam.getInteger("userId");

        // 连接数据库
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://47.95.240.12/test?characterEncoding=utf8&useSSL=false&serverTimezone=GMT";
        Connection con = DriverManager.getConnection(url, "hhh", "123");
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from User_info where User_id="+userId);
        JSONObject result = new JSONObject();
        if(resultSet.next())
        {
            result.put("msg","ok");
            JSONObject jp=new JSONObject();
            jp.put("id",resultSet.getInt("User_id"));
            jp.put("name",resultSet.getString("User_name"));
            jp.put("telephone",resultSet.getString("User_phone_num"));
            result.put("data",jp);
        }
        else
        {
            result.put("msg", "false");
        }
        return result.toJSONString();
    }

    /**
     * 添加用户自定义任务API
     * @param jsonParam
     * @return
     * @throws JSONException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @ResponseBody
    @RequestMapping(value = "/addTask", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String addTask(@RequestBody JSONObject jsonParam) throws JSONException, ClassNotFoundException, SQLException {

        String info=jsonParam.getString("info");
        String start=jsonParam.getString("start");
        String end=jsonParam.getString("end");
        int userId=jsonParam.getInteger("userId");

        // 连接数据库
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://47.95.240.12/test?characterEncoding=utf8&useSSL=false&serverTimezone=GMT";
        Connection con = DriverManager.getConnection(url, "hhh", "123");
        Statement statement = con.createStatement();
        int row=statement.executeUpdate("INSERT INTO User_Task(U_Task_Info,U_Task_start,U_Task_end,User_Id) VALUES('"+info+"','"+start+"','"+end+"',"+userId+")");
        JSONObject result = new JSONObject();
        if(row==1)
        {
            result.put("msg","ok");
        }
        else
        {
            result.put("msg","false");
        }

        return result.toJSONString();
    }

    /**
     * 查看所有任务API
     * @param jsonParam
     * @return
     * @throws JSONException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @ResponseBody
    @RequestMapping(value = "/viewTask", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String viewTask(@RequestBody JSONObject jsonParam) throws JSONException, ClassNotFoundException, SQLException {

        int userId=jsonParam.getInteger("userId");
        java.sql.Date wantDate=java.sql.Date.valueOf(jsonParam.getString("date").substring(0,10));

        // 连接数据库
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://47.95.240.12/test?characterEncoding=utf8&useSSL=false&serverTimezone=GMT";
        Connection con = DriverManager.getConnection(url, "hhh", "123");
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM User_Task WHERE User_Id="+userId+" ORDER BY U_Task_start ASC");
        JSONObject result = new JSONObject();
        JSONArray taskArr=new JSONArray();
        int i=0;
        while(resultSet.next())
        {
            if(i==0)
            {
                result.put("userId",resultSet.getInt("User_id"));
            }
            Date start=java.sql.Date.valueOf(resultSet.getString("U_Task_start").substring(0,10));
            Date end=java.sql.Date.valueOf(resultSet.getString("U_Task_end").substring(0,10));
            if((start.before(wantDate)||start.equals(wantDate))&&(wantDate.before(end)||wantDate.equals(end)))
            {
                JSONObject jo=new JSONObject();
                jo.put("taskId",resultSet.getInt("U_Task_Id"));
                jo.put("taskFlag",resultSet.getString("Task_Flag"));//任务类别：系统->0/自定义->1
                jo.put("taskInfo",resultSet.getString("U_Task_Info"));
                jo.put("start",resultSet.getString("U_Task_start"));
                jo.put("end",resultSet.getString("U_Task_end"));
                jo.put("status",resultSet.getBoolean("U_Task_status"));
                jo.put("score",resultSet.getInt("U_Task_score"));

                taskArr.add(i,jo);
                i++;
            }
            else
            {
                continue;
            }
        }
        result.put("msg","ok");//无条件ok？
        result.put("data",taskArr);

        return result.toJSONString();
    }

    /**
     * 查看旅程任务API
     * @param jsonParam
     * @return
     * @throws JSONException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @ResponseBody
    @RequestMapping(value = "/viewSysTask", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String viewSysTask(@RequestBody JSONObject jsonParam) throws JSONException, ClassNotFoundException, SQLException {

        String cata1="健康的饮食";
        String cata2="美妙的夜晚";
        String cata3="专注和集中";
        String cata4="精力更充沛";
        int userId=jsonParam.getInteger("userId");
        String catagory=jsonParam.getString("catagory");

        // 连接数据库
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://47.95.240.12/test?characterEncoding=utf8&useSSL=false&serverTimezone=GMT";
        Connection con = DriverManager.getConnection(url, "hhh", "123");
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM User_Task WHERE User_Id="+userId+" ORDER BY U_Task_start ASC");
        JSONObject result = new JSONObject();
        JSONArray taskArr=new JSONArray();
        int i=0;
        while(resultSet.next())
        {
            if(i==0)
            {
                result.put("userId",resultSet.getInt("User_id"));
            }
            Date start=java.sql.Date.valueOf(resultSet.getString("U_Task_start").substring(0,10));
            Date end=java.sql.Date.valueOf(resultSet.getString("U_Task_end").substring(0,10));
            if((start.before(wantDate)||start.equals(wantDate))&&(wantDate.before(end)||wantDate.equals(end)))
            {
                JSONObject jo=new JSONObject();
                jo.put("taskId",resultSet.getInt("U_Task_Id"));
                jo.put("taskFlag",resultSet.getString("Task_Flag"));
                jo.put("taskInfo",resultSet.getString("U_Task_Info"));
                jo.put("start",resultSet.getString("U_Task_start"));
                jo.put("end",resultSet.getString("U_Task_end"));
                jo.put("status",resultSet.getBoolean("U_Task_status"));
                jo.put("score",resultSet.getInt("U_Task_score"));

                taskArr.add(i,jo);
                i++;
            }
            else
            {
                continue;
            }
        }
        result.put("msg","ok");//无条件正确？
        result.put("data",taskArr);

        return result.toJSONString();
    }

    /**
     * 测试API
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws JSONException
     */
    @ResponseBody
    @RequestMapping(value = "/db", produces = "application/json;charset=UTF-8")
    public String db() throws ClassNotFoundException, SQLException, JSONException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://47.95.240.12/test?characterEncoding=utf8&useSSL=false&serverTimezone=GMT";
        Connection con = DriverManager.getConnection(url, "hhh", "123");
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from User_info");

        JSONObject result = new JSONObject();
        JSONArray arr=new JSONArray();
        while (resultSet.next()) {
//            System.out.print("查询结果： "+resultSet.getString("User_name"));
//            System.out.println("密码： "+resultSet.getString("User_password"));
//            return resultSet.getString("User_name");
//            return resultSet.getString(2);
            JSONObject jsonParam=new JSONObject();
            jsonParam.put("name",resultSet.getString("User_name"));
            jsonParam.put("password",resultSet.getString("User_password"));
            result.put("msg", "ok");
            result.put("method", "@ResponseBody");
            result.put("data", jsonParam);
//            arr.put(result);
            arr.add(result);
        }

        return arr.toString();
    }


    
    @RequestMapping(value = "/hello")
    public String hello(){
        return "hello world!";
    }

    @RequestMapping(value = "/")
    public String bigOne(){
        return "hello bigOne!";
    }

//    @RequestMapping(value = "/getuser")
//    public User getuser(){
//        User user=new User();
//        user.setUsername("xiaoming");
//        user.setPassword("jkjk");
//        return user;
//    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
