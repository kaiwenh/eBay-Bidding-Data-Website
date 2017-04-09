<%@page import="java.util.ArrayList"%>
<html>
    <head>
    <title>eBay Search</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" /> 
    <style type="text/css"> 
      html { height: 100% } 
      body { height: 100%; margin: 0px; padding: 0px; } 
      #map_canvas { height: 100% } 
    </style> 
    <script type="text/javascript" 
        src="http://maps.google.com/maps/api/js?sensor=false"> 
    </script> 
    <script type="text/javascript"> 
      function initialize(x, y) { 
        var latlng = new google.maps.LatLng(x, y); 
        var myOptions = { 
          zoom: 14, // default is 8  
          center: latlng, 
          mapTypeId: google.maps.MapTypeId.ROADMAP 
        }; 
        var map = new google.maps.Map(document.getElementById("map_canvas"), 
            myOptions); 
      } 

    </script> 
    </head>

    <% String latitude = (String)request.getAttribute("latitude"); %>
    <% String longitude = (String)request.getAttribute("longitude"); %>
    <% ArrayList<String> category = (ArrayList<String>)request.getAttribute("category"); %>
    <% String buyPrice = (String) request.getAttribute("buyPrice"); %>
    <% ArrayList<String> bidderID = (ArrayList<String>)request.getAttribute("bidderID"); %>
    <% ArrayList<String> bidderRating = (ArrayList<String>)request.getAttribute("bidderRating"); %>
    <% ArrayList<String> bidderCountry = (ArrayList<String>)request.getAttribute("bidderCountry"); %>
    <% ArrayList<String> bidderLatitude = (ArrayList<String>)request.getAttribute("bidderLatitude"); %>
    <% ArrayList<String> bidderLongitude = (ArrayList<String>)request.getAttribute("bidderLongitude"); %>
    <% ArrayList<String> bidderLocationName = (ArrayList<String>)request.getAttribute("bidderLocationName"); %>
    <% ArrayList<String> time = (ArrayList<String>)request.getAttribute("time"); %>
    <% ArrayList<String> amount = (ArrayList<String>)request.getAttribute("amount"); %>


    <body onload="initialize('<%= latitude %>', '<%= longitude %>')"> 

	<h1>eBay Search Web Site</h1>

	<form action="search" method="get">
	<input type="submit" value="Reset">
	</form>

	<table border=1 align='left' width=60%>

    <tr>
    <td> name </td>
    <td> <%= request.getAttribute("name") %></td>
    </tr>

    <tr>
    <td> category </td>
    <td> 
    
   	<% for (int i=0; i<category.size(); i++) { %>

   		<%= category.get(i) %><br>

    <% } %>
    </td>
    </tr>

    <tr>
    <td> currently </td>
    <td> $<%= request.getAttribute("currently") %></td>
    </tr>

    <% if (buyPrice != null){ %>
    <tr>
    <td> buyPrice </td>
    <td> $<%= buyPrice %></td>
    </tr>
    <% } %>
    

    <tr>
    <td> firstBid </td>
    <td> $<%= request.getAttribute("firstBid") %></td>
    </tr>

    <tr>
    <td> numOfBids </td>
    <% String numOfBids = (String)request.getAttribute("numOfBids"); %>
    <td> <%= numOfBids %></td>
    </tr>

    <tr>
    <td> locationName </td>
    <td> <%= request.getAttribute("locationName") %></td>
    </tr>

    <tr>
    <td> country </td>
    <td> <%= request.getAttribute("country") %></td>
    </tr>

    <tr>
    <td> start </td>
    <td> <%= request.getAttribute("start") %></td>
    </tr>

    <tr>
    <td> end </td>
    <td> <%= request.getAttribute("end") %></td>
    </tr>

    <% for (int i=0; i<Integer.parseInt(numOfBids); i++) { %>
    <tr>
    <td> Bid <%= i %> </td>
    <td> 
    
   		bidderID: <%= bidderID.get(i) %><br>
   		bidderRating: <%= bidderRating.get(i) %><br>
   		bidderCountry: <%= bidderCountry.get(i) %><br>
   		bidderLocationName: <%= bidderLocationName.get(i) %><br>
   		time: <%= time.get(i) %><br>
   		amount: $<%= amount.get(i) %><br>

    </td>
    </tr>
    <% } %>

    <tr>
    <td> sellerID </td>
    <td> <%= request.getAttribute("sellerID") %></td>
    </tr>

    <tr>
    <td> sellerRating </td>
    <td> <%= request.getAttribute("sellerRating") %></td>
    </tr>

    <tr>
    <td> description </td>
    <td> <%= request.getAttribute("description") %></td>
    </tr>

    </table>

    <div  id="map_canvas" style="width:40%; height:40%; top: 0%; left: 0%"></div> 


	</body>
</html>