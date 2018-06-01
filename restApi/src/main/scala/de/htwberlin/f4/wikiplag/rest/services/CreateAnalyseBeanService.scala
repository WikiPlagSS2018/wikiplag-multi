package de.htwberlin.f4.wikiplag.rest.services

import de.htwberlin.f4.wikiplag.plagiarism.models.WikiPlagiarism
import de.htwberlin.f4.wikiplag.rest.models.AnalyseBean
import de.htwberlin.f4.wikiplag.utils.Functions

/**
  * Creates the Analyse bean from the given list of plagiarisms and raw text.
  */
object CreateAnalyseBeanService {
  /**
    * Create the Analyse bean from the given list of plagiarisms and raw text.
    *
    * @param plags        the plagiarisms
    * @param rawInputText the raw input text for which the plagiarisms matched
    *
    */
  def createAnalyseBean(plags: List[WikiPlagiarism], rawInputText: String): AnalyseBean = {
    val model = AnalyseBean(plags, 0)

    if (rawInputText.isEmpty) {
      model.tagged_input_text = ""
      return model
    }
    if (plags.isEmpty) {
      model.tagged_input_text = rawInputText
      return model
    }

    var startEndPositionsLists = plags.flatMap(x => x.wiki_excerpts).map(x => (x.start, x.end)).unzip
    //concatenate them
    var plagIndices = (startEndPositionsLists._1 ::: startEndPositionsLists._2).distinct.sorted
    plagIndices = List(0) ::: plagIndices ::: List(rawInputText.length)
    var rawTextSplit = Functions.SplitByMultipleIndices(plagIndices, rawInputText)

    var span ="""<span id='%d' class='input_plag'>%s</span>"""

    model.tagged_input_text = rawTextSplit.zipWithIndex.map(x => {
      //even ones are plagiarisms
      if (x._2 % 2 == 1)
      // divide by 2 to get the correct index because we have 2 positions per word
        span.format(x._2 / 2, x._1)
      else
        x._1
    }).mkString("")

    model
  }

}
