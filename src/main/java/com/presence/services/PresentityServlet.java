/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.presence.services;

import com.presence.dao.DAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;

/**
 *
 * @author chauh
 */
public class PresentityServlet extends HttpServlet {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PresentityServlet.class);

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug(request.getParameter("filter"));
        logger.debug(request.getParameter("keys"));
        try {
            long start = System.nanoTime();
            String key = request.getParameter("keys");
            String filter = request.getParameter("filter");
            
            String outputJson = DAO.select(key, filter, "presentity");
            //logger.debug("Total time: {}",(float)(System.nanoTime() - start)/1000000);
            PrintWriter out = response.getWriter();
            
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(outputJson);
            out.flush();
            //logger.debug("Total time: {}",(float)(System.nanoTime() - start)/1000000);
        } catch (SQLException ex) {
            logger.error("Error while sending request to database", ex);
            response.sendError(500, "Server was unable to process the request.");
            //response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long start = System.nanoTime();
        try {
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = request.getReader().readLine()) != null) {
                sb.append(s);
            }
            
            int status = DAO.insert(sb.toString(), "presentity");
            if (status > 0) {
                response.setStatus(HttpServletResponse.SC_CREATED);
            }
         //   logger.debug("Total time: {}",(float)(System.nanoTime() - start)/1000000);
        } catch (SQLException ex) {
            logger.error("Error while sending request to database", ex);
            response.sendError(500, "Server was unable to process the request.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doDelete(request, response); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //super.doPut(request, response); //To change body of generated methods, choose Tools | Templates.
      long start = System.nanoTime();
        try {  
            
       //     logger.debug(request.getParameter("filter"));
            StringBuilder postData = new StringBuilder();
            String s;
            while ((s = request.getReader().readLine()) != null) {
                postData.append(s);
            }
            int status = DAO.update(postData.toString(), request.getParameter("filter"), "presentity");
     //       logger.debug("Return status {}.", status);
            /*RFC2616 If an existing resource is modified, either the 200 (OK)
            or 204 (No Content) response codes SHOULD be sent to indicate successful
            completion of the request. */
            if (status == 0) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
            }
           //   logger.debug("Total time: {}",(float)(System.nanoTime() - start)/1000000);
        } catch (SQLException ex) {
            logger.error("Error while sending request to database", ex);
            response.sendError(500, "Server was unable to process the request.");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
