package de.htwberlin.f4.wikiplag.rest.servlets

import de.htwberlin.f4.wikiplag.plagiarism.models.HyperParameters
import de.htwberlin.f4.wikiplag.plagiarism.PlagiarismFinder
import de.htwberlin.f4.wikiplag.plagiarism.services.WikiPlagiarismService
import de.htwberlin.f4.wikiplag.rest.models.{AnalyseBean, Text}
import de.htwberlin.f4.wikiplag.rest.services.CreateAnalyseBeanService
import de.htwberlin.f4.wikiplag.utils.CassandraParameters
import de.htwberlin.f4.wikiplag.utils.database.CassandraClient
import org.apache.spark.SparkContext
import org.joda.time.{DateTime, DateTimeZone}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json._

/** The wikiplag servlet. Serves the analyse and documents endpoints.
  *
  * The analyse endpoint forwards the passed json object to the analyse algorithm and returns
  * the matching plagiarisms.
  *
  * The documents endpoint returns the text of a wikipedia article with the given id.
  *
  * @note the documents endpoint isn't currently used, but is handy to have
  * @note this servlet uses spark and has only been run in local mode (on one node). If you want to run
  *       it with the spark2-submit command, remove the .setMaster("local") in the init file and specify
  *       a spark master on the command line with --master
  *
  */
class WikiplagServlet extends ScalatraServlet with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  /** The number of characters which are returned before and after each plagiarism excerpt.
    * These are not part of the plagiarism itself, but help to grasp the context of the article from which
    * the potential plagiarism was taken.
    */
  private val N_CHARS_BEFORE_AND_AFTER_PLAG_MATCH = 20

  /** The cassandra client. Used to query the database. */
  private var cassaandraClient: CassandraClient = _

  /** The plagiarism finder. Used to check a given text for plagiarisms. */
  private var plagiarismFinder: PlagiarismFinder = _

  override def init(): Unit = {
    println("WikiplagServlet: in init")

    //gets the cassandra parameters from the app.conf file.
    //for an example app.conf see the /resources/app.conf-sample file
    var cassandraParameter = CassandraParameters.readFromConfigFile("app.conf")
    //create a spark conf and context from the parameters and set the master to local
    //setting the master to local is required if the app is started using jetty:start
    var conf = cassandraParameter.toSparkConf("[Wikiplag]REST-API").setMaster("local")
    var sparkContext = new SparkContext(conf)

    cassaandraClient = new CassandraClient(sparkContext, cassandraParameter)
    plagiarismFinder = new PlagiarismFinder(cassaandraClient)
  }

  //called before each response
  before() {
    //allow cross-origin requests (CORS) from all sources
    //for more info about CORS see https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
    response.setHeader("Access-Control-Allow-Origin", "*")
  }
  /**
    * Returns the text of the document with the given id from wikipedia.
    *
    * @note the text can contain WIKI-Markdown and HTML tags
    */
  get("/documents/:id") {
    try {
      contentType = "text/plain"
      val wikiId = params("id").toInt
      println(s"get /documents/:id with $wikiId")
      val document = cassaandraClient.queryArticlesAsMap(List(wikiId))(wikiId).text
      val begin_index = document.indexOf("TEMPLATE")
      val end_index = document.indexOf("Weblinks")
      document.substring(begin_index, end_index)
    } catch {
      case e: StringIndexOutOfBoundsException => halt(404, "Not Found")
      case e: NumberFormatException => halt(400, "Invalid Id")
      case e: Exception =>
        e.printStackTrace()
        halt(500, "Internal server error")
    }
  }

  /**
    * Performs a plagiarism analysis on the text in the json payload and return a collection of matches.
    *
    * @example POST "http://localhost:8080/wikiplag/rest/analyse"
    *          ```json
    *          {
    *          text="TEXT-TO-CHECK"
    *          }
    *          ```
    * @see the README.md file for a sample of the output format
    * @see the restApi/test_scripts directory for some sample texts with a curl command to send them to the REST-API
    */
  post("/analyse") {
    println("WikiplagServlet: post /wikiplag/analyse")
    contentType = formats("json")
    try {
      val start_time = DateTime.now(DateTimeZone.UTC).getMillis
      // read json input file and converts it to Text object
      val jsonString = parse(request.body)
      val textObject = jsonString.extract[Text]
      //find the plagiarism using the default hyper parameters
      val plagiarisms = plagiarismFinder.findPlagiarisms(textObject.text, new HyperParameters())
      //get the wiki excerpts for the plagiarisms
      val plagiarismExcerpts = new WikiPlagiarismService(cassaandraClient).createWikiPlagiarisms(plagiarisms, N_CHARS_BEFORE_AND_AFTER_PLAG_MATCH)
      //add the span tags for the input text
      val result = CreateAnalyseBeanService.createAnalyseBean(plagiarismExcerpts, textObject.text)
      result.elapsed_time = DateTime.now(DateTimeZone.UTC).getMillis - start_time
      result
    } catch {
      case e: org.json4s.MappingException => halt(400, "Malformed JSON")
      case e: Exception =>
        e.printStackTrace()
        halt(500, "Internal Server Error")
    }
  }
}