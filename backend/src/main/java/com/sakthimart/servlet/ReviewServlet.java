package com.sakthimart.servlet;

import com.sakthimart.config.DatabaseConfig;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/api/reviews")
public class ReviewServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

User user = null;

if (session != null &&
        session.getAttribute("user") instanceof User) {
    user = (User) session.getAttribute("user");
}

if (user == null || user.getId() == null) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write(
        "{\"error\":\"Please login to submit a review.\"}"
    );
    return;
}

        try {
            long userId = user.getId();

            int productId = Integer.parseInt(
                request.getParameter("productId")
            );

            int rating = Integer.parseInt(
                request.getParameter("rating")
            );

            String comment = request.getParameter("comment");

            if (rating < 1 || rating > 5 ||
                    comment == null || comment.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    "{\"error\":\"Enter a rating from 1 to 5 and a review.\"}"
                );
                return;
            }

            try (Connection connection = DatabaseConfig.getDataSource().getConnection()) {
                String sql = "INSERT INTO reviews "
                        + + "(product_id, user_id, rating, review_text) "
                        + "VALUES (?, ?, ?, ?)";

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setInt(1, productId);
                    statement.setLong(2, userId);
                    statement.setInt(3, rating);
                    statement.setString(4, comment.trim());

                    statement.executeUpdate();
                }
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(
                "{\"message\":\"Review submitted successfully.\"}"
            );

        } catch (NumberFormatException | SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                "{\"error\":\"Unable to save review.\"}"
            );
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                "{\"error\":\"Invalid review details.\"}"
            );
        }
    }
}