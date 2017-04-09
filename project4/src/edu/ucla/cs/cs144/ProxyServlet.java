package edu.ucla.cs.cs144;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyServlet extends HttpServlet implements Servlet {
       
    public ProxyServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here
    	response.setContentType("text/xml");
    	String query = request.getParameter("query");
    	String q = String.format("%s", URLEncoder.encode(query, "UTF-8"));
    	String urlStr = "http://google.com/complete/search?output=toolbar&q=" + q;
    	
    	URL url = new URL(urlStr);
    	HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty("Content-Type", "text/xml");     
        connection.setRequestProperty("Charset", "UTF-8");

        
        InputStream stream = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(stream); 
        BufferedReader br = new BufferedReader(reader);
        
        StringBuffer result = new StringBuffer();
        String line = null;
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        
        PrintWriter out = response.getWriter();
        out.println(result.toString());
        
        br.close();
        connection.disconnect();
    	
    }
}
