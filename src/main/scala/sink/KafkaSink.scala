package sink

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011
/**
 * @author 4paradigm
 * @date 2020/9/12
 */
object KafkaSink {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)
    env.setParallelism(1)
    val inputPath = System.getProperty("user.dir") + "/src/main/resources/source.txt"
    val inputStream = env.readTextFile(inputPath)
    val dataStream = inputStream.map(data => {
      val arr = data.split(",")
      SensorReading(arr(0), arr(1).toLong, arr(2).toDouble).toString
    })
    dataStream.print("kafka")
    dataStream.addSink(new FlinkKafkaProducer011[String]("127.0.0.1:32768,127.0.0.1:32769", "test-01", new SimpleStringSchema()))

    env.execute("kafka-sink")

  }
}

