package controllers;

import dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by Max on 26.11.2014.
 */
@WebServlet(name = "LoginController")
public class LoginController extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String res = "";
        UserDAO userDAO = null;
        try{
            userDAO = new UserDAO(username, password);
        }catch (NullPointerException e){
            res = e.getMessage();
        }
        if(userDAO == null){
            res = "NULL";
        }else
            res = userDAO.getUser().toString();

        Writer out = response.getWriter();
        out.write("<h1> Hello,"+res );
        out.write("</h1>");



    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
