package run

import epic.preprocess.TreebankTokenizer
import epic.trees.Span
import org.apache.spark.{SparkConf, SparkContext}


// https://github.com/dlwh/epic/issues/17
object Run {

  val tokenizer = new TreebankTokenizer()
  val ner = epic.models.NerSelector.loadNer("en").get

  def main(args: Array[String]): Unit = {
    val inputFile = args(0)
    val outputFile = args(1)
    // Create a Scala Spark Context.
    val path = "/home/toa/Documents/spark-fun/jars/"
    val conf = new SparkConf().setAppName("wordCount").setJars(Seq(
      path + "epic_2.10-0.3.jar",
      path + "epic-ner-en-conll_2.10-2015.1.25.jar",
      path + "nak_2.10-1.3.jar",
      path + "scala-logging-api_2.10-2.1.2.jar",
      path + "scala-logging-slf4j_2.10-2.1.2.jar",
      path + "breeze_2.10-0.11-M0.jar",
      path + "spark-assembly-1.5.2-hadoop2.6.0.jar",
      path + "spark-fun-assembly-1.0.jar"
    ))
    val sc = new SparkContext(conf)

    sc.addFile("model.ser.gz")
    val input = sc.textFile(inputFile)

    val token = input.map { line =>
      val Array(_, sent) = line.split("\t")
      tokenizer(sent).toIndexedSeq
    }
    val ne = token.map(extractFromIndexedSentence)
    ne.saveAsTextFile(outputFile)
  }

  def extractFromIndexedSentence(sentence: IndexedSeq[String]): List[String] = {
    val segments = ner.bestSequence(sentence)
    segments.segmentsWithOutside.collect {
      case (Some(l), span: Span) => segments.words.slice(span.begin, span.end).mkString(" ")
    }.toList
  }
}
