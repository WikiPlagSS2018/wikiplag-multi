package de.htwberlin.f4.wikiImporterWord2Vec.commandLine

import de.htwberlin.f4.wikiImporterWord2Vec.cassandra.ParametersForCasandra
import org.apache.commons.cli.{GnuParser, OptionBuilder, OptionGroup, Options}


object OptionsForCommandLine {

  //throws a parse exception if something goes wrong
  def parse(args: Array[String]): (ParametersForCasandra, Commands.Command, String) = {
    val options = createCLiOptions()
    val commandLine = new GnuParser().parse(options, args)

    val parameters = ParametersForCasandra.readFromConfigFile("app.conf")

    // word2vec--> Import--> Id-Title-Sentence
    //--------------------
    if (commandLine.hasOption("extract-wikitext")) {
      val file = commandLine.getParsedOptionValue("extract-wikitext").asInstanceOf[String]
      return (parameters, Commands.ExtractWikiText, file)
    }
    //--------------------


    throw new IllegalArgumentException("Unknown action. Did you add a new action but forgot to add handling for it here?")

  }


  private def createCLiOptions() = {
    val options = new Options()

    /* Commands */
    val group = new OptionGroup()
    group.setRequired(false)

    OptionBuilder.withLongOpt("extract-wikitext")
    OptionBuilder.withDescription("Path to the XML-File containing the Wiki-Articles")
    OptionBuilder.hasArgs(1)
    OptionBuilder.withType(classOf[String])
    OptionBuilder.withArgName("wiki-file")
    group.addOption(OptionBuilder.create("ff"))

    options.addOptionGroup(group)
    options

  }



}
