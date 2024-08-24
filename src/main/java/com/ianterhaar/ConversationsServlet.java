package com.ianterhaar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/conversations")
public class ConversationsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            response.setContentType("text/html");
            response.getWriter().println("<h1>Conversations</h1>");
            response.getWriter().println("<textarea id='chat' readonly></textarea><br>");
            response.getWriter().println("<input type='text' id='message'><button onclick='sendMessage()'>Send</button>");
            response.getWriter().println("<script src='chat.js'></script>");
        } else {
            response.sendRedirect("login.html");
        }
    }
}
