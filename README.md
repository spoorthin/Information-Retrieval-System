# Information Retrieval System

# IR_1
Parsing and indexing html documents in a given folder recursively and listing all parsed files.

# How to Run
We have added few html docs downloaded from https://www.gutenberg.org/ to make it easier to test which can be found in the sites folder.

The program can be run from the command line:

java -jar IR_P01.jar [path to document folder] [path to index folder] [VS/OK] [query]

# Methodology
We split the task into two phases, the first consists of building the index and the second is search phase.
The solution was implemented using Java as a programming language, Lucene and Jsoup for parsing html documents.

By default our solution uses Lucene’s EnglishAnalyzer which by default also uses the Porter Stemmer according to its documentation – v 7.1.0.
The implementation starts by creating the index and indexing all the documents, for this we also use the  corresponding similarity that the user has chosen – by default Lucene uses the okapi BM25Similarity, so we only needed to consider the Vector space model.

For every html document in the folder, we then proceed to parse it using Jsoup and add it to the index. For each document we store its title, path, body and summary if it has any. If we encounter a subfolder, the program traverses it recursively and indexes every html document it finds.

Once the indexing is done, the search begins. For this we use an IndexSearcher and we specify the corresponding similarity and analyzer – same ones that we used for the indexing  process.
In order to search both the title and body at the same time we use the MultiFieldQueryParser and specify the fields to be searched. We retrieve the 10 most relevant documents and for each of them we show its ranking, path, title, summary and score.

# IR_2
Parsing and indexing html webpages and crawl the webpages to traverse the links within the webpage upto the given depth.

# How to Run
The program can be run from the command line:

java -jar IR_P02.jar [seed URL] [crawl depth] [path_to_index folder] [query]

# Methodology
The implementation starts by creating the index and indexing all the webpages , for this we also use the  corresponding similarity that the user has chosen – by default Lucene uses the BM25Similarity.

For every html webpage, we then proceed to parse it using Jsoup and add it to the index. For each webpage we store its title, URL-path, rank and score. The web crawl is done in crawler class where the sub-links within the webpage are traversed and indexed. The crawling depends on the given crawl depth.

Once the indexing is done, the search begins. For this we use an IndexSearcher and we specify the corresponding similarity and analyzer – same ones that we used for the indexing  process.
In order to search both the title and content at the same time we use the MultiFieldQueryParser and specify the fields to be searched. We retrieve the 10 most relevant webpages and for each of them we show its ranking, path, title, summary and score.
