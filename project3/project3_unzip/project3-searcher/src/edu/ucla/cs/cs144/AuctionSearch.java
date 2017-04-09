package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;


public class AuctionSearch implements IAuctionSearch {

	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */
	
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn){
		// TODO: Your code here!
		
		SearchEngine se;
		SearchResult[] res = null;
		
		try {
			se = new SearchEngine();
			TopDocs topDocs = se.performSearch(query, numResultsToReturn + numResultsToSkip); 
			ScoreDoc[] hits = topDocs.scoreDocs;
			
//			int totolNumber = Math.min (numResultsToReturn, hits.length - numResultsToSkip);
//			res = new SearchResult[totolNumber];
//			
//			for (int i = numResultsToSkip; i < totolNumber + numResultsToSkip ; i++) {
//			    Document doc = se.getDocument(hits[i].doc);
//			    res[i] = new SearchResult (doc.get("id"), doc.get("name"));
//			}
			ArrayList<SearchResult> resList = new ArrayList<SearchResult>();
			
			for (int i = numResultsToSkip; i < numResultsToReturn + numResultsToSkip && i < hits.length ; i++) {
			    Document doc = se.getDocument(hits[i].doc);
			    resList.add(new SearchResult (doc.get("id"), doc.get("name")));

			}
			res = (SearchResult[]) resList.toArray(new SearchResult[resList.size()]);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {
		// TODO: Your code here!
		
		SearchEngine se;
		SearchResult[] res = null;
		
		try {

			Connection conn = DbManager.getConnection(true); 
		    Statement stmt = conn.createStatement();
			ResultSet rs = null;
			
			double lx = region.getLx();
			double ly = region.getLy();
			double rx = region.getRx();
			double ry = region.getRy();
			
			rs = stmt.executeQuery("SELECT ItemID FROM Location WHERE MBRContains(GeomFromText('Polygon(("+lx+" "+ly+", "+rx+" "+ly+", "+rx+" "+ry+", "+lx+" "+ry+", "+lx+" "+ly+"))'),Position);");
		
			Set<String> set = new HashSet<String>();
			
			while (rs.next()) {
				set.add(rs.getString(1));
			}

			rs.close();
			stmt.close();
			conn.close();
			
			se = new SearchEngine();
			TopDocs topDocs = se.performSearch(query, Integer.MAX_VALUE); 
			ScoreDoc[] hits = topDocs.scoreDocs;
			
		//	int totolNumber = Math.min (numResultsToReturn, hits.length - numResultsToSkip);
			
			ArrayList<SearchResult> resList = new ArrayList<SearchResult>();
			
		//	System.out.println(hits.length + " " + numResultsToReturn + " " + numResultsToSkip);
			
			for (int i = 0, count = 0, skip = 0; count < numResultsToReturn  && i < hits.length ; i++) {
			    Document doc = se.getDocument(hits[i].doc);
			    String id = doc.get("id");
			    if (set.contains(id)){
			    	if (skip < numResultsToSkip)
			    		skip++;
			    	else {
			    		resList.add(new SearchResult (id, doc.get("name")));
				    	count++;
			    	}
			    }
			}
			res = (SearchResult[]) resList.toArray(new SearchResult[resList.size()]);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}

	public String getXMLDataForItemId(String itemId) {
		// TODO: Your code here!
		
		Connection conn;
		String res = "";
		try {
			conn = DbManager.getConnection(true);
			Statement stmt = conn.createStatement();
			
			ResultSet rs = null;
			
			rs = stmt.executeQuery("SELECT * FROM Items WHERE ItemID="+itemId);
			
			String name = "";
			String currently = "";
			String buyPrice = "";
			String firstBid = "";
			int numberOfBids = 0;
			String latitude = "";	
			String longitude = "";
			String location = "";
			String country = "";
			String started = "";
			String ends = "";
			String sellerID = "";
			String description = "";
			SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        SimpleDateFormat format = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
	       
			if (rs.next()) {
				name = rs.getString("Name");
				currently = rs.getString("Currently");
				buyPrice = rs.getString("Buy_Price");
				firstBid = rs.getString("First_Bid");
				numberOfBids = rs.getInt("Number_of_Bids");
				latitude = rs.getString("Latitude");
				longitude = rs.getString("Longitude");
				location = rs.getString("Location");
				country = rs.getString("Country");
				started = rs.getString("Started");
				ends = rs.getString("Ends");
				sellerID = rs.getString("Seller_UserID");
				description = rs.getString("Description");
			}
			else {
				rs.close();
				stmt.close();
				conn.close();
				return "";
			}
			
			
			rs.close();
			stmt.close();
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT Title FROM Category WHERE ItemID =" + itemId);
			ArrayList<String> category = new ArrayList<String>();
			while (rs.next()) {
				category.add(rs.getString(1));
			}
			
			rs.close();
			stmt.close();
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT Rating FROM Seller WHERE UserID =\"" + sellerID + "\"");
			int sellerRating = 0;
			if (rs.next()) {
				sellerRating = rs.getInt(1);
			}
			
			rs.close();
			stmt.close();
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT Bidder_UserID, Time, Amount FROM Bids WHERE ItemID ="+itemId);
			ArrayList<String> bidsTime = new ArrayList<String>();
			ArrayList<String> bidsAmount = new ArrayList<String>();
			ArrayList<String> bidsBidderID = new ArrayList<String>();
			ArrayList<Integer> bidsBidderRating = new ArrayList<Integer>();
			ArrayList<String> bidsBidderLatitude = new ArrayList<String>();
			ArrayList<String> bidsBidderLongitude = new ArrayList<String>();
			ArrayList<String> bidsBidderLocation = new ArrayList<String>();
			ArrayList<String> bidsBidderCountry = new ArrayList<String>();
			
			while (rs.next()) {
				bidsTime.add(rs.getString("Time"));
				bidsAmount.add(rs.getString("Amount"));
				String bidderId = rs.getString("Bidder_UserID");
				bidsBidderID.add(bidderId);
				
				Statement stmtBidder = conn.createStatement();
				ResultSet rsBidder = stmtBidder.executeQuery("SELECT Rating, Latitude, Longitude, Location, Country FROM Bidder WHERE UserID =\"" + bidderId + "\"");
				if (rsBidder.next()) {
					bidsBidderRating.add(rsBidder.getInt("Rating"));
					bidsBidderLatitude.add(rsBidder.getString("Latitude"));
					bidsBidderLongitude.add(rsBidder.getString("Longitude"));
					bidsBidderLocation.add(rsBidder.getString("Location"));
					bidsBidderCountry.add(rsBidder.getString("Country"));
				}
				rsBidder.close();
				stmtBidder.close();
			}
			rs.close();
			stmt.close();
			conn.close();
			
			res = res + "<Item ItemID=\"" + itemId + "\">\n";
			res = res + "  <Name>" + name.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</Name>\n";
			
			for (String each : category){
				res = res + "  <Category>" + each.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</Category>\n";
			}
			
			res = res + "  <Currently>$" + currently + "</Currently>\n";
			
			if (!buyPrice.equals("0.00"))
				res = res + "  <Buy_Price>$" + buyPrice + "</Buy_Price>\n";
			
			res = res + "  <First_Bid>$" + firstBid + "</First_Bid>\n";
			res = res + "  <Number_of_Bids>" + numberOfBids + "</Number_of_Bids>\n";
			if (numberOfBids != 0){
				res = res + "  <Bids>\n";
				for (int i = 0; i < bidsBidderID.size(); i++){
					res = res + "    <Bid>\n";
					res = res + "      <Bidder Rating=\"" + bidsBidderRating.get(i) + "\" UserID=\"" + bidsBidderID.get(i).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;") + "\">\n";
					
					if (!bidsBidderLatitude.get(i).equals("0") || !bidsBidderLongitude.get(i).equals("0"))
						res = res + "        <Location Latitude=\"" + bidsBidderLatitude.get(i) + "\" Longitude=\"" + bidsBidderLongitude.get(i) + "\">" + bidsBidderLocation.get(i).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</Location>\n";
					else
						res = res + "        <Location>" + bidsBidderLocation.get(i).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</Location>\n";
					
					res = res + "        <Country>" + bidsBidderCountry.get(i).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</Country>\n";
					res = res + "      </Bidder>\n";
					res = res + "      <Time>" + format.format(parse.parse(bidsTime.get(i))) + "</Time>\n";
					res = res + "      <Amount>$" + bidsAmount.get(i) + "</Amount>\n";
					res = res + "    </Bid>\n";
				}
			}
			else 
				res = res + "  <Bids />\n";
			
			if (!latitude.equals("0") || !longitude.equals("0"))
				res = res + "  <Location Latitude=\"" + latitude + "\" Longitude=\"" + longitude + "\">" + location.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</Location>\n";
			else
				res = res + "  <Location>" + location.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</Location>\n";
			
			res = res + "  <Country>" + country.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</Country>\n";
			res = res + "  <Started>" + format.format(parse.parse(started)) + "</Started>\n";
			res = res + "  <Ends>" + format.format(parse.parse(ends)) + "</Ends>\n";
			res = res + "  <Seller Rating=\"" + sellerRating + "\" UserID=\"" + sellerID.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;") + "\" />\n";
			
			if (!description.equals("")) 
				res = res + "  <Description>" + description.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")+ "</Description>\n";
			else 
				res = res + "  <Description />\n";
			
			res = res + "</Item>\n";
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		return res;
	}
	
	public String echo(String message) {
		return message;
	}

}
