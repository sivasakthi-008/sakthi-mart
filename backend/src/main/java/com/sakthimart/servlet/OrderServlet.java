package com.sakthimart.servlet;

import com.sakthimart.dao.JdbcCartDao;
import com.sakthimart.dao.JdbcOrderDao;
import com.sakthimart.model.Cart;
import com.sakthimart.model.CartItem;
import com.sakthimart.model.Order;
import com.sakthimart.model.Product;
import com.sakthimart.model.User;
import com.sakthimart.dao.JdbcProductDao;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/api/orders")
public class OrderServlet extends HttpServlet {

    private JdbcCartDao cartDao;
    private JdbcOrderDao orderDao;
    private JdbcProductDao productDao;

    @Override
    public void init() {
        cartDao = new JdbcCartDao();
        orderDao = new JdbcOrderDao();
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

        if (session == null ||
                session.getAttribute("user") == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write(
                    "{\"error\":\"Please login first\"}"
            );
            return;
        }

        User user = (User) session.getAttribute("user");

        if (!"BUYER".equals(user.getRole())) {

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN);

            response.getWriter().write(
                    "{\"error\":\"Only buyers can place orders\"}"
            );
            return;
        }

        try {

            Cart cart =
                    cartDao.findCartByBuyerId(user.getId())
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "Cart not found"));

            List<CartItem> items =
                    cartDao.findItemsByCartId(cart.getId());

            if (items.isEmpty()) {

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST);

                response.getWriter().write(
                        "{\"error\":\"Cart is empty\"}"
                );
                return;
            }

            BigDecimal totalAmount = BigDecimal.ZERO;

            for (CartItem item : items) {

                Product product =
                        productDao.findById(item.getProductId())
                                .orElseThrow(
                                        () -> new RuntimeException(
                                                "Product not found"));

                BigDecimal itemTotal =
                        product.getPrice()
                                .multiply(
                                        BigDecimal.valueOf(
                                                item.getQuantity()));

                totalAmount =
                        totalAmount.add(itemTotal);
            }

            Order order =
                    orderDao.createOrder(
                            user.getId(),
                            totalAmount);

            for (CartItem item : items) {

                Product product =
                        productDao.findById(item.getProductId())
                                .orElseThrow(
                                        () -> new RuntimeException(
                                                "Product not found"));

                orderDao.addOrderItem(
                        order.getId(),
                        product.getId(),
                        item.getQuantity(),
                        product.getPrice()
                );

                cartDao.removeItem(
                        cart.getId(),
                        product.getId());
            }

            response.setStatus(
                    HttpServletResponse.SC_CREATED);

            response.getWriter().write(
                    "{\"message\":\"Order placed successfully\","
                            + "\"orderId\":"
                            + order.getId()
                            + ",\"totalAmount\":"
                            + totalAmount
                            + "}"
            );

        } catch (Exception e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write(
                    "{\"error\":\"Unable to place order\"}"
            );
        }
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session =
                request.getSession(false);

        if (session == null ||
                session.getAttribute("user") == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write(
                    "{\"error\":\"Please login first\"}"
            );
            return;
        }

        User user =
                (User) session.getAttribute("user");

        if (!"BUYER".equals(user.getRole())) {

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN);

            response.getWriter().write(
                    "{\"error\":\"Only buyers can view orders\"}"
            );
            return;
        }

        try {

            List<Order> orders =
                    orderDao.findOrdersByBuyerId(
                            user.getId());

            StringBuilder json =
                    new StringBuilder("[");

            for (int i = 0;
                 i < orders.size();
                 i++) {

                Order order = orders.get(i);

                json.append("{")
                        .append("\"id\":")
                        .append(order.getId())
                        .append(",")

                        .append("\"totalAmount\":")
                        .append(order.getTotalAmount())
                        .append(",")

                        .append("\"status\":\"")
                        .append(order.getStatus())
                        .append("\"")
                        .append("}");

                if (i < orders.size() - 1) {
                    json.append(",");
                }
            }

            json.append("]");

            response.setStatus(
                    HttpServletResponse.SC_OK);

            response.getWriter().write(
                    json.toString());

        } catch (Exception e) {

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            response.getWriter().write(
                    "{\"error\":\"Unable to fetch orders\"}"
            );
        }
    }
}