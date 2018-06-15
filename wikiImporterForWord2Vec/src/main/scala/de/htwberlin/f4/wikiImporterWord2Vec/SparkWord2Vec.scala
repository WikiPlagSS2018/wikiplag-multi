package de.htwberlin.f4.wikiImporterWord2Vec

import de.htwberlin.f4.wikiImporterWord2Vec.commandLine.{Commands, OptionsForCommandLine}
import de.htwberlin.f4.wikiImporterWord2Vec.cassandra.ParametersForCasandra
import de.htwberlin.f4.wikiImporterWord2Vec.cassandra.tableEntity.IdTitleSentence
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import com.datastax.spark.connector._
import de.htwberlin.f4.wikiImporterWord2Vec.formatting.{Artikel, ArtikelParser, WikiParser}

object SparkWord2Vec {

  def main(args: Array[String]): Unit = {

    val result = OptionsForCommandLine.parse(args)
    val cassandraParameters = result._1
    val action = result._2
    val actionParameter = result._3

    if (action == Commands.ExtractWikiText) {
      extractWikitextAndStoreInCassandra(actionParameter, cassandraParameters)
    }




  }

  private def extractWikitextAndStoreInCassandra(hadoopFile: String, cassandraParameters: ParametersForCasandra): Unit = {


    var appName = "WikiImporter_Cassandra"
    val sparkConf = cassandraParameters.toSparkConf(appName)


    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)
    val df = sqlContext.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "page")
      .load(hadoopFile)

    df.filter("ns = 0")
      .select("id", "title", "revision.text")
      .rdd.map(X => (X.getLong(0).toInt, X.getString(1), WikiParser.parseXMLWikiPage(X.getStruct(2).getString(0))))
      .saveToCassandra(cassandraParameters.keyspace, cassandraParameters.idTableSentenceTable, SomeColumns(IdTitleSentence.id, IdTitleSentence.title, IdTitleSentence.sentence))
    println("Import Complete")
    sc.stop()


    /*
    var appName = "WikiImporter_Cassandra"
    val sparkConf = cassandraParameters.toSparkConf(appName)


    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)
    val df = sqlContext.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "page")
      .load(hadoopFile)

    df.filter("ns = 0")
      .select("id", "revision.id", "title", "revision.text")
      .rdd.map(x => new Artikel(x.get(0).toString(), x.get(1).toString(), x.get(2).toString(), x.getStruct(3).getString(0)))



    //val artikel = new Artikel()

    //ArtikelParser.compWithLibmagic(hadoopFile)

    */

  }


}
