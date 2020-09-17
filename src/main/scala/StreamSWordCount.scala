import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

/**
 * TODO
 *
 * @author 4paradigm
 * @date 2020/9/6
 */
object StreamSWordCount {

  def main(args: Array[String]): Unit = {

    val tool: ParameterTool = ParameterTool.fromArgs(args)
    val host: String = tool.get("host")
    val port: Int = tool.getInt("port")

    // 创建环境
    val environment = StreamExecutionEnvironment.getExecutionEnvironment
    // 指定数据源
    val inputDataStream: DataStream[String] = environment.socketTextStream(host, port)
    // 导入隐式转换
    import org.apache.flink.api.scala._
    // 进行转换处理并输出
    inputDataStream.flatMap(_.split(" ")).filter(_.nonEmpty)
      .map((_,1)).keyBy(0).sum(1).print().setParallelism(1)

    environment.execute("streamwc")

  }
}
