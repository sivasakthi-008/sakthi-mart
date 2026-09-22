package com.sakthimart.servlet;

import com.sakthimart.dao.JdbcUserDao;
import com.sakthimart.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/auth/register")
public class RegisterServlet extends HttpServlet {

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

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        try {
            authService.register(name, email, password, role);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(
                    "{\"message\":\"Registration successful\"}"
            );

        } catch (IllegalArgumentException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                    "{\"error\":\"" + e.getMessage() + "\"}"
            );
        }
    }
}