import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * TODO
 *
 * @author 4paradigm
 * @date 2020/9/6
 */
public class BatchWordCount {
    public static void main(String[] args) throws Exception {

        ExecutionEnvironment environment = ExecutionEnvironment.getExecutionEnvironment();
        String inputPath = "/Users/4paradigm/tmp/flinkdir/file/batchwc";
        DataSource<String> inputSource = environment.readTextFile(inputPath);
        FlatMapOperator<String, Tuple2<String, Integer>> flatMap = inputSource.flatMap(new LineSplitter());
        AggregateOperator<Tuple2<String, Integer>> result = flatMap.groupBy(0).sum(1);
        result.print();
    }

    public static class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {

        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String world : value.split(" ")) {
                out.collect(new Tuple2<String, Integer>(world,1));
            }
        }
    }
}



