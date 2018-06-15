package de.htwberlin.f4.wikiImporterWord2Vec.cassandra

import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf

/**
  * Encapsulates cassandra parameters read from the command line or a file for easier handling.
  */
class ParametersForCasandra(val idTableSentenceTable: String,
                            val keyspace: String,
                            val cassandraUser: String, val cassandraPW: String,
                            val cassandraHost: String, val cassandraPort: String = "9042") {


  /**
    * Creates a cassandra SparkConf from the parameters.
    * If the spark app is started within an IDE the spark master is also set to "local".
    **/
  def toSparkConf(appName: String, loadDefaults: Boolean = true): SparkConf = {
    val conf = new SparkConf(loadDefaults)
      .setAppName(appName)
      .set("spark.cassandra.connection.host", cassandraHost)
      .set("spark.cassandra.connection.port", cassandraPort)
      .set("spark.cassandra.auth.username", cassandraUser)
      .set("spark.cassandra.auth.password", cassandraPW)
    conf.setMaster("local")
    conf
  }

  override def toString: String = s"CassandraParameters($idTableSentenceTable, " +
    s"$keyspace," +
    s" $cassandraUser, $cassandraPW, $cassandraHost, $cassandraPort)"
}

object ParametersForCasandra {

  /** reads cassandra parameters from a file with the given path. Throws a ConfigException if something goes wrong.*/
  def readFromConfigFile(path: String): ParametersForCasandra = {

    var config = ConfigFactory.load(path)
    var user = config.getString("username")
    val password = config.getString("password")
    val host = config.getString("host")
    val port = config.getString("port")
    val keyspace = config.getString("keyspace")

    val idTableSentenceTable = config.getString("id_title_sentence")

    new ParametersForCasandra(idTableSentenceTable, keyspace, user, password, host, port)
  }

}
