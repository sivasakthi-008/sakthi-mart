package com.sakthimart.servlet;

import com.sakthimart.dao.JdbcUserDao;
import com.sakthimart.model.User;
import com.sakthimart.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        authService = new AuthService(new JdbcUserDao());
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = authService.login(email, password);

            HttpSession session = request.getSession(true);

request.changeSessionId();

session.setAttribute("user", user);
session.setMaxInactiveInterval(30 * 60);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(
                    "{\"message\":\"Login successful\",\"role\":\""
                            + user.getRole() + "\"}"
            );

        } catch (IllegalArgumentException e) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                    "{\"error\":\"Invalid email or password\"}"
            );
        }
    }
}