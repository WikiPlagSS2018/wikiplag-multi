package de.htwberlin.f4.wikiplag.plagiarism.models

/** Represents a potential plagiarism wikipedia excerpt.
  *
  * @param title   the title from wikipedia
  * @param id   the id from wikipedia
  * @param start   the starting position in the input text (inclusive)
  * @param end     the end position in the input text (exclusive)
  * @param excerpt the wikipedia excerpt
  * @param start_of_plag_in_wiki   the starting position of the plag in the wikipedia text
  * @param end_of_plag_in_wiki     the end position of the plag in the wikipedia text
  *
  */
@SerialVersionUID(1)
class WikiExcerpt(val title: String, val id: Int, val start: Int, val end: Int, val excerpt: String,
                  val start_of_plag_in_wiki: Int, val end_of_plag_in_wiki: Int) extends Serializable {

  override def toString = s"WikiExcerpt(title=$title, start=$start, end=$end, excerpt=$excerpt, id=$id," +
    s" start_of_plag_in_wiki=$start_of_plag_in_wiki, end_of_plag_in_wiki=$end_of_plag_in_wiki)"
}
