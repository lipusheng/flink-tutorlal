package window

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, SlidingProcessingTimeWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import sink.SensorReading

/**
 * @author 4paradigm
 * @date 2020/9/13
 */
object TimeWindow {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val inputPath = System.getProperties.get("user.dir") + "/src/main/source.txt"
    val inputStream = env.readTextFile(inputPath)
    val dataStream = inputStream.map(data => {
      val arr = data.split(",")
      SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
    })
    dataStream.map(data => (data.id, data.temperature))
        .keyBy(_._1)
        //.window(TumblingEventTimeWindows.of(Time.seconds(15)))
        //.window(SlidingEventTimeWindows.of(Time.seconds(15),Time.seconds(5))
        .window(SlidingProcessingTimeWindows.of(Time.seconds(15),Time.seconds(5)))
      //.reduce()

    env.execute("time-window")
  }
}


