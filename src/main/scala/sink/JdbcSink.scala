package sink

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

/**
 * @author 4paradigm
 * @date 2020/9/13
 */
object JdbcSink {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val inputPath = System.getProperty("user.dir") + "/src/main/resources/source.txt"
    val inputStream = env.readTextFile(inputPath)
    val dataStream = inputStream.map(data => {
      val arr = data.split(",")
      SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
    })
    dataStream.addSink(new MyJdbcSinkFunc())
    env.execute("jdbc_sink")
  }

}

class MyJdbcSinkFunc() extends RichSinkFunction[SensorReading] {
  // 定义连接
  var conn: Connection  = _
  var insertStmt: PreparedStatement = _
  var updateStmt: PreparedStatement = _

  override def open(parameters: Configuration): Unit = {

    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "root")
    insertStmt = conn.prepareStatement("insert into sensor(id, temp) values(?, ?)")
    updateStmt = conn.prepareStatement("update sensor set temp = ? where id = ?")
  }

  override def invoke(value: SensorReading, context: SinkFunction.Context[_]): Unit = {
    // 先执行更新操作，查到就更新
    updateStmt.setString(2,value.id)
    updateStmt.setDouble(1, value.temperature)
    updateStmt.execute()
    // 更新无数据插入
    if (updateStmt.getUpdateCount == 0) {
      insertStmt.setString(1, value.id)
      insertStmt.setDouble(2, value.temperature)
      insertStmt.execute()
    }

  }

  override def close(): Unit = {
    insertStmt.close()
    updateStmt.close()
    conn.close()
  }
}

