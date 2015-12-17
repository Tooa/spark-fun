name := "spark-fun"

version := "1.0"

scalaVersion := "2.10.6"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.5.2" % "provided",
  "org.scalanlp" %% "epic-ner-en-conll" % "2015.1.25",
  "org.scalanlp" %% "epic" % "0.3"
)
    

assemblyMergeStrategy in assembly := {
  case "META-INF/MANIFEST.MF" => MergeStrategy.rename
  case x => {
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    if (oldStrategy(x) == MergeStrategy.deduplicate) MergeStrategy.first else oldStrategy(x)
  }
}
