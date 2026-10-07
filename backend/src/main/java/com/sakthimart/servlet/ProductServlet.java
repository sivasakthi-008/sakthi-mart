package com.sakthimart.servlet;

import com.sakthimart.dao.JdbcProductDao;
import com.sakthimart.dao.ProductDao;
import com.sakthimart.model.Product;
import com.sakthimart.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/api/products")
public class ProductServlet extends HttpServlet {

    private ProductDao productDao;

    @Override
    public void init() throws ServletException {
        productDao = new JdbcProductDao();
    }

    // SELLER - Create Product
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
            User user = (User) session.getAttribute("user");

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

    // BUYER - View All Products
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Product> products;

HttpSession session = request.getSession(false);

if (session != null && session.getAttribute("user") != null
        && "mine".equals(request.getParameter("view"))) {

    User user = (User) session.getAttribute("user");
    products = productDao.findBySellerId(user.getId());

} else {
    products = productDao.findAll();
}

            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < products.size(); i++) {

                Product product = products.get(i);

                json.append("{")
                        .append("\"id\":")
                        .append(product.getId())
                        .append(",")

                        .append("\"name\":\"")
                        .append(product.getName())
                        .append("\",")

                        .append("\"description\":\"")
                        .append(product.getDescription())
                        .append("\",")

                        .append("\"price\":")
                        .append(product.getPrice())
                        .append(",")

                        .append("\"stock\":")
                        .append(product.getStock())
                        .append(",")

                        .append("\"category\":\"")
                        .append(product.getCategory())
                        .append("\",")

                        .append("\"imageUrl\":\"")
                        .append(product.getImageUrl())
                        .append("\"")

                        .append("}");

                if (i < products.size() - 1) {
                    json.append(",");
                }
            }

            json.append("]");

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(json.toString());

        } catch (Exception e) {

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().write(
                    "{\"error\":\"Unable to fetch products\"}"
            );
        }
    }
// SELLER - Update Product
@Override
protected void doPut(
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
        User user = (User) session.getAttribute("user");

        if (!"SELLER".equals(user.getRole())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(
                    "{\"error\":\"Only sellers can update products\"}"
            );
            return;
        }

        Long productId =
                Long.parseLong(request.getParameter("id"));

        Product existing =
                productDao.findById(productId).orElseThrow();

        if (!existing.getSellerId().equals(user.getId())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(
                    "{\"error\":\"You can update only your products\"}"
            );
            return;
        }

        Product product = new Product(
                productId,
                user.getId(),
                request.getParameter("name"),
                request.getParameter("description"),
                new BigDecimal(request.getParameter("price")),
                Integer.parseInt(request.getParameter("stock")),
                request.getParameter("category"),
                request.getParameter("imageUrl")
        );

        productDao.update(product);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(
                "{\"message\":\"Product updated successfully\"}"
        );

    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(
                "{\"error\":\"Unable to update product\"}"
        );
    }
}


// SELLER - Delete Product
@Override
protected void doDelete(
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
        User user = (User) session.getAttribute("user");

        if (!"SELLER".equals(user.getRole())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(
                    "{\"error\":\"Only sellers can delete products\"}"
            );
            return;
        }

        Long productId =
                Long.parseLong(request.getParameter("id"));

        Product existing =
                productDao.findById(productId).orElseThrow();

        if (!existing.getSellerId().equals(user.getId())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(
                    "{\"error\":\"You can delete only your products\"}"
            );
            return;
        }

        productDao.delete(productId);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(
                "{\"message\":\"Product deleted successfully\"}"
        );

    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(
                "{\"error\":\"Unable to delete product\"}"
        );
    }
}
}