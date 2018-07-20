package de.htwberlin.f4.wikiImporterWord2Vec.formatting

trait Parser {

  /**
    * Extrahiert alle Woerter aus einem Text.
    * Ein Wort kann:
    * aus einem Zeichen bestehen,
    * Unicode Buchstaben beinhalten (sowohl Gross- als auch Kleinschreibung),
    * nur aus Unicode Buchstaben bestehen (sowohl Gross- als auch Kleinschreibung)
    *
    * Als Unicode Buchstaben gelten alle Buchstaben, die Java nach dem Regex "\p{L}" als Buchstabe interpretiert.
    * http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html
    *
    * Die Reihenfolge der extrahierten Woerter in der Liste ist gleich der Reihenfolge der Woerter im Text.
    *
    * Beispiel:
    *s
    * Input:
    * pageContent = String("1997 kam die Parodie An Alan Smithee Film: Burn Hollywood")
    *
    * Output:
    * List[String]("kam", "die", "Parodie", "An", "Alan", "Smithee", "Film", "Burn", "Hollywood")
    * List[String](wort_1, wort_2, ...)
    *
    * @param pageContent Der Textinhalt.
    * @return Alle Woerter aus dem Text als Liste.
    */
  def extractPlainText(pageContent: String): List[String]

  /**
    * Extrahiert alle Woerter aus einer mit der Methode "parseXMLWikiPage" vorgeparsten Wiki-Page.
    * Desweiteren werden zuvor alle Ueberschriften als auch alle Template-Marker entfernt und treten somit nicht in der
    * Ergebnis-Liste mit auf.
    *
    * Ein Wort kann:
    * aus einem Zeichen bestehen,
    * Unicode Buchstaben beinhalten (sowohl Gross- als auch Kleinschreibung),
    * nur aus Unicode Buchstaben bestehen (sowohl Gross- als auch Kleinschreibung)
    *
    * Als Unicode Buchstaben gelten alle Buchstaben, die Java nach dem Regex "\p{L}" als Buchstabe interpretiert.
    * http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html
    *
    * Als Ueberschriften gelten alle Zeilen, die mit dem Zeichen "=" beginnen.
    * Als Template-Marker gelten alle Woerter, die gleich der Zeichenkette "TEMPLATE" sind.
    *
    * Die Reihenfolge der extrahierten Woerter in der Liste ist gleich der Reihenfolge der Woerter in der Wiki-Page.
    *
    * Beispiel:
    *
    * Input:
    * pageContent = String("1997 kam die Parodie TEMPLATE An Alan Smithee Film: Burn Hollywood\n===Ueberschrift===\nText")
    *
    * Output:
    * List[String]("kam", "die", "Parodie", "An", "Alan", "Smithee", "Film", "Burn", "Hollywood", "Text")
    *
    * @param pageContent Der Textinhalt.
    * @return Alle Woerter aus dem Text als Liste.
    */
  def extractWikiDisplayText(pageContent: String): List[String]

  /**
    * Erzeugt aus einer WikiDumpXml-Page (Page-XML-Objekt als String) eine HTML und Wiki-Markup bereinigte Seite.
    *
    * Entfernt werden:
    * - alle HTML-Tags die Wikipedia als HTML-Tags erlaubt (https://en.wikipedia.org/wiki/Help:HTML_in_wikitext)
    * - Die HTML-Tags:  &lt;code&gt;, &lt;ruby&gt;, &lt;rb&gt;, &lt;time&gt;, &lt;var&gt;, &lt;table&gt;, &lt;gallery&gt;,
    * &lt;noinclude&gt;, &lt;onlyinclude&gt;, &lt;includeonly&gt;, &lt;ce&gt;, &lt;graph&gt;, &lt;hiero&gt;,
    * &lt;imagemap&gt;, &lt;indicator&gt;, &lt;inputbox&gt;, &lt;math&gt;, &lt;math chem&gt;, &lt;categorytree&gt;,
    * &lt;syntaxhighlight&gt;, &lt;score&gt;, &lt;ref&gt;, &lt;references&gt;, &lt;source&gt;, &lt;timeline&gt;,
    * &lt;templatedata&gt;, HTML-Kommentare und den Inhalt, den sie umschliessen
    * - alle Zeichen die als Wiki-Markups interpretiert werden (https://en.wikipedia.org/wiki/Help:Wiki_markup)
    * - Tabellen (und deren Inhalt), die durch Wiki-Markups gekennzeichnet sind
    * - leere Listenelemente
    * - Interne Wiki-Links
    *
    *
    * Ausnahmen:
    * - Die Kennzeichnung von Listen (Zeilen, die mit dem Zeichen "*" beginnen) sowie die Kennzeichnung von Ueberschriften
    * (Zeilen, die mit dem Zeichen "=" beginnen) bleiben enthalten.
    * - Wiki-Templates werden durch die Zeichenkette "TEMPLATE" ersetzt und nicht aufgeloest.
    * - Doppelte Leerzeichen werden durch ein Leerzeichen ersetzt.
    * - Externe Wiki-Links bleiben erhalten.
    *
    * Beispiel:
    *
    * Input:
    * pageContent = String("1997 kam ""die"" Parodie An [[Alan Smithee Film]]: Burn Hollywood {{Text}}")
    *
    * Output:
    * String("1997 kam die Parodie An Alan Smithee Film: Burn Hollywood TEMPLATE")
    *
    * @param pageContent Page-XML-Objekt als String.
    * @return Die HTML und Wiki-Markup bereinigte Seite.
    */
  def parseXMLWikiPage(pageContent: String): String

}
