package de.htwberlin.f4.wikiImporterWord2Vec.commandLine

object Commands {

  val commands = Seq(ExtractWikiText)

  sealed trait Command

  /** Represents the command to extract the text from the wikipedia articles. */
  case object ExtractWikiText extends Command


}
