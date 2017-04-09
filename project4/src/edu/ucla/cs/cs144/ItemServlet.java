package edu.ucla.cs.cs144;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here
    	
    	AuctionSearch as = new AuctionSearch();
    	
    	String itemId = request.getParameter("id");
		String item = as.getXMLDataForItemId(itemId);
		
		Document doc = null;
		DocumentBuilder builder;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true); 

		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(new ByteArrayInputStream(item.getBytes()));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		Element elem = (Element) doc.getFirstChild();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat parse = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
        
		String id = elem.getAttribute("ItemID");
		String name = getElementTextByTagNameNR(elem, "Name");
		Element[] catList = getElementsByTagNameNR(elem, "Category");
		ArrayList<String> category = new ArrayList<String>();
		for (int k = 0; k < catList.length; k++){
			category.add(getElementText(catList[k]));
		}
		
		String currently = strip(getElementTextByTagNameNR(elem, "Currently"));
		
		Element buy = getElementByTagNameNR(elem, "Buy_Price");
		String buyPrice=null;
		if (buy != null){
			buyPrice = strip(getElementText(buy));
		}
		
		String firstBid = strip(getElementTextByTagNameNR(elem, "First_Bid"));
		String numOfBids = getElementTextByTagNameNR(elem, "Number_of_Bids");
		
		Element location = getElementByTagNameNR(elem, "Location");
		String latitude = location.getAttribute("Latitude");
		String longitude = location.getAttribute("Longitude");
		String locationName = location.getTextContent();
		
		String country = getElementTextByTagNameNR(elem, "Country");
		
		String start = null;
		try {
			start = format.format(parse.parse(getElementTextByTagNameNR(elem, "Started")));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		String end = null;
		try {
			end = format.format(parse.parse(getElementTextByTagNameNR(elem, "Ends")));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		int n = Integer.parseInt(numOfBids);
		
		ArrayList<String> bidderID = new ArrayList<String>();
		ArrayList<String> bidderRating = new ArrayList<String>();
		ArrayList<String> bidderCountry = new ArrayList<String>();
		ArrayList<String> bidderLatitude = new ArrayList<String>();
		ArrayList<String> bidderLongitude = new ArrayList<String>();
		ArrayList<String> bidderLocationName = new ArrayList<String>();
		ArrayList<String> time = new ArrayList<String>();
		ArrayList<String> amount = new ArrayList<String>();
		
		if (n > 0){
			Element bids = getElementByTagNameNR(elem, "Bids");
			Element[] bidList = getElementsByTagNameNR(bids, "Bid");

			for (int j = 0; j < n; j++){
				Element bidder = getElementByTagNameNR(bidList[j], "Bidder");
				bidderID.add(bidder.getAttribute("UserID"));
	    		bidderRating.add(bidder.getAttribute("Rating"));
	    		
	    		
	    		Element countryElem = getElementByTagNameNR(bidder, "Country");

	    		if (countryElem != null){
	    			bidderCountry.add(getElementText(countryElem));
	    		}
	    		else
	    			bidderCountry.add(null);
	    		
	    		
	    		Element bidderLocation = getElementByTagNameNR(bidder, "Location");

	    		if (bidderLocation != null) {
    	    		bidderLatitude.add(bidderLocation.getAttribute("Latitude"));
    	    		bidderLongitude.add(bidderLocation.getAttribute("Longitude"));
    	    		bidderLocationName.add(getElementText(bidderLocation));
	    		}
	    		
				try {
					time.add(format.format(parse.parse(getElementTextByTagNameNR(bidList[j], "Time"))));
				} catch (ParseException e) {
					e.printStackTrace();
				}
	    		amount.add(strip(getElementTextByTagNameNR(bidList[j], "Amount")));
	    		
			}
		}
		
		Element seller = getElementByTagNameNR(elem, "Seller");
		String sellerID = seller.getAttribute("UserID");
		String sellerRating = seller.getAttribute("Rating");
		String description = getElementTextByTagNameNR(elem, "Description");
            

		
		
		request.setAttribute("id", id);
		request.setAttribute("name", name);
		request.setAttribute("category", category);
		request.setAttribute("currently", currently);
		request.setAttribute("buyPrice", buyPrice);
		request.setAttribute("firstBid", firstBid);
		request.setAttribute("numOfBids", numOfBids);
		request.setAttribute("latitude", latitude);
		request.setAttribute("longitude", longitude);
		request.setAttribute("locationName", locationName);
		request.setAttribute("country", country);
		request.setAttribute("start", start);
		request.setAttribute("end", end);
		
		request.setAttribute("bidderID", bidderID);
		request.setAttribute("bidderRating", bidderRating);
		request.setAttribute("bidderCountry", bidderCountry);
		request.setAttribute("bidderLatitude", bidderLatitude);
		request.setAttribute("bidderLongitude", bidderLongitude);
		request.setAttribute("bidderLocationName", bidderLocationName);
		request.setAttribute("time", time);
		request.setAttribute("amount", amount);
		
		request.setAttribute("sellerID", sellerID);
		request.setAttribute("sellerRating", sellerRating);
		request.setAttribute("description", description);
		
		
		request.getRequestDispatcher("/item.jsp").forward(request, response);

    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }
}
