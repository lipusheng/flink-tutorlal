package source


import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

import scala.util.Random

/**
 * @author 4paradigm
 * @date 2020/9/7
 */
object MySource {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val source: DataStream[SensorReading] = env.addSource(new MySensorSource())
    source.print();
    env.execute("mysource")
  }
}

class MySensorSource extends SourceFunction[SensorReading] {

  var flag : Boolean = true

  override def run(sourceContext: SourceFunction.SourceContext[SensorReading]): Unit = {
    val random = new Random()
    var curTemp = 1.to(10).map(
      i => ("sensor_" + i, 65 + random.nextGaussian() * 20)
    )

    while(flag) {
      curTemp = curTemp.map(
        t => (t._1,t._2 + random.nextGaussian())
      )

      val curTime = System.currentTimeMillis();

      curTemp.foreach(
        t => sourceContext.collect(SensorReading(t._1,curTime,t._2))
      )

      Thread.sleep(1000)
    }
  }

  override def cancel(): Unit = {
    flag = false
  }
}

case class SensorReading(id : String, time : Long,temp:Double)