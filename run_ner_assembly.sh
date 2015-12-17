# Clean working directory
rm -R ./wordcounts

/home/toa/spark-1.5.2-bin-hadoop2.6/bin/spark-submit \
 --class run.Run \
 ./target/scala-2.10/spark-fun-assembly-1.0.jar \
 ./sentences_100.tsv ./wordcounts
