package com.sakthimart.servlet;

import com.sakthimart.dao.JdbcProductDao;
import com.sakthimart.dao.ProductDao;
import com.sakthimart.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/api/products")
public class ProductServlet extends HttpServlet {

    private ProductDao productDao;

    @Override
    public void init() throws ServletException {
        productDao = new JdbcProductDao();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                    "{\"error\":\"Authentication required\"}"
            );
            return;
        }

        try {
            com.sakthimart.model.User user =
        (com.sakthimart.model.User)
                session.getAttribute("user");

if (!"SELLER".equals(user.getRole())) {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.getWriter().write(
            "{\"error\":\"Only sellers can create products\"}"
    );
    return;
}

Long sellerId = user.getId();

            String name = request.getParameter("name");
            String description = request.getParameter("description");
            BigDecimal price =
                    new BigDecimal(request.getParameter("price"));
            int stock =
                    Integer.parseInt(request.getParameter("stock"));
            String category = request.getParameter("category");
            String imageUrl = request.getParameter("imageUrl");

            Product product = new Product(
                    null,
                    sellerId,
                    name,
                    description,
                    price,
                    stock,
                    category,
                    imageUrl
            );

            productDao.save(product);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(
                    "{\"message\":\"Product created successfully\"}"
            );

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                    "{\"error\":\"Invalid product details\"}"
            );
        }
    }
}