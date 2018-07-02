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

    /*    def flatten(l: List[List[String]]): List[String] = {
          def _flatten(res: List[Any], rem: List[Any]):List[String] = rem match {
            case Nil => List(res.toString())
            case (h:List[_])::Nil => _flatten(res, h)
            case (h:List[_])::tail => _flatten(res:::h, tail)
            case h::tail => _flatten(res:::List(h), tail)
          }
          _flatten(List(), l)
        }*/

    def flatten(l: List[Any]): List[Any] = l match {
      case Nil => Nil
      case (h: List[Any]) :: tail => flatten(h) ::: flatten(tail)
      case h :: tail => h :: flatten(tail)
    }

    def preprocess(text: String): List[String] = {
      val allWords = InverseIndexBuilderImpl.tokenizeAndNormalize(text)

      System.out.println("allWords " + allWords)
      //Todo: think about sorting out for example single chars here, since they do not convey the topic of an article
      allWords
    }

    def countTheWords(l: List[String]): List[(String, Int)] = {
      l.foldLeft(Map.empty[String, Int]) { (count, word) => count + (word -> (count.getOrElse(word, 0) + 1)) }.toList //(Int, String) => Int
    }



    val inputMapContainsDocuments = cassandraClient.getAllArticles() //get all Documents from DB
    val inputTexts = inputMapContainsDocuments.values.seq.map(doc => doc.text).toList

    def getDocumentAsString: String = {
      flatten(for (text <- inputTexts) yield text).toString()
    }

    val sentencesTokenized = preprocess(getDocumentAsString)




    System.out.println("sentences tokenized  " + sentencesTokenized)
    System.out.println("sentences tokenized  " + sentencesTokenized.getClass)

    //  val result = countTheWords(sentencesTokenized)
    //System.out.println(result)
    //  result.toMap
    Map.empty[String, Int]
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
