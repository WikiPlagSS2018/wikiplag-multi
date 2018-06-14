package de.htwberlin.f4.wikiImporterWord2Vec.formatting

import sys.process._

object ArtikelParser {

  def compWithLibmagic(file:String){
    val result = "python3 wikiImporterForWord2Vec/src/main/scala/de/htwberlin/f4/wikiImporterWord2Vec/formatting/WikiExtractor.py " +
      file + " -o - --json -q" !



    val answ = (result+"").dropRight(1)
    println(answ)
  }

}
