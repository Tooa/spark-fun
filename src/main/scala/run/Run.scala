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
    val conf = new SparkConf().setAppName("wordCount")
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
