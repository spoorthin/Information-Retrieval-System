package assignment1;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
	
	private static int N = 10;

	public static void main(String[] args) {
		//parse arguments
		//java -jar IR P02.jar [seed URL] [crawl depth] [path to index folder] [query]

	    String seedUrl = args[0];
	    int depth = Integer.parseInt(args[1]);
		String indexPath = args[2];
		String queryString = args[3];
		Directory dir = null;
	    IndexWriter writer = null;
		System.out.println("Seed url: " + seedUrl);
		System.out.println("Index path: " + indexPath);
		System.out.println("Crawl depth: " + depth);
		System.out.println("Query: " + queryString);
	    try {
			dir = FSDirectory.open(Paths.get(indexPath));
	    		
	    		// EnglishAnalyzer applies PorterStemFilter by default, no need to add anything.
	    		Analyzer analyzer = new EnglishAnalyzer();
	    		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
   			
	    		// by default it uses BM25Similarity
	    		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

    			if (!DirectoryReader.indexExists(dir)) {
 
    				writer = new IndexWriter(dir, iwc);
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println("Indexing documents...");
				
				Crawler crawler = new Crawler(seedUrl, depth, indexPath, writer);
				crawler.perform();
			
				writer.close();
				System.out.println("Done indexing documents.");
				System.out.println();
				System.out.println();
				System.out.println();
			
    			}
			System.out.println("Search documents...");
			searchDocs(indexPath, queryString);
			System.out.println("Done!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private static void searchDocs(String index, String queryString) {		
		//10 most relevant documents with their rank, title and summary, relevance score and path.
	    IndexReader reader;
		try {		
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		    IndexSearcher searcher = new IndexSearcher(reader);
		    Analyzer analyzer = new EnglishAnalyzer();
		    // we need to store the title also and do a multifield search
		    MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[] {"contents", "title"}, analyzer);
		    Query query = parser.parse(queryString);
		    
		    ScoreDoc[] results = searcher.search(query, N).scoreDocs;
		    
		    for(int i=0; i<results.length; i++) {
		    		Document doc = searcher.doc(results[i].doc);		   
		    		String path = doc.get("url");
	    			System.out.println((i+1) + ". ");
		    		if (path != null) {
		    			//rank
		    			System.out.println("        Path: " + path);
		    			String title = doc.get("title");
		    			if (title != null) {
		    				System.out.println("	Title: " + title);
		    			} else {
		    				System.out.println("	This document has no title.");
		    			}
		    			//String summary = doc.get("summary");
		    			//if (summary != null) {
		    				//System.out.println("	Summary: " + summary);
		    			//} else {
		    				//System.out.println("	This document has no summary.");
		    			//}
			    		System.out.println("	Score: " + results[i].score);
			    		
		    		} else {
		    			System.out.println("No path for this document.");
		    		}
		    }
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
}
