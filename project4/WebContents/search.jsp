<%@page import="edu.ucla.cs.cs144.SearchResult"%>
<html>
    <head>
    <title>eBay Search</title>
    <script type="text/javascript" src="suggest.js"></script>
    <link rel="stylesheet" type="text/css" href="suggest.css" />        
    <script type="text/javascript">
        window.onload = function () {
            var oTextbox = new AutoSuggestControl(document.getElementById("txt"));        
        }
    </script>

    </head>

    <body>

	<h1>eBay Search Web Site</h1>

    <form action="search" method="get">

	<br><input type="text" id="txt" name="query" autocomplete="off"/><br>

	<br>
	<input type="submit" value="Search" name="search">
	<input type="submit" value="Back" name="search">
	<input type="submit" value="Next" name="search">
	</form>

	<form action="search" method="get">
	<input type="submit" value="Reset">
	</form>

	<% SearchResult[] result = (SearchResult[])request.getAttribute("result"); %>
	<% Integer n = (Integer)request.getAttribute("number"); %>

    <h3>Result for: <%= request.getAttribute("query") %></h3>


	<table border=1 align='left'>
	<% for (int i=0; i<result.length; i++) { %>
    <tr> 
    <td>
    <%= i + 1 + n*20%>
    <td>
    <td>
    <a href=item?id=<%= result[i].getItemId() %> > <%= result[i].getName() %> </a> 
    </td>
    </tr>
    <% } %>

    </table>


	</body>
</html>