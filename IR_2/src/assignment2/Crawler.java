package assignment1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	
	private static String REFERRER = "http://www.google.com";
	private static String USER_AGENT = "curl/7.37.0";
	private static String PAGES_FILE = "pages.txt";
	
	private Queue <ItemUrl> visitedUrls = new LinkedList<ItemUrl>();
	private Queue <ItemUrl> nextUrls = new LinkedList<ItemUrl>();
	
	URL seedUrl; 
	int maxDepth;
	String indexPath;
	IndexWriter indexWriter;
	
	public Crawler(String seedUrl, int depth, String indexPath, IndexWriter writer) throws MalformedURLException {
		this.seedUrl = new URL(seedUrl);
		this.maxDepth = depth;
		this.indexPath = indexPath;
		this.indexWriter = writer;
	}
	
	public void perform() throws MalformedURLException {
		URL url = new URL(this.seedUrl.getProtocol() + "://" + this.seedUrl.getAuthority() + this.seedUrl.getPath());
		ItemUrl currentUrl = new ItemUrl(url, 0);
		nextUrls.add(currentUrl);
		processUrls();
		logVisitedUrls();
	}
		
		
	public void processUrls() {
		while(!this.nextUrls.isEmpty()) {
			ItemUrl currentUrl = this.nextUrls.remove();
			if (!this.visitedUrls.contains(currentUrl)) {
				System.out.println("Visiting : " + currentUrl.toString());
				try {
					org.jsoup.nodes.Document doc = Jsoup.connect(currentUrl.url.toString()).userAgent(USER_AGENT).referrer(REFERRER).followRedirects(false).get();
					this.visitedUrls.add(currentUrl);
					
					if (currentUrl.depth < this.maxDepth) {
						// index and crawl the page
						indexPage(doc);
						List <ItemUrl> nestedUrls = crawlPage(doc, currentUrl.depth + 1, currentUrl);
						// add them to nextUrls, only if not already present...
						for(ItemUrl url : nestedUrls) {
							//System.out.println(url.toString());
							if (!this.visitedUrls.contains(url) || !nextUrls.contains(url)) {
								this.nextUrls.add(url);
							}
						}
					} else { 
						// just index the page
						indexPage(doc);
					}
				} catch (IOException  e) {
					System.out.println("Couldn't visit the url");
					e.printStackTrace();
				}
			}
		}
	}
	
	public List<ItemUrl> crawlPage(org.jsoup.nodes.Document doc, int depth, ItemUrl parentUrl) {
		List <ItemUrl> results = new ArrayList<ItemUrl>();
		Elements links = doc.select("a");
	    
		for (Element link : links) {
		    	String linkHref = link.attr("href");
		    	URL newUrl;
		    	newUrl = normalizeUrl(linkHref, parentUrl);
			results.add(new ItemUrl(newUrl, depth));
	    }
	
		return results;
	}
	
	public URL normalizeUrl(String link, ItemUrl parentUrl) {
		URL normalizedUrl = null;
		URL result = null;
		try {
			//lowercase urls
			normalizedUrl = new URL(parentUrl.url, link);
			result = new URL(normalizedUrl.getProtocol() + "://" + normalizedUrl.getAuthority().toLowerCase() + normalizedUrl.getPath().replaceFirst("/$", "").toLowerCase());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to parse url");
			e.printStackTrace();
		}

		return result;
	}
	
	
	public void indexPage(org.jsoup.nodes.Document htmlDoc) throws IOException {
  		 Document doc = new Document();
  		 Field pathField = new StringField("url", htmlDoc.location(), Field.Store.YES);
  		 doc.add(pathField);
  		 Field contents = new TextField("contents", htmlDoc.body().text(), Field.Store.YES);
  		 doc.add(contents);
  		 String title = htmlDoc.title();
  		 doc.add(new TextField("title", title, Field.Store.YES));
  		 Elements summaries = htmlDoc.getElementsByTag("summary");
  		 Element htmlSummary = summaries.first();
  		 String summary = "";
  		 if (htmlSummary != null) {
  			 summary = htmlSummary.text();
  		 }
  		 doc.add(new TextField("summary", summary, Field.Store.YES));
  		 indexWriter.updateDocument(new Term("path", htmlDoc.location()), doc);
	}
	
	public void logVisitedUrls() {
		PrintWriter writer;
		try {
			writer = new PrintWriter(new File(this.indexPath + "/" + PAGES_FILE), "UTF-8");
			for (ItemUrl url : this.visitedUrls) {
				writer.println(url.toString());
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}