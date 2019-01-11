# Information-Retrieval-System-file-parser

Parsing and indexing html documents in a given folder recursively and list all parsed files.

# How to Run
We have added few html docs downloaded from https://www.gutenberg.org/ to make it easier to test which can be found in the sites folder.

The program can be run from the command line:

java -jar IR_P01.jar [path to document folder] [path to index folder] [VS/OK] [query]

# Methodology
We split the task into two phases, the first consists of building the index and the second is search phase.
The solution was implemented using Java as a programming language, Lucene and Jsoup for parsing html documents.

By default our solution uses Lucene’s EnglishAnalyzer which by default also uses the Porter Stemmer according to its documentation – v 7.1.0 -.
The implementation starts by creating the index and indexing all the documents, for this we also use the  corresponding similarity that the user has chosen – by default Lucene uses the BM25Similarity, so we only needed to consider the Vector Model Space case-.

For every html document in the folder, we then proceed to parse it using Jsoup and add it to the index. For each document we store its title, path, body and summary if it has any. If we encounter a subfolder, the program traverses it recursively and indexes every html document it finds.

Once the indexing is done, the search begins. For this we use an IndexSearcher and we specify the corresponding similarity and analyzer – same ones that we used for the indexing  process.
In order to search both the title and body at the same time we use the MultiFieldQueryParser and specify the fields to be searched. We retrieve the 10 most relevant documents and 
