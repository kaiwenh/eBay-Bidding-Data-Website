/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;


class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;
    
    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
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
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */
        PrintWriter sellerWriter = null;
        PrintWriter bidderWriter = null;
        PrintWriter bidsWriter = null;
        PrintWriter itemsWriter = null;
        PrintWriter categoryWriter = null;
        try{
        	sellerWriter = new PrintWriter(new FileOutputStream (new File("sellerDup.txt"), true));
        	bidderWriter = new PrintWriter(new FileOutputStream (new File("bidderDup.txt"), true));
        	bidsWriter = new PrintWriter(new FileOutputStream (new File("bids.txt"), true));
        	itemsWriter = new PrintWriter(new FileOutputStream (new File("items.txt"), true));
        	categoryWriter = new PrintWriter(new FileOutputStream (new File("category.txt"), true));
        	
		} catch (IOException e) {
			System.out.println("Cannot write file");
            e.printStackTrace();
            System.exit(3);
		}
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat parse = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
        NodeList list = doc.getElementsByTagName("Item");
        
        for (int i = 0; i < list.getLength(); i++){

    		Element elem = (Element) list.item(i);
    		
    		String id = elem.getAttribute("ItemID");
    		
    		String name = getElementTextByTagNameNR(elem, "Name");
    		
    		Element[] catList = getElementsByTagNameNR(elem, "Category");
    		for (int k = 0; k < catList.length; k++){
    			String category = getElementText(catList[k]);
    			categoryWriter.println(id+",,"+category);
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
    		if (n > 0){
    			Element bids = getElementByTagNameNR(elem, "Bids");
    			Element[] bidList = getElementsByTagNameNR(bids, "Bid");

    			for (int j = 0; j < n; j++){
    				Element bidder = getElementByTagNameNR(bidList[j], "Bidder");
    				String bidderID = bidder.getAttribute("UserID");
    	    		String bidderRating = bidder.getAttribute("Rating");
    	    		
    	    		Element countryElem = getElementByTagNameNR(bidder, "Country");
    	    		String bidderCountry=null;
    	    		if (countryElem != null){
    	    			bidderCountry = getElementText(countryElem);
    	    		}
    	    		
    	    		
    	    		Element bidderLocation = getElementByTagNameNR(bidder, "Location");
    	    		String bidderLatitude = null;
    	    		String bidderLongitude = null;
    	    		String bidderLocationName = null;
    	    		if (bidderLocation != null) {
	    	    		bidderLatitude = bidderLocation.getAttribute("Latitude");
	    	    		bidderLongitude = bidderLocation.getAttribute("Longitude");
	    	    		bidderLocationName = getElementText(bidderLocation);
    	    		}
    	    		
    	    		String time = null;
					try {
						time = format.format(parse.parse(getElementTextByTagNameNR(bidList[j], "Time")));
					} catch (ParseException e) {
						e.printStackTrace();
					}
    	    		String amount = strip(getElementTextByTagNameNR(bidList[j], "Amount"));
    	    		
    	    		bidderWriter.println(bidderID+",,"+bidderRating+",,"+bidderLatitude+",,"+bidderLongitude+",,"+bidderLocationName+",,"+bidderCountry);
    	    		bidsWriter.println(bidderID+",,"+time+",,"+id+",,"+amount);
    			}
    		}
    		
    		Element seller = getElementByTagNameNR(elem, "Seller");
    		String sellerID = seller.getAttribute("UserID");
    		String sellerRating = seller.getAttribute("Rating");
    		String description = getElementTextByTagNameNR(elem, "Description");
    		
    		if (description.length() >= 4000)
    			description = description.substring(0, 4000);
    		
    		sellerWriter.println(sellerID+",,"+sellerRating);
    		itemsWriter.println(id+",,"+name+",,"+currently+",,"+buyPrice+",,"+firstBid+",,"+numOfBids+",,"+latitude+",,"+longitude+",,"+locationName+",,"+country+",,"+start+",,"+end+",,"+sellerID+",,"+description);
        }

        sellerWriter.close();
        bidderWriter.close();
        bidsWriter.close();
        itemsWriter.close();
        categoryWriter.close();
        
        /**************************************************************/
        
    }
    
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }
    }
}
