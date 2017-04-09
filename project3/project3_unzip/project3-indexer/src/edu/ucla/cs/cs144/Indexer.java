package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
    
    /** Creates a new instance of Indexer */
    public Indexer() {
    }
    
    private IndexWriter indexWriter = null;
    
    public IndexWriter getIndexWriter(boolean create) throws IOException {
        if (indexWriter == null) {
            Directory indexDir = FSDirectory.open(new File("/var/lib/lucene/index"));
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            indexWriter = new IndexWriter(indexDir, config);
        }
        return indexWriter;
    }
    
    public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }
    
    public void indexItem(String id, String name, String content) throws IOException {

        IndexWriter writer = getIndexWriter(false);
        Document doc = new Document();
        doc.add(new StringField("id", id, Field.Store.YES));
        doc.add(new TextField("name", name, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.NO));
        
        writer.addDocument(doc);
    }
 
    public void rebuildIndexes() {

        Connection conn = null;

        // create a connection to the database to retrieve Items from MySQL
		try {
		    conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
		    System.out.println(ex);
		}


	/*
	 * Add your code here to retrieve Items using the connection
	 * and add corresponding entries to your Lucene inverted indexes.
         *
         * You will have to use JDBC API to retrieve MySQL data from Java.
         * Read our tutorial on JDBC if you do not know how to use JDBC.
         *
         * You will also have to use Lucene IndexWriter and Document
         * classes to create an index and populate it with Items data.
         * Read our tutorial on Lucene as well if you don't know how.
         *
         * As part of this development, you may want to add 
         * new methods and create additional Java classes. 
         * If you create new classes, make sure that
         * the classes become part of "edu.ucla.cs.cs144" package
         * and place your class source files at src/edu/ucla/cs/cs144/.
	 * 
	 */
	
		Statement stmt = null;
	
		try {
			stmt = conn.createStatement();
		
			ResultSet rs = null;
		
			rs = stmt.executeQuery("SELECT ItemID, Name, Description FROM Items");
		
			getIndexWriter(true);
			
			while (rs.next()) {
				String id = rs.getString("ItemID");
				
				String name = rs.getString("Name");
				String description = rs.getString("Description");
				
				Statement stmtCategory = null;
				stmtCategory = conn.createStatement();
		
				ResultSet rsCategory = null;
				rsCategory = stmtCategory.executeQuery("SELECT Title FROM Category WHERE ItemID ="+id);
		
				String category = "";
				while (rsCategory.next()) {
					category = category + rsCategory.getString("Title")+" ";
				}
				String content = name + " " + category + " " + description;
				indexItem (id, name, content);
				rsCategory.close();
				stmtCategory.close();
			}
			
			rs.close();
			stmt.close();
			closeIndexWriter();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	    // close the database connection
		try {
		    conn.close();
		} catch (SQLException ex) {
		    System.out.println(ex);
		}
    }    

    public static void main(String args[]) {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }   
}
