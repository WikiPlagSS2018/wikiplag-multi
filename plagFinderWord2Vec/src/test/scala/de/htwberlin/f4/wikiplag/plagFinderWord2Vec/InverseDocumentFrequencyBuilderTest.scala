package de.htwberlin.f4.wikiplag.plagFinderWord2Vec

import de.htwberlin.f4.wikiplag.utils.CassandraParameters
import de.htwberlin.f4.wikiplag.utils.database.CassandraClient
import org.apache.spark.SparkContext
import org.junit.Assert._
import org.junit._
import org.junit.{After, Before, Test}
import org.scalatest.junit.AssertionsForJUnit

class InverseDocumentFrequencyBuilderTest extends AssertionsForJUnit {

  var builder: InverseDocumentFrequencyBuilder = _
  var sc: SparkContext = _

  @Before
  def setUp() ={
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

  @Test def testBuildInverseDocFreq(): Unit = {
    val invDocFreq = builder.buildInverseDocFrequency()

    System.out.println("Inverse Document Frequency: " + invDocFreq)

    assertNotNull(invDocFreq)
  }

  @Test def testBuildSortedInverseDocFreq(): Unit = {
    val sortedInvDocFreq = builder.buildSortedInvDocFreq()

    val firstElement = sortedInvDocFreq.values.take(1)

    System.out.println("Sorted Inverse Document Frequency: " + sortedInvDocFreq)

    assertNotNull(sortedInvDocFreq)
    assertSame(List(1), firstElement)
  }
}
