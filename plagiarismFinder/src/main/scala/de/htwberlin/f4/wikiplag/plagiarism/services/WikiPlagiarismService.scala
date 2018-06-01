package de.htwberlin.f4.wikiplag.plagiarism.services

import de.htwberlin.f4.wikiplag.plagiarism.PlagiarismFinder
import de.htwberlin.f4.wikiplag.plagiarism.models.{TextPosition, WikiExcerpt, WikiPlagiarism}
import de.htwberlin.f4.wikiplag.utils.Functions
import de.htwberlin.f4.wikiplag.utils.database.CassandraClient

/**
  * Creates [[WikiPlagiarism]] from the result of the plagiarism finder so they can be served by the rest api.
  */
class WikiPlagiarismService(cassandraClient: CassandraClient) {

  /** Creates wikipedia plagiarisms from the given plagiarism candidates.
    *
    * @param plagiarismCandidates the result of the [[PlagiarismFinder.findPlagiarisms()]] Method
    * @param n                    number of characters to include before and after the plagiarism in the excerpt
    **/
  def createWikiPlagiarisms(plagiarismCandidates: Map[TextPosition, List[(Vector[String], Int)]], n: Int): List[WikiPlagiarism] = {

    //there are no plagiarism, just return an empty list
    if (plagiarismCandidates.isEmpty)
      return List.empty[WikiPlagiarism]

    //get all references wikipedia articles and store them in a map
    var docIds = plagiarismCandidates.values.flatten.map(_._2)
    var wikiArticlesMap = cassandraClient.queryArticlesAsMap(docIds)

    //create the wiki plagiarisms
    //sort them in ascending order
    plagiarismCandidates.toSeq.sortWith(_._1.start < _._1.start).zipWithIndex.map(x => {
      val startInInputText = x._1._1.start
      val endInInputText = x._1._1.end
      val excerpts = x._1._2.map(x => {
        val wikiArticle = wikiArticlesMap(x._2)
        val excerptTuple = findExactWikipediaExcerpt(x._1, wikiArticle.text, n)
        val excerpt = combineToSpan(excerptTuple._1, excerptTuple._2, excerptTuple._3)
        new WikiExcerpt(wikiArticle.title, wikiArticle.docId, startInInputText, endInInputText, excerpt,
          excerptTuple._4 , excerptTuple._5)
      })

      new WikiPlagiarism(x._2, excerpts)
    }).toList
  }

  private def combineToSpan(before: String, plagiarism: String, after: String): String = {
    var span ="""<span class="wiki_plag">"""
    s"[...] $before$span$plagiarism</span>$after [...]"
  }

  /** Finds the exact excerpt for the tokenized match from the given wikipedia article.
    *
    * @param tokenizedMatch the tokenzied match
    * @param wikiText       the wikipedia article from which the tokenized match originated
    * @param n              number of characters before and after the plagiarism text
    *
    * @return a tuple of (n-chars before, plagiarism, n-chars after)
    **/
  private def findExactWikipediaExcerpt(tokenizedMatch: Vector[String], wikiText: String, n: Int): (String, String, String, Int, Int) = {
    try {
      findExactWikipediaExcerptOrThrow(tokenizedMatch, wikiText, n)
    } catch {
      //if there was an exception write the stack trace and just concatenate the tokens
      case e: Exception =>
        e.printStackTrace()
        ("", tokenizedMatch.mkString(" "), "", 0, 0)
    }
  }

  private def findExactWikipediaExcerptOrThrow(tokenizedMatch: Vector[String], wikiText: String, n: Int): (String, String, String, Int, Int) = {
    val wikiTextLower = wikiText.toLowerCase
    //find all positions of each word and remove those with no results
    val nonEmptyOrderedTokensPositionsWithTokens = tokenizedMatch.map(x => (x, Functions.allIndicesOf(wikiTextLower, x))).filter(_._2.nonEmpty).toList

    //less than two words have been found, throw an exception
    if (nonEmptyOrderedTokensPositionsWithTokens.size < 2)
      throw new NoSuchElementException("Less than 2 tokens were found.")

    val firstWordPositions = nonEmptyOrderedTokensPositionsWithTokens.head._2
    //add the length of the word to get the position of the end of the word, not the start
    val otherWordsPositions = nonEmptyOrderedTokensPositionsWithTokens.tail.map(
      positionTokenPair => positionTokenPair._2.map(position => position + positionTokenPair._1.length))

    //build start,end, count of words founds tuples
    val startEndPositionCountTuples = firstWordPositions.map(startingPosition => {
      val endPositionWithNumberOfWordsFound = otherWordsPositions.foldLeft(startingPosition, 0) {
        (accumulator, current) => {
          val previousPosition = accumulator._1
          val closestPositionToPrevious = current.find(_ > previousPosition)
          //no position exists which are after the previous one,
          // continue with the next query word without modifying the positions
          if (closestPositionToPrevious.isEmpty)
            accumulator
          else
            (closestPositionToPrevious.get, accumulator._2 + 1)
        }
      }
      (startingPosition, endPositionWithNumberOfWordsFound._1, endPositionWithNumberOfWordsFound._2)
    })

    //get the maximum number of words found
    val maxWordsCounds = startEndPositionCountTuples.map(x => x._3).max
    //from the result having the max number of word get the result with the smallest length from start to end
    val best = startEndPositionCountTuples.filter(x => x._3 == maxWordsCounds).minBy(x => x._2 - x._1)

    //if it has 0 words throw an exception
    if (best._3 == 0)
      throw new NoSuchElementException("0 Element best match.")

    val plagStartPosition = best._1
    val plagEndPosition = best._2
    //clip to avoid out of bounds exceptions
    val beforeStartPosition = math.max(plagStartPosition - n, 0)
    val afterEndPosition = math.min(plagEndPosition + n, wikiText.length - 1)

    val before = wikiText.slice(beforeStartPosition, plagStartPosition).replace("\n", "")
    val plag = wikiText.slice(plagStartPosition, plagEndPosition).replace("\n", "")
    val after = wikiText.slice(plagEndPosition, afterEndPosition).replace("\n", "")

    (before, plag, after, plagStartPosition.toInt, plagEndPosition.toInt)
  }
}