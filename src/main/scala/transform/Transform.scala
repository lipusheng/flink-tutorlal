package transform

import org.apache.flink.api.common.functions.{IterationRuntimeContext, MapFunction, ReduceFunction, RichMapFunction, RuntimeContext}
import org.apache.flink.streaming.api.scala.{DataStream, SplitStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration

import scala.collection.TraversableOnce
/**
 * TODO
 *
 * @author 4paradigm
 * @date 2020/9/12
 */
object Transform {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // env.setParallelism(1)
    val inputPath = System.getProperty("user.dir") + "/src/main/resources/source.txt"
    val inputStream = env.readTextFile(inputPath)
    val dataStream = inputStream.map( data => {
      val arr = data.split(",")
        SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
    })
    val keyByDS = dataStream.keyBy("id");
    val addStream = keyByDS.minBy("temperature")
    //addStream.print()

    val resultStream = dataStream.keyBy(0)
      .reduce(new ReduceFunction[SensorReading] {
        override def reduce(value1: SensorReading, value2: SensorReading): SensorReading = {
          val id = value1.id
          SensorReading(id, value2.timestamp.max(value1.timestamp), value1.temperature.min(value2.temperature))

        }
      })
    //resultStream.print()

    val splitStream: SplitStream[SensorReading] = dataStream.split(x => {
      if (x.temperature > 30.0) Seq("high") else Seq("lower")
    })
    //splitStream.print()
    val highStream = splitStream.select("high")
    val lowerStream = splitStream.select("lower")
    val warnStream = highStream.map(x => (x.id, x.temperature))
    val connectStreams = warnStream.connect(lowerStream)
    val coMapStream = connectStreams
      .map(x => (x._1, x._2, "warning"), y => (y.id, "healthy"))
    coMapStream.print()

    // 4.3 union 合流操作  union 可union多条流
    val unionStream: DataStream[SensorReading] = highStream.union(lowerStream)

    env.execute("transform")
  }
}

case class SensorReading(id: String, timestamp: Long, temperature: Double);

