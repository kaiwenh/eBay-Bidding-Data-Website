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
	</form>


	</body>
</html>