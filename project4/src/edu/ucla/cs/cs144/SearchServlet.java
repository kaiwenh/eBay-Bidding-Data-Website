package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}
    static int n = 1;
    static String lastQuery = null;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here

    	AuctionSearch as = new AuctionSearch();
    	String action = null;
    	try {
    		action = request.getParameter("search");
    		if (request.getParameter("search").equals("Next")){
    			n++;
            	SearchResult[] basicResults = as.basicSearch(lastQuery, n * 20, 20);
            	if (basicResults.length == 0){
            		n--;
            		basicResults = as.basicSearch(lastQuery, n * 20, 20);
            	}		
                request.setAttribute("result", basicResults);
                request.setAttribute("number", n);
                request.setAttribute("query", lastQuery);
            }
            else if (request.getParameter("search").equals("Back")){
            	if (n > 0)
            		n--;
            	SearchResult[] basicResults = as.basicSearch(lastQuery, n * 20, 20);
                request.setAttribute("result", basicResults);
                request.setAttribute("number", n);
                request.setAttribute("query", lastQuery);

            }
            else {
            	String query = request.getParameter("query");
            	
                SearchResult[] basicResults = as.basicSearch(query, 0, 20);
                n = 0;
                lastQuery = query;
                request.setAttribute("result", basicResults);
                request.setAttribute("number", n);
                request.setAttribute("query", lastQuery);
            }
    		request.getRequestDispatcher("/search.jsp").forward(request, response);
    	} catch(Exception e) {
    		request.getRequestDispatcher("/searchIni.jsp").forward(request, response);
    	}
        
        

        
    }
}
