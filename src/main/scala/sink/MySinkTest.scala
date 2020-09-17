package sink

import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink

/**
 * TODO
 *
 * @author 4paradigm
 * @date 2020/9/12
 */
object MySinkTest {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // env.setParallelism(1)
    val inputPath = System.getProperty("user.dir") + "/src/main/resources/source.txt"
    val outputPath = System.getProperty("user.dir") + "/src/main/resources";
    val inputStream = env.readTextFile(inputPath)
    val dataStream = inputStream.map( data => {
      val arr = data.split(",")
      SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
    })
    // dataStream.writeAsText(outputPath)

    val sinkStream: DataStreamSink[SensorReading] = dataStream.addSink(
      StreamingFileSink.forRowFormat(
        new Path(outputPath), new SimpleStringEncoder[SensorReading]()
      ).build())

    env.execute("mysink")
  }
}

case class SensorReading(id: String, timestamp: Long, temperature: Double);

