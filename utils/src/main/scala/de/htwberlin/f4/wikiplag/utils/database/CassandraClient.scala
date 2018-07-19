package de.htwberlin.f4.wikiplag.utils.database

import de.htwberlin.f4.wikiplag.utils.database.tables.InverseIndexTable.NGram
import de.htwberlin.f4.wikiplag.utils.database.tables.InverseIndexTable.DocId
import de.htwberlin.f4.wikiplag.utils.database.tables.InverseIndexTable.Occurrences
import com.datastax.spark.connector._
import com.datastax.spark.connector.rdd.CassandraTableScanRDD
import de.htwberlin.f4.wikiplag.utils.CassandraParameters
import de.htwberlin.f4.wikiplag.utils.database.tables.{ArticlesTable, TokenizedTable}
import de.htwberlin.f4.wikiplag.utils.models.Document
import org.apache.spark.SparkContext

/**
  * Client for accessing the Cassandra database.
  *
  * @param sc                  the spark context
  * @param cassandraParameters the cassandra parameters
  */
class CassandraClient(sc: SparkContext, cassandraParameters: CassandraParameters) {

  //TODO cache results
  var tokenizedCache: Map[Int, Vector[String]] = _
  var articlesCache: Map[Int, Document] = _

  /**
    * Same as [[CassandraClient.queryNGramHashes]] but returns an array of cassandra rows instead.
    **/
  def queryNGramHashesAsArray(ngramHashes: List[Long]): Array[CassandraRow] = {
    queryNGramHashes(ngramHashes).collect()
  }

  /**
    * Retrieves all matching n-gram hashes from the database using a single where in statement.
    *
    * @param ngramhashes The n-grams to look for
    * @return a cassandra rdd with the results
    */
  def queryNGramHashes(ngramhashes: List[Long]): CassandraTableScanRDD[CassandraRow] = {
    if (ngramhashes == null || ngramhashes.isEmpty)
      throw new IllegalArgumentException("ngramhashes")
    val df = sc.cassandraTable(cassandraParameters.keyspace, cassandraParameters.inverseIndexTable)

    val result = df.select(NGram, DocId, Occurrences).where(NGram + " in ?", ngramhashes)
    result
  }

  /**
    * produces map from the result rows of queryDocIdsTokens()
    **/
  def queryDocIdsTokensAsMap(docIds: List[Int]): Map[Int, Vector[String]] = {
    queryDocIdsTokens(docIds).map(x => (x.getInt(TokenizedTable.DocId), x.getList[String](TokenizedTable.Tokens))).collect.toMap
  }
  /**
    * returns cassandra rows matching to provided docId-list from TokenizedTable
    * rows contain the docId and the tokens of an article
    **/
  def queryDocIdsTokens(docIds: List[Int]): CassandraTableScanRDD[CassandraRow] = {
    if (docIds == null || docIds.isEmpty)
      throw new IllegalArgumentException("docIds is null or empty")
    val df = sc.cassandraTable(cassandraParameters.keyspace, cassandraParameters.tokenizedTable)

    val result = df.select(TokenizedTable.DocId, TokenizedTable.Tokens).where(TokenizedTable.DocId + " in ?", docIds.toSet)
    result
  }
  /**
    * produces map from the result rows of queryArticles()
    **/
  def queryArticlesAsMap(docIds: Iterable[Int]): Map[Int, Document] = {
    queryArticles(docIds).map(x => x.getInt(ArticlesTable.DocId) ->
      new Document(x.getInt(ArticlesTable.DocId), x.getString(ArticlesTable.Title), x.getString(ArticlesTable.WikiText))).collect.toMap
  }
  /**
    * returns cassandra rows matching to provided docId-List from ArticlesTable
    * rows contain the docId, the original text and the title of an article
    **/
  def queryArticles(docIds: Iterable[Int]): CassandraTableScanRDD[CassandraRow] = {
    if (docIds == null || docIds.isEmpty)
      throw new IllegalArgumentException("docIds is null or empty.")
    val df = sc.cassandraTable(cassandraParameters.keyspace, cassandraParameters.articlesTable)

    val result = df.select(ArticlesTable.DocId, ArticlesTable.WikiText,ArticlesTable.Title).where(ArticlesTable.DocId + " in ?", docIds.toSet)
    result
  }

  /**
    * Author: Laura H.
    * get all wiki articles from cassandra db
    * result is a map with per Wiki Article: (key: int DocId, value: Document(DocId, Title, Text))
    */
  def getAllArticles(): Map[Int, Document] = {
    val table = sc.cassandraTable(cassandraParameters.keyspace, cassandraParameters.articlesTable)
    // has docid, title, wikitext
    val allArticlesFromDB = table.select(ArticlesTable.DocId, ArticlesTable.Title, ArticlesTable.WikiText)
    allArticlesFromDB.map(x => x.getInt(ArticlesTable.DocId) -> new Document(x.getInt(ArticlesTable.DocId), x.getString(ArticlesTable.Title), x.getString(ArticlesTable.WikiText))).collect.toMap
  }


  /**
    * Author: Laura H.
    * gets one wiki article from cassandra db, used for testing purposes
    * result is a Document(DocId, Title, Text)
    */
  def getOneArticle(): Document = {
    val table = sc.cassandraTable(cassandraParameters.keyspace, cassandraParameters.articlesTable)
    // has docid, title, wikitext
    val allArticlesFromDB = table.select(ArticlesTable.DocId, ArticlesTable.Title, ArticlesTable.WikiText)
    val x = allArticlesFromDB.first()
    val doc = new Document(x.getInt(ArticlesTable.DocId), x.getString(ArticlesTable.Title), x.getString(ArticlesTable.WikiText))
    doc
  }
}
