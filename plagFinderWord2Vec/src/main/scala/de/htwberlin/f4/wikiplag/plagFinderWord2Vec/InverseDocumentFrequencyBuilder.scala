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

    def preprocess(texts: List[String]): List[String] = {
      var allWords = ""
      for (text <- texts) {
        //allWords.concat(InverseIndexBuilderImpl.tokenizeAndNormalize(text).toString())
        allWords = allWords + InverseIndexBuilderImpl.tokenizeAndNormalize(text)
        // System.out.println("TOKENIZE AND NORMALIZE  " + InverseIndexBuilderImpl.tokenizeAndNormalize(text)) //TOKENIZE AND NORMALIZE  List(americium,
        // System.out.println("WORDS  " + allWords) //WORD  List()List(americium,

      }
      //Todo: think about sorting out for example single chars here, since they do not convey the topic of an article
      System.out.println("WORDS CLASS " + allWords.getClass)
     // System.out.println("WORDS " + allWords)

      List(allWords)
    }

    def countTheWords(l: List[String]): List[(String, Int)] = {
      l.foldLeft(Map.empty[String, Int]) { (count, word) => count + (word -> (count.getOrElse(word, 0) + 1)) }.toList //(Int, String) => Int
    }

    val inputMapContainsDocuments = cassandraClient.getAllArticles() //get all Documents from DB
    // val inputMapWithStrings = inputMapContainsDocuments.mapValues(doc => doc.text)
    // val inputTexts = inputMapWithStrings.values.reduceLeft((x, String) => String)
    // val inputTexts = inputMapContainsDocuments.values.seq.flatMap(doc => doc.text).toString()
    val inputTexts = inputMapContainsDocuments.values.seq.map(doc => doc.text).toList
    // System.out.println("input string  " + inputTexts)
    System.out.println("input string  " + inputTexts.size)
    System.out.println("input string  " + inputTexts.getClass)


    //val sentencesTokenized = InverseIndexBuilderImpl.tokenizeAndNormalize(inputTexts)
    //System.out.println("sentences tokenized  " + sentencesTokenized.size)
    //System.out.println("sentences tokenized  " + sentencesTokenized.getClass)
    val sentencesTokenized = preprocess(inputTexts)
    System.out.println("sentences tokenized  " + sentencesTokenized)
    System.out.println("sentences tokenized  " + sentencesTokenized.getClass)


    //val result = sentencesTokenized.groupBy(identity).par.mapValues(_.size)
    //val words = sentencesTokenized.split(", ").map(word => (word, 1)).toMap
    val result = countTheWords(sentencesTokenized)
    //  System.out.println("result: " + words)
    result.toMap
    //Map.empty[String, Int]
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
