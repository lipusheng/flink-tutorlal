import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;


/**
 * @author 4paradigm
 * @date 2020/9/6
 */
public class StreamWordCount {

    private static final String SPLIT_CHAR = " ";

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(8);
        DataStreamSource<String> dataStreamSource = env.socketTextStream("localhost", 9999);
        dataStreamSource.flatMap(new FlatMapFunction<String, Tuple2<String,Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                for(String word : value.split(SPLIT_CHAR)) {
                    out.collect(new Tuple2<String, Integer>(word,1));
                }
            }
        }).filter(new FilterFunction<Tuple2<String, Integer>>() {
            @Override
            public boolean filter(Tuple2<String, Integer> value) throws Exception {
                if (value.f0.isEmpty()) {
                    return false;
                }
                return true;
            }
        }).keyBy(0).print();

        env.execute("");
    }
}
