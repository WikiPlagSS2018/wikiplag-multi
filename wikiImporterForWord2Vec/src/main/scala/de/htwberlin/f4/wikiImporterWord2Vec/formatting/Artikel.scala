package de.htwberlin.f4.wikiImporterWord2Vec.formatting

class Artikel(val pageID:String, val revID:String, val title: String, val text: String) {

  var template = "<mediawiki xmlns=\"http://www.mediawiki.org/xml/export-0.10/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.mediawiki.org/xml/export-0.10/ http://www.mediawiki.org/xml/export-0.10.xsd\" version=\"0.10\" xml:lang=\"de\">\n  <siteinfo>\n    <sitename>Wikipedia</sitename>\n    <dbname>dewiki</dbname>\n    <base>https://de.wikipedia.org/wiki/Wikipedia:Hauptseite</base>\n    <generator>MediaWiki 1.31.0-wmf.1</generator>\n    <case>first-letter</case>\n    <namespaces>\n      <namespace key=\"-2\" case=\"first-letter\">Medium</namespace>\n      <namespace key=\"-1\" case=\"first-letter\">Spezial</namespace>\n      <namespace key=\"0\" case=\"first-letter\" />\n      <namespace key=\"1\" case=\"first-letter\">Diskussion</namespace>\n      <namespace key=\"2\" case=\"first-letter\">Benutzer</namespace>\n      <namespace key=\"3\" case=\"first-letter\">Benutzer Diskussion</namespace>\n      <namespace key=\"4\" case=\"first-letter\">Wikipedia</namespace>\n      <namespace key=\"5\" case=\"first-letter\">Wikipedia Diskussion</namespace>\n      <namespace key=\"6\" case=\"first-letter\">Datei</namespace>\n      <namespace key=\"7\" case=\"first-letter\">Datei Diskussion</namespace>\n      <namespace key=\"8\" case=\"first-letter\">MediaWiki</namespace>\n      <namespace key=\"9\" case=\"first-letter\">MediaWiki Diskussion</namespace>\n      <namespace key=\"10\" case=\"first-letter\">Vorlage</namespace>\n      <namespace key=\"11\" case=\"first-letter\">Vorlage Diskussion</namespace>\n      <namespace key=\"12\" case=\"first-letter\">Hilfe</namespace>\n      <namespace key=\"13\" case=\"first-letter\">Hilfe Diskussion</namespace>\n      <namespace key=\"14\" case=\"first-letter\">Kategorie</namespace>\n      <namespace key=\"15\" case=\"first-letter\">Kategorie Diskussion</namespace>\n      <namespace key=\"100\" case=\"first-letter\">Portal</namespace>\n      <namespace key=\"101\" case=\"first-letter\">Portal Diskussion</namespace>\n      <namespace key=\"828\" case=\"first-letter\">Modul</namespace>\n      <namespace key=\"829\" case=\"first-letter\">Modul Diskussion</namespace>\n      <namespace key=\"2300\" case=\"first-letter\">Gadget</namespace>\n      <namespace key=\"2301\" case=\"first-letter\">Gadget Diskussion</namespace>\n      <namespace key=\"2302\" case=\"case-sensitive\">Gadget-Definition</namespace>\n      <namespace key=\"2303\" case=\"case-sensitive\">Gadget-Definition Diskussion</namespace>\n      <namespace key=\"2600\" case=\"first-letter\">Thema</namespace>\n    </namespaces>\n  </siteinfo>\n" +
    "<page>\n    " +
    "<title>" +
    title +
    "</title>\n    <ns>0</ns>\n    " +
    "<id>" +
    pageID +
    "</id>\n    " +
    "<revision>\n      <id>" +
    revID +
    "</id>\n      <parentid>166436400</parentid>\n      <timestamp>2017-08-28T11:21:16Z</timestamp>\n      <contributor>\n        <ip>81.14.207.162</ip>\n      </contributor>\n      <comment>/* Geschichte */ falscher Genitiv</comment>\n      <model>wikitext</model>\n      <format>text/x-wiki</format>\n      " +
    "<text xml:space=\"preserve\">" +
    text+
    "</text>\n      <sha1>4j2lxr70l8o8y70t4lfwdyyd7d2ybq5</sha1>\n    " +
    "</revision>\n  " +
    "</page>  \n</mediawiki>"


  override def toString = s"($template)"


  def getCleanText(in:String){

  }


}
