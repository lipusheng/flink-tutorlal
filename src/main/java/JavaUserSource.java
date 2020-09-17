import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.io.Serializable;
import java.util.Random;

/**
 * @author 4paradigm
 * @date 2020/9/7
 */
public class JavaUserSource {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<User> myDS = env.addSource(new UserSource());
        myDS.print();
        env.execute();
    }
}

class UserSource implements SourceFunction<User> {

    private boolean flag = true;

    @Override
    public void run(SourceContext<User> sourceContext) throws Exception {
        Random random = new Random();
        int number;
        int id = 0;
        while (flag) {
//            number = random.nextInt(1000);
//            String str = String.format("hello_world_%s", number);
//            sourceContext.collect(str);

            number = random.nextInt(100);
            User user = new User(id, "dog" + number, number);
            id ++;
            sourceContext.collect(user);
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        flag = false;
    }
}

class User implements Serializable {
    private Integer id;
    private String name;
    private Integer age;

    public User(Integer id, String name, Integer age){
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}