package de.htwberlin.f4.wikiImporterWord2Vec.formatting

import java.util.regex.{Matcher, Pattern}

import org.unbescape.html.HtmlEscape._

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

object WikiParser extends Parser {

  val TEMPLATE_MARKER = "|TEMPLATE|"
  /**
    * @todo Tags die noch fehlen.
    * section Tag <section></section>
    */

  /**
    * Erlaubte HTML-Tags in Wikipedia
    * https://en.wikipedia.org/wiki/Help:HTML_in_wikitext
    *
    *
    * Tag und Inhalt kann entfernt werden.
    * Kann durch einen leeren String ersetzt werden.
    */
  val CODE_TAG_PATTERN = """(?s)<code(>| .*?>).*?</code>""".r
  val RUBY_TAG_PATTERN = """(?s)<ruby(>| .*?>).*?</ruby>""".r
  val RB_TAG_PATTERN = """(?s)<rb(>| .*?>).*?</rb>""".r
  val TIME_TAG_PATTERN = """(?s)<time(>| .*?>).*?</time>""".r
  val VAR_TAG_PATTERN = """(?s)<var(>| .*?>).*?</var>""".r
  val COMMENT_PATTERN = """(?s)<!--.*?-->""".r
  val TABLE_TAG_PATTERN = """(?s)<table(>| .*?>).*?</table>""".r
  val GALLERY_TAG_PATTERN = """(?s)<gallery(>| .*?>).*?</gallery>""".r
  val NO_INCLUDE_TAG_PATTERN = """(?s)<noinclude(>| .*?>).*?</noinclude>""".r
  val ONLY_INCLUDE_TAG_PATTERN = """(?s)<onlyinclude(>| .*?>).*?</onlyinclude>""".r
  val INCLUDE_ONLY_TAG_PATTERN = """(?s)<includeonly(>| .*?>).*?</includeonly>""".r
  val CE_TAG_PATTERN = """(?s)<ce(>| .*?>).*?</ce>""".r
  val GRAPH_TAG_PATTERN = """(?s)<graph(>| .*?>).*?</graph>""".r
  val HIERO_TAG_PATTERN = """(?s)<hiero(>| .*?>).*?</hiero>""".r
  val IMAGE_MAP_TAG_PATTERN = """(?s)<imagemap(>| .*?>).*?</imagemap>""".r
  val INDICATOR_TAG_PATTERN = """(?s)<indicator(>| .*?>).*?</indicator>""".r
  val INPUT_BOX_TAG_PATTERN = """(?s)<inputbox(>| .*?>).*?</inputbox>""".r
  val MATH_TAG_PATTERN = """(?s)<math(>| .*?>).*?</math>""".r
  val MATH_CHEM_TAG_PATTERN = """(?s)<math chem(>| .*?>).*?</math chem>""".r
  val CATEGORY_TREE_TAG_PATTERN = """(?s)<categorytree(>| .*?>).*?</categorytree>""".r
  val SYNTAX_HIGH_TAG_PATTERN = """(?s)<syntaxhighlight(>| .*?>).*?</syntaxhighlight>""".r
  val SCORE_TAG_PATTERN = """(?s)<score(>| .*?>).*?</score>""".r
  val REF_TAG_PATTERN = """(?s)<ref(>| [^/]*?>).*?</ref>""".r
  val REFERENCES_TAG_PATTERN = """(?s)<references(>| .*?>).*?</references>""".r
  val SOURCE_TAG_PATTERN = """(?s)<source(>| .*?>).*?</source>""".r
  val TIME_LINE_TAG_PATTERN = """(?s)<timeline(>| .*?>).*?</timeline>""".r
  val TEMPLATE_DATA_TAG_PATTERN = """(?s)<templatedata(>| .*?>).*?</templatedata>""".r

  /**
    * Tag entfernen, Inhalt behalten.
    */
  val H1_TAG_PATTERN =
    """(?s)<h1(>| .*?>)(.*?)</h1>""".r
  val H2_TAG_PATTERN = """(?s)<h2(>| .*?>)(.*?)</h2>""".r
  val H3_TAG_PATTERN = """(?s)<h3(>| .*?>)(.*?)</h3>""".r
  val H4_TAG_PATTERN = """(?s)<h4(>| .*?>)(.*?)</h4>""".r
  val H5_TAG_PATTERN = """(?s)<h5(>| .*?>)(.*?)</h5>""".r
  val H6_TAG_PATTERN = """(?s)<h6(>| .*?>)(.*?)</h6>""".r
  val P_TAG_PATTERN = """(?s)<p(>| .*?>)(.*?)</p>""".r
  val ABBR_TAG_PATTERN = """(?s)<abbr(>| .*?>)(.*?)</abbr>""".r
  val BOLD_TAG_PATTERN = """(?s)<b(>| .*?>)(.*?)</b>""".r
  val BDI_TAG_PATTERN = """(?s)<bdi(>| .*?>)(.*?)</bdi>""".r
  val BDO_TAG_PATTERN = """(?s)<bdo(>| .*?>)(.*?)</bdo>""".r
  val BLOCKQUOTE_TAG_PATTERN = """(?s)<blockquote(>| .*?>)(.*?)</blockquote>""".r
  val CITE_TAG_PATTERN = """(?s)<cite(>| .*?>)(.*?)</cite>""".r
  val DATA_TAG_PATTERN = """(?s)<data(>| .*?>)(.*?)</data>""".r
  val DEL_TAG_PATTERN = """(?s)<del(>| .*?>)(.*?)</del>""".r
  val DFN_TAG_PATTERN = """(?s)<dfn(>| .*?>)(.*?)</dfn>""".r
  val EM_TAG_PATTERN = """(?s)<em(>| .*?>)(.*?)</em>""".r
  val I_TAG_PATTERN = """(?s)<i(>| .*?>)(.*?)</i>""".r
  val INS_TAG_PATTERN = """(?s)<ins(>| .*?>)(.*?)</ins>""".r
  val KBD_TAG_PATTERN = """(?s)<kbd(>| .*?>)(.*?)</kbd>""".r
  val MARK_TAG_PATTERN = """(?s)<mark(>| .*?>)(.*?)</mark>""".r
  val Q_TAG_PATTERN = """<q(>| .*?>)(.*?)</q>""".r
  val STRIKE_OUT_TAG_PATTERN = """(?s)<s(>| .*?>)(.*?)</s>""".r
  val SAMP_TAG_PATTERN = """(?s)<samp(>| .*?>)(.*?)</samp>""".r
  val SMALL_TAG_PATTERN = """(?s)<small(>| .*?>)(.*?)</small>""".r
  val STRONG_TAG_PATTERN = """(?s)<strong(>| .*?>)(.*?)</strong>""".r
  val SUP_TAG_PATTERN = """(?s)<sup(>| .*?>)(.*?)</sup>""".r
  val SUB_TAG_PATTERN = """(?s)<sub(>| .*?>)(.*?)</sub>""".r
  val UNDERLINE_TAG_PATTERN = """(?s)<u(>| .*?>)(.*?)</u>""".r
  val POEM_TAG_PATTERN = """(?s)<poem(>| .*?>)(.*?)</poem>""".r
  val PRE_TAG_PATTERN = """(?s)<pre(>| .*?>)(.*?)</pre>""".r
  val CHARINSERT_TAG_PATTERN = """(?s)<charinsert(>| .*?>)(.*?)</charinsert>""".r
  // Obsolete aber noch in Verwendung.
  val BIG_TAG_PATTERN = """(?s)<big(>| .*?>)(.*?)</big>""".r
  val CENTER_TAG_PATTERN = """(?s)<center(>| .*?>)(.*?)</center>""".r
  val FONT_TAG_PATTERN = """(?s)<font(>| .*?>)(.*?)</font>""".r
  val STRIKE_TAG_PATTERN = """(?s)<strike(>| .*?>)(.*?)</strike>""".r
  val TT_TAG_PATTERN = """(?s)<tt(>| .*?>)(.*?)</tt>""".r

  /**
    * Tags die sich selber beinhalten koennen
    */
  val SPAN_TAG_PATTERN = """(?s)<span(>| .*?>)(.*?)</span>""".r
  val DIV_TAG_PATTERN = """(?s)<div(>| .*?>)(.*?)</div>""".r
  val NOWIKI_TAG_PATTERN = """(?s)<nowiki(>| .*?>)(.*?)</nowiki>""".r

  /**
    * Weitere Regexs fuer das Entfernen von HTML-Tags.
    *
    * Listen
    * Loescht die ganze Liste (inklusive Eintraege). Koennen durch leeren String ersetzt werden.
    */
  val DL_TAG_PATTERN =
    """(?s)<dl(>| .*?>).*?</dl>""".r
  val OL_TAG_PATTERN = """(?s)<ol(>| .*?>).*?</ol>""".r
  val UL_TAG_PATTERN = """(?s)<ul(>| .*?>).*?</ul>""".r

  /**
    * Einzelne / selbstschliessende Tags.
    */
  // Muss durch einen leeren String ersetzt werden.
  val WBR_TAG_PATTERN =
  """<wbr>""".r
  val NOWIKI_SINGLE_TAG_PATTERN = """<nowiki />""".r
  val B_TAG_PATTERN = """<b/>""".r

  // Kann durch einen leeren String ersetzt werden.
  val HR_TAG_PATTERN =
    """<hr>""".r
  val LINE_BREAKS_TAG_PATTERN = """(<br(>| />|/>|.>))|(</br>)""".r
  val REF_TAG_SINGLE_PATTERN = """<ref(/>| [^<>]*?/>)""".r
  val REFERENCES_SINGLE_TAG_PATTERN = """<references(/>| [^<>]*?/>)""".r

  /**
    * WikiMarkup
    * https://en.wikipedia.org/wiki/Help:Wiki_markup
    * Inline
    */
  // Kann durch einen leeren String ersetzt werden.
  val BOLD_FORMATTING_PATTERN =
  """'''""".r
  val ITALICS_FORMATTING_PATTERN = """''""".r
  val TABLE_PATTERN = """(?s)\{\|.*?\|\}""".r
  val INTERNAL_WIKI_LINK = """\[\[[^\]:]*?\|(.*?)\]\]""".r
  val INTERNAL_WIKI_LINK_WITHOUT_ALT = """\[\[(([^\]\[\|: ]*? [^\]\[\|]*?)|([^\]\[\|: ]*?))\]\]""".r
  val SPECIAL_INTERNAL_WIKI_LINK = """\[\[[^\]\[ ]*:[^\]\[]*\]\]""".r
  val EXTERNAL_LINKS_PATTERN = """(^|[^\[])\[[^\[\]]*?\]([^\]]|$)""".r
  val EXTERNAL_LINKS_VAR_2_PATTERN = """(^|[^\[])\[[^\[\]]*? ([^\[\]]*?)\]([^\]]|$)""".r
  val SECTION_PATTERN = """^\=.{0,}$""".r
  val HR_WIKI_MARKUP_PATTERN = """^----[^A-Za-z0-9]{0,}$""".r

  val TEMPLATE_PATTERN = """(?s)[\{]{2}[^\{\}]*?[\}]{2}""".r

  /**
    * Text bereinigen.
    */
  val DUPLICATE_WHITESPACE_PATTERN =
    """ {2,}""".r
  //  val WORD_PATTERN = """(\p{L}+)""".r
  // changed pattern to get words and numbers
  val WORD_PATTERN = """((\p{L}+)|(\p{N}+))""".r
  val NEW_LINE_PATTERN = """[\n]{3,}""".r
  val EMPTY_ORDERED_LIST_ELEMENT_PATTERN = """^\#[^A-Za-z0-9]{0,}$""".r
  val EMPTY_UNORDERED_LIST_ELEMENT_PATTERN = """^\*[^A-Za-z0-9]{0,}$""".r
  val REDIRECT_PATTERN = """^\#REDIRECT.{0,}$""".r
  val EMPTY_DEFINITION_PATTERN = """^\:[^A-Za-z0-9]{0,}$""".r
  val EMPTY_TERM_PATTERN = """^\;[^A-Za-z0-9]{0,}$""".r

  /**
    *
    * http://stackoverflow.com/questions/60160/how-to-escape-text-for-regular-expression-in-java
    *
    * @param page
    * @param regexPattern
    * @param matchListIterator
    * @param groupId
    * @return
    */
  @tailrec def replaceMatchWithGroup(page: String,
                                     regexPattern: Regex,
                                     matchListIterator: Iterator[Regex.Match],
                                     groupId: Int): String = {
    if (matchListIterator.isEmpty)
      page
    else {
      val replaceText = matchListIterator.next().group(groupId)
      replaceMatchWithGroup(
        regexPattern.replaceFirstIn(page, Matcher.quoteReplacement(replaceText)),
        regexPattern,
        matchListIterator,
        groupId)
    }
  }

  /**
    *
    *
    * @param page
    * @param regexList
    * @param regexListIndex
    * @param groupId
    * @return
    */
  @tailrec def removeMatchWithGroup(page: String, regexList: List[Regex], regexListIndex: Int, groupId: Int): String = {
    if (regexListIndex == regexList.size)
      page
    else {
      val matchListIterator = regexList(regexListIndex).findAllMatchIn(page)
      removeMatchWithGroup(
        replaceMatchWithGroup(page, regexList(regexListIndex), matchListIterator, groupId),
        regexList,
        regexListIndex + 1,
        groupId)
    }
  }

  /**
    * Entfernt Zeilen die mit einer Wikisyntax beginnen.
    *
    * Beispiel:
    * input: page = "* Listenelement\nText und mehr", regex = "^\*.{0,}$"
    * output: "Text und mehr"
    *
    *
    * @param page
    * @param regex
    * @return
    */
  def removeWikiMarkup(page: String, regex: Regex): String = {
    val lines = page.split("\n")
    lines.map(line => if (regex.findFirstMatchIn(line).isDefined) "" else line).mkString("\n")
  }

  /**
    *
    * @param page
    * @param wordList
    * @param groupdId
    * @return
    */
  @tailrec def extractWords(page: String, wordList: ListBuffer[String], groupdId: Int): List[String] = {
    val matchIterator = WORD_PATTERN.findFirstMatchIn(page)
    if (matchIterator.isEmpty)
      wordList.toList
    else {
      wordList.append(matchIterator.get.group(groupdId))
      extractWords(WORD_PATTERN.replaceFirstIn(page, ""), wordList, groupdId)
    }
  }

  /**
    *
    * @param page
    * @param removeChars
    * @param regexListIndex
    * @param replace
    * @return
    */
  @tailrec def replacePattern(page: String, removeChars: List[Regex], regexListIndex: Int, replace: String): String = {
    if (regexListIndex == removeChars.size)
      page
    else
      replacePattern(
        removeChars(regexListIndex).replaceAllIn(page, replace),
        removeChars,
        regexListIndex + 1,
        replace)
  }

  /**
    *
    * @param page
    * @return
    */
  def removeExternalLinks(page: String): String = {
    val matchListIterator = EXTERNAL_LINKS_VAR_2_PATTERN.findAllMatchIn(page)

    @tailrec def replaceMatch(page: String, matchList: Iterator[Regex.Match]): String = {
      if (matchList.isEmpty)
        page
      else {
        val match_group = matchList.next()
        val replaceText = match_group.group(1) + match_group.group(2) + match_group.group(3)
        replaceMatch(
          EXTERNAL_LINKS_VAR_2_PATTERN.replaceFirstIn(
            page,
            Matcher.quoteReplacement(replaceText)),
          matchList)
      }
    }
    replaceMatch(page, matchListIterator)
  }

  /**
    *
    * @param page
    * @param tag
    * @param tagAsRegex
    * @param beginSearchIndex
    * @return
    */
  @tailrec def replaceNestedTags(page: String,
                                 tag: String,
                                 tagAsRegex: Regex,
                                 beginSearchIndex: Int): String = {
    val indexOfMatch = page.lastIndexOf(tag, beginSearchIndex)
    if (indexOfMatch == -1)
      page
    else {
      if (indexOfMatch == beginSearchIndex)
        replaceNestedTags(page.substring(0, indexOfMatch) +
          removeMatchWithGroup(page.substring(indexOfMatch, page.length), List(tagAsRegex), 0, 2),
          tag,
          tagAsRegex,
          indexOfMatch - tag.length)
      else
        replaceNestedTags(page.substring(0, indexOfMatch) +
          removeMatchWithGroup(page.substring(indexOfMatch, page.length), List(tagAsRegex), 0, 2),
          tag,
          tagAsRegex,
          indexOfMatch)
    }
  }

  /**
    *
    * @param page
    * @param tag
    * @param tagAsRegex
    * @param beginSearchIndex
    * @param replaceWith
    * @return
    */
  @tailrec def removeNestedTags(page: String,
                                tag: String,
                                tagAsRegex: Regex,
                                beginSearchIndex: Int,
                                replaceWith: String): String = {
    val indexOfMatch = page.lastIndexOf(tag, beginSearchIndex)
    if (indexOfMatch == -1)
      page
    else {
      if (indexOfMatch == beginSearchIndex)
        removeNestedTags(page, tag, tagAsRegex, indexOfMatch - tag.length, replaceWith)
      else
        removeNestedTags(page.substring(0, indexOfMatch) +
          replacePattern(page.substring(indexOfMatch, page.length), List(tagAsRegex), 0, replaceWith),
          tag,
          tagAsRegex,
          indexOfMatch,
          replaceWith)
    }
  }

  /**
    * Erzeugt aus einer WikiDumpXml-Page (Page-XML-Objekt als String) eine HTML und Wiki-Markup bereinigte Seite.
    *
    * Entfernt werden:
    * - alle HTML-Tags die Wikipedia als HTML-Tags erlaubt (https://en.wikipedia.org/wiki/Help:HTML_in_wikitext)
    * - Die HTML-Tags:  &lt;code&gt;, &lt;ruby&gt;, &lt;rb&gt;, &lt;time&gt;, &lt;var&gt;, &lt;table&gt;, &lt;gallery&gt;,
    * &lt;noinclude&gt;, &lt;onlyinclude&gt;, &lt;includeonly&gt;, &lt;ce&gt;, &lt;graph&gt;, &lt;hiero&gt;,
    * &lt;imagemap&gt;, &lt;indicator&gt;, &lt;inputbox&gt;, &lt;math&gt;, &lt;math chem&gt;, &lt;categorytree&gt;,
    * &lt;syntaxhighlight&gt;, &lt;score&gt;, &lt;ref&gt;, &lt;references&gt;, &lt;source&gt;, &lt;timeline&gt;,
    * &lt;templatedata&gt; HTML-Kommentare und den Inhalt, den sie umschliessen
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
    * pageContent = String("1997 kam ''die'' Parodie An [[Alan Smithee Film]]: Burn Hollywood {{Text}}")
    *
    * Output:
    * String("1997 kam die Parodie An Alan Smithee Film: Burn Hollywood TEMPLATE")
    *
    * @param pageContent Page-XML-Objekt als String.
    * @return Die HTML und Wiki-Markup bereinigte Seite.
    */
  override def parseXMLWikiPage(pageContent: String): String = {
    val page = pageContent :: List[String]()
    try {
      val displayPages = page
        .map(page => removeNestedTags(page, "{{", TEMPLATE_PATTERN, page.length, TEMPLATE_MARKER))
        .map(replacePattern(_, List(TABLE_PATTERN), 0, " "))
        .map(replacePattern(
          _,
          List(COMMENT_PATTERN, TABLE_TAG_PATTERN, GALLERY_TAG_PATTERN, MATH_TAG_PATTERN, CODE_TAG_PATTERN,
            SYNTAX_HIGH_TAG_PATTERN, SCORE_TAG_PATTERN, NO_INCLUDE_TAG_PATTERN, ONLY_INCLUDE_TAG_PATTERN,
            INCLUDE_ONLY_TAG_PATTERN, CATEGORY_TREE_TAG_PATTERN, CE_TAG_PATTERN, GRAPH_TAG_PATTERN, HIERO_TAG_PATTERN,
            IMAGE_MAP_TAG_PATTERN, INDICATOR_TAG_PATTERN, INPUT_BOX_TAG_PATTERN, MATH_CHEM_TAG_PATTERN, REF_TAG_PATTERN,
            REFERENCES_TAG_PATTERN, SOURCE_TAG_PATTERN, TIME_LINE_TAG_PATTERN, TEMPLATE_DATA_TAG_PATTERN,
            TIME_TAG_PATTERN, VAR_TAG_PATTERN, RUBY_TAG_PATTERN, RB_TAG_PATTERN, DL_TAG_PATTERN, OL_TAG_PATTERN,
            UL_TAG_PATTERN, B_TAG_PATTERN, REF_TAG_SINGLE_PATTERN, REFERENCES_SINGLE_TAG_PATTERN, HR_TAG_PATTERN,
            WBR_TAG_PATTERN, NOWIKI_SINGLE_TAG_PATTERN), 0, ""))
        .map(removeMatchWithGroup(
          _,
          List(H1_TAG_PATTERN, H2_TAG_PATTERN, H3_TAG_PATTERN, H4_TAG_PATTERN, H5_TAG_PATTERN, H6_TAG_PATTERN,
            SMALL_TAG_PATTERN, BLOCKQUOTE_TAG_PATTERN, SUP_TAG_PATTERN, SUB_TAG_PATTERN, UNDERLINE_TAG_PATTERN,
            DEL_TAG_PATTERN, INS_TAG_PATTERN, STRIKE_OUT_TAG_PATTERN, P_TAG_PATTERN, ABBR_TAG_PATTERN, BOLD_TAG_PATTERN,
            BDI_TAG_PATTERN, BDO_TAG_PATTERN, CITE_TAG_PATTERN, DATA_TAG_PATTERN, DFN_TAG_PATTERN, EM_TAG_PATTERN,
            I_TAG_PATTERN, KBD_TAG_PATTERN, MARK_TAG_PATTERN, Q_TAG_PATTERN, SAMP_TAG_PATTERN, STRONG_TAG_PATTERN,
            BIG_TAG_PATTERN, CENTER_TAG_PATTERN, FONT_TAG_PATTERN, STRIKE_OUT_TAG_PATTERN, TT_TAG_PATTERN,
            DIV_TAG_PATTERN, POEM_TAG_PATTERN, PRE_TAG_PATTERN), 0, 2))
        .map(page => replaceNestedTags(page, "<nowiki", NOWIKI_TAG_PATTERN, page.length))
        .map(page => replaceNestedTags(page, "<span", SPAN_TAG_PATTERN, page.length))
        .map(page => replaceNestedTags(page, "<div", DIV_TAG_PATTERN, page.length))
        .map(removeMatchWithGroup(_, List(INTERNAL_WIKI_LINK, INTERNAL_WIKI_LINK_WITHOUT_ALT), 0, 1))
        .map(replacePattern(_, List(BOLD_FORMATTING_PATTERN, ITALICS_FORMATTING_PATTERN), 0, ""))
        .map(replacePattern(_, List(LINE_BREAKS_TAG_PATTERN), 0, " "))
        .map(removeExternalLinks(_))
        .map(replacePattern(_, List(SPECIAL_INTERNAL_WIKI_LINK), 0, ""))
        .map(unescapeHtml(_))
        .map(removeWikiMarkup(_, HR_WIKI_MARKUP_PATTERN))
        .map(removeWikiMarkup(_, EMPTY_UNORDERED_LIST_ELEMENT_PATTERN))
        .map(removeWikiMarkup(_, EMPTY_ORDERED_LIST_ELEMENT_PATTERN))
        .map(removeWikiMarkup(_, REDIRECT_PATTERN))
        .map(removeWikiMarkup(_, EMPTY_DEFINITION_PATTERN))
        .map(removeWikiMarkup(_, EMPTY_TERM_PATTERN))
        .map(replacePattern(_, List(DUPLICATE_WHITESPACE_PATTERN), 0, " "))
        .map(replacePattern(_, List(NEW_LINE_PATTERN), 0, "\n\n"))
      displayPages.head
    } catch {
      case e: IllegalArgumentException =>
        println(e.toString + " in Page: " + page)
        ""
    }
  }

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
    * pageContent = String("1997 kam die Parodie An Alan Smithee Film: Burn Hollywood")
    *
    * Output:
    * List[String]("kam", "die", "Parodie", "An", "Alan", "Smithee", "Film", "Burn", "Hollywood")
    * List[String](wort_1, wort_2, ...)
    *
    * @param pageContent Der Textinhalt.
    * @return Alle Woerter aus dem Text als Liste.
    */
  override def extractWikiDisplayText(pageContent: String): List[String] = {
    val page = pageContent :: List[String]()
    page.map(removeWikiMarkup(_, SECTION_PATTERN))
      .map(replacePattern(_, List(new Regex(Pattern.quote(TEMPLATE_MARKER))), 0, " "))
      .map(extractWords(_, ListBuffer(), 0)).head
  }

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
    *
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
  override def extractPlainText(pageContent: String): List[String] = {
    extractWords(pageContent, ListBuffer(), 0)
  }

}
