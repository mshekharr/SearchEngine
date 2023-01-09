package com.Accio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;

@WebServlet("/Search")
public class Search extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        String keyword = request.getParameter("keyword");
        System.out.println(keyword);
        try{
            Connection connection = DatabaseConnection.getConnection();
            //add keyword into history table
            PreparedStatement preparedStatement = connection.prepareStatement("Insert into history values(?,?)");
            preparedStatement.setString(1,keyword);
            preparedStatement.setString(2,"http://localhost:8080/SearchEngineAccio/Search?keyword="+keyword);
            preparedStatement.executeUpdate();

            //get result from pages table
            ResultSet resultSet = connection.createStatement().executeQuery("select pageTitle, pageLink, (length(lower(pageData))-length(replace(lower(pageData), '"+keyword+"',\"\")))/length('"+keyword+"') as countoccurence from pages order by countoccurence desc;");

            //store resultSet inside ArrayList
            ArrayList<SearchResult> results = new ArrayList<SearchResult>();
            while(resultSet.next()) {
                SearchResult searchResult = new SearchResult();
                searchResult.setPageTitle(resultSet.getString("pageTitle"));
                searchResult.setPageLink(resultSet.getString("pageLink"));
                results.add(searchResult);
            }

            //send result to search.jsp(frontend)
            request.setAttribute("results",results);   // setting results to results attribute
            request.getRequestDispatcher("/search.jsp").forward(request,response);  // sending this to search.jsp

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
        }
        catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        catch (IOException ioException){
            ioException.printStackTrace();
        }
        catch (ServletException servletException){
            servletException.printStackTrace();
        }
    }
}
