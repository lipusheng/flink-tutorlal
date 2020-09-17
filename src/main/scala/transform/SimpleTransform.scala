/*
package transform

import org.apache.flink.streaming.api.scala.{DataStream, KeyedStream, StreamExecutionEnvironment}
import source.MySensorSource
import org.apache.flink.api.scala._
/**
 * @author 4paradigm
 * @date 2020/9/8
 */
object SimpleTransform {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 添加数据源
    val inputDS = env.addSource(new MySensorSource())
    // filter操作，按条件进行过滤，true输出
    val filterDS = inputDS.filter(x => x.temp >= 30)

    // map操作，对stream中的每个元素进行操作
    val mapDS = filterDS.map(x => (x.id + " " + x.time + " " + x.temp))

    // flatMap操作，对Stream中每个元素进行转换
    val flatMapDS = mapDS.flatMap(x => x.split(" "))

    // 启动任务执行
    env.execute()

  }
}
*/
