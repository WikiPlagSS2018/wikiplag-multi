package de.htwberlin.f4.wikiplag.plagFinderWord2Vec

import de.htwberlin.f4.wikiplag.utils.database.CassandraClient
import de.htwberlin.f4.wikiplag.utils.inverseindex.InverseIndexBuilderImpl

import scala.collection.immutable.ListMap

/**
  * @author Laura H.
  * @param cassandraClient
  */
class InverseDocumentFrequencyBuilder(val cassandraClient: CassandraClient) {


  def buildInverseDocFrequency(): Map[String, Int] = {
    val inputMapContainsDocuments = cassandraClient.getAllArticles() //get all Documents from DB
    val inputTexts = inputMapContainsDocuments.values.seq.map(doc => doc.text).toList

    def getDocumentAsString: String = {
      flatten(for (text <- inputTexts) yield text).toString()
    }

    val sentencesTokenized = preprocess(getDocumentAsString)
    val result = countTheWords(sentencesTokenized)
    result
  }


  def buildSortedInvDocFreq(): Map[String, Int] = {
    val invDocFreq = buildInverseDocFrequency()
    ListMap(invDocFreq.toSeq.sortBy(_._2): _*)
    //invDocFreq.toSeq.sortBy(_._2).toMap
  }


  def getInverseDocFreqForOneWord(word: String, invDocFreq: Map[String, Int]): Int = {
    invDocFreq.getOrElse(word, 0)
  }

  def testBuildInverseDocFrequency(): Map[String, Int] = {
    val inputDocContainsOneDocument = cassandraClient.getOneArticle()
    val inputTextAsString = inputDocContainsOneDocument.text

    val sentencesTokenized = preprocess(inputTextAsString)

    val result = countTheWords(sentencesTokenized)
    result
  }


  private def preprocess(text: String): List[String] = {
    val allWords = InverseIndexBuilderImpl.tokenizeAndNormalize(text)
    //Todo: think about sorting out for example single chars here, since they do not convey the topic of an article
    allWords
  }

  private def countTheWords(l: List[String]): Map[String, Int] = {
    l.foldLeft(Map.empty[String, Int]) { (count, word) => count + (word -> (count.getOrElse(word, 0) + 1)) } //(Int, String) => Int
  }

  private def flatten(l: List[Any]): List[Any] = l match {
    case Nil => Nil
    case (h: List[Any]) :: tail => flatten(h) ::: flatten(tail)
    case h :: tail => h :: flatten(tail)
  }

}
