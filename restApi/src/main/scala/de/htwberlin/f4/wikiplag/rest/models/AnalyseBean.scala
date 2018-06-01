package de.htwberlin.f4.wikiplag.rest.models

import de.htwberlin.f4.wikiplag.plagiarism.models.{WikiExcerpt, WikiPlagiarism}
import de.htwberlin.f4.wikiplag.utils.Functions

/** The bean returned by the rest api.
  *
  * @see README.md or mock.json in webapp. */
@SerialVersionUID(1)
case class AnalyseBean(var plags: List[WikiPlagiarism], var elapsed_time: Long, var tagged_input_text: String = "")
  extends Serializable {
}
