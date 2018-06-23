package de.htwberlin.f4.wikiplag.plagFinderWord2Vec

import java.io.{BufferedWriter, File, FileWriter}

import de.htwberlin.f4.wikiplag.utils.database.CassandraClient
import de.htwberlin.f4.wikiplag.utils.inverseindex.InverseIndexBuilderImpl

/**
  * @author Laura H.
  * @param cassandraClient
  */
class InverseDocumentFrequencyBuilder(val cassandraClient: CassandraClient) {

  //private String fileName =

  val invDocFreq = buildInverseDocFrequency()


  def buildInverseDocFrequency(): Map[String, Int] = {
    val docIds = Iterable.newBuilder[Int].result()
    val inputMapContainsDocuments = cassandraClient.queryArticlesAsMap(docIds) //get all Documents from DB
    val inputMapWithStrings = inputMapContainsDocuments.mapValues(doc => doc.toString)
    val inputString = inputMapWithStrings.values.reduceLeft((x, String) => String)
    val sentencesTokenized = InverseIndexBuilderImpl.tokenizeAndNormalize(inputString)

  sentencesTokenized.groupBy(identity).mapValues(_.size)
  }


  def getInverseDocFreqForOneWord(word: String) : Int = {
    invDocFreq.getOrElse(word, 0)
  }


  def writeInvDocFreqToFile(invDocFreq: Map[String, Int]): Boolean = {
    return false
  }


  def readInvDocFreqFromFile(): Map[String, Int] = {
    return null

  }
}
