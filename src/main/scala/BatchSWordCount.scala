import org.apache.flink.api.scala.{AggregateDataSet, DataSet, ExecutionEnvironment}
import org.apache.flink.api.scala._

/**
 * TODO
 *
 * @author 4paradigm
 * @date 2020/9/6
 */
object BatchSWordCount {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = ExecutionEnvironment.getExecutionEnvironment
    // 从文件中读取数据
    val inputPath = "/Users/4paradigm/tmp/flinkdir/file/batchwc"
    val inputDS: DataSet[String] = env.readTextFile(inputPath)
    val inputDS1 = env.fromCollection(List(1, 2, 3, 4));
    val wordCountDS: AggregateDataSet[(String, Int)]
    = inputDS.flatMap(_.split(" ")).map((_, 1)).groupBy(0).sum(1)
    // 打印输出
    wordCountDS.print()
  }
}
