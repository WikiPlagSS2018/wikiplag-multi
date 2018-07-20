package de.htwberlin.f4.wikiplag.plagFinderWord2Vec

import java.io.PrintWriter

import de.htwberlin.f4.wikiplag.utils.CassandraParameters
import de.htwberlin.f4.wikiplag.utils.database.CassandraClient
import org.apache.spark.SparkContext
import org.junit.Assert._
import org.junit.{After, Before, Test}
import org.scalatest.junit.AssertionsForJUnit

/**
  *  @author Laura H.
  */
class InverseDocumentFrequencyBuilderTest extends AssertionsForJUnit {

  var builder: InverseDocumentFrequencyBuilder = _
  var sc: SparkContext = _

  @Before
def setUp() = {
    var cassandraParameters = CassandraParameters.readFromConfigFile("app.conf")
    val sparkConf = cassandraParameters.toSparkConf("[Wikiplag] InvDocFreqBuilder")
    sc = new SparkContext(sparkConf)
    val cassandraClient = new CassandraClient(sc, cassandraParameters)
    builder = new InverseDocumentFrequencyBuilder(cassandraClient)
  }

  @After
def tearDown() {
    sc.stop()
  }

  @Test
def testBuildInverseDocFreq(): Unit = {
    val invDocFreq = builder.buildInverseDocFrequency()

    writeMapToFile("./InverseDocumentFrequency.txt", invDocFreq)

    assertNotNull(invDocFreq)
  }

  @Test
def testBuildSortedInverseDocFreq(): Unit = {
    val sortedInvDocFreq = builder.buildSortedInvDocFreq()

    val firstElement = sortedInvDocFreq.values.take(1)

    writeMapToFile("./SortedInverseDocumentFrequency.txt", sortedInvDocFreq)

    assertNotNull(sortedInvDocFreq)
    assertSame(List(1), firstElement)
  }

  @Test
def testWithOneDocAndWriteMapToFile(): Unit = {
    val mapToSave = builder.testBuildInverseDocFrequency()

    def filename = "./InverseDocFreqTestWithOneDoc.txt"

    writeMapToFile(filename, mapToSave)
  }

  private def writeMapToFile(filename: String, toSave: Map[String, Int]): Unit = {
    new PrintWriter(filename) {
      toSave.foreach {
        case (k, v) =>
          write(k + ":" + v)
          write("\n")
      }
      close()
    }

  }

}
