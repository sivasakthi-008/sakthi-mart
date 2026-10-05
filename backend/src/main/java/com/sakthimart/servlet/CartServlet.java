package com.sakthimart.servlet;

import com.sakthimart.dao.CartDao;
import com.sakthimart.dao.JdbcCartDao;
import com.sakthimart.model.Cart;
import com.sakthimart.model.CartItem;
import com.sakthimart.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/cart")
public class CartServlet extends HttpServlet {

    private CartDao cartDao;

    @Override
    public void init() {
        cartDao = new JdbcCartDao();
    }

    private User getLoggedInUser(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(false);

        if (session == null) {
            return null;
        }

        return (User) session.getAttribute("user");
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        User user = getLoggedInUser(request);

        if (user == null) {
            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write(
                    "{\"error\":\"Please login first\"}"
            );
            return;
        }

        if (!"BUYER".equals(user.getRole())) {
            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN);

            response.getWriter().write(
                    "{\"error\":\"Only buyers can use cart\"}"
            );
            return;
        }

        try {
            Long productId =
                    Long.parseLong(
                            request.getParameter("productId"));

            int quantity =
                    Integer.parseInt(
                            request.getParameter("quantity"));

            if (quantity <= 0) {
                throw new IllegalArgumentException();
            }

            Cart cart =
                    cartDao.findCartByBuyerId(user.getId())
                            .orElseGet(
                                    () -> cartDao.createCart(
                                            user.getId()));

            cartDao.addItem(
                    cart.getId(),
                    productId,
                    quantity
            );

            response.setStatus(
                    HttpServletResponse.SC_OK);

            response.getWriter().write(
                    "{\"message\":\"Product added to cart\"}"
            );

        } catch (Exception e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write(
                    "{\"error\":\"Unable to add product to cart\"}"
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

        User user = getLoggedInUser(request);

        if (user == null) {
            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write(
                    "{\"error\":\"Please login first\"}"
            );
            return;
        }

        try {
            Cart cart =
                    cartDao.findCartByBuyerId(user.getId())
                            .orElse(null);

            if (cart == null) {
                response.getWriter().write("[]");
                return;
            }

            List<CartItem> items =
                    cartDao.findItemsByCartId(cart.getId());

            StringBuilder json =
                    new StringBuilder("[");

            for (int i = 0; i < items.size(); i++) {

                CartItem item = items.get(i);

                json.append("{")
                        .append("\"id\":")
                        .append(item.getId())
                        .append(",")

                        .append("\"productId\":")
                        .append(item.getProductId())
                        .append(",")

                        .append("\"quantity\":")
                        .append(item.getQuantity())

                        .append("}");

                if (i < items.size() - 1) {
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
                    "{\"error\":\"Unable to fetch cart\"}"
            );
        }
    }
    @Override
protected void doPut(
        HttpServletRequest request,
        HttpServletResponse response)
        throws IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    User user = getLoggedInUser(request);

    if (user == null) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(
                "{\"error\":\"Please login first\"}"
        );
        return;
    }

    if (!"BUYER".equals(user.getRole())) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(
                "{\"error\":\"Only buyers can update cart\"}"
        );
        return;
    }

    try {
        Long productId =
                Long.parseLong(request.getParameter("productId"));

        int quantity =
                Integer.parseInt(request.getParameter("quantity"));

        Cart cart =
                cartDao.findCartByBuyerId(user.getId())
                        .orElseThrow();

        if (quantity <= 0) {
            cartDao.removeItem(cart.getId(), productId);
        } else {
            cartDao.updateItemQuantity(
                    cart.getId(),
                    productId,
                    quantity
            );
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(
                "{\"message\":\"Cart updated successfully\"}"
        );

    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(
                "{\"error\":\"Unable to update cart\"}"
        );
    }
}
@Override
protected void doDelete(
        HttpServletRequest request,
        HttpServletResponse response)
        throws IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    User user = getLoggedInUser(request);

    if (user == null) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(
                "{\"error\":\"Please login first\"}"
        );
        return;
    }

    if (!"BUYER".equals(user.getRole())) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(
                "{\"error\":\"Only buyers can use cart\"}"
        );
        return;
    }

    try {
        Long productId =
                Long.parseLong(request.getParameter("productId"));

        Cart cart =
                cartDao.findCartByBuyerId(user.getId())
                        .orElseThrow();

        cartDao.removeItem(
                cart.getId(),
                productId
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(
                "{\"message\":\"Product removed from cart\"}"
        );

    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(
                "{\"error\":\"Unable to remove product from cart\"}"
        );
    }
}
}