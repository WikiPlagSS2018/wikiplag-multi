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
    // System.out.println("input map  "+ inputMapContainsDocuments)
    // val inputMapWithStrings = inputMapContainsDocuments.mapValues(doc => doc.text)
    // System.out.println("input map strings  "+ inputMapWithStrings)
    // val inputString = inputMapWithStrings.values.reduceLeft((x, String) => String)
    val inputStrings = inputMapContainsDocuments.values.seq.map(doc => doc.text)
    System.out.println("input string  " + inputStrings)

    Map.empty[String, Int]
    // val sentencesTokenized = InverseIndexBuilderImpl.tokenizeAndNormalize(inputString)
    //System.out.println("sentences tokenized  "+ inputMapContainsDocuments)

   // sentencesTokenized.groupBy(identity).mapValues(_.size)
  }

  def buildSortedInvDocFreq(): Map[String, Int] = {
    val invDocFreq = buildInverseDocFrequency()
    ListMap(invDocFreq.toSeq.sortBy(_._2): _*)
    //invDocFreq.toSeq.sortBy(_._2).toMap
  }


  def getInverseDocFreqForOneWord(word: String, invDocFreq: Map[String, Int]): Int = {
    invDocFreq.getOrElse(word, 0)
  }

}
