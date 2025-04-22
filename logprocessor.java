import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class logprocessor {

    // Mapper class
    public static class TokenizerMapper
        extends Mapper<Object, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
            // Tokenize the input line and check for "ERROR"
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                String token = itr.nextToken();
                if (token.contains("ERROR")) { // Look for "ERROR" in the token
                    word.set("ERROR");
                    context.write(word, one); // Emit "ERROR" with count 1
                }
            }
        }
    }

    // Reducer class
    public static class IntSumReducer
        extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                            Context context)
            throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get(); // Sum the occurrences of "ERROR"
            }
            result.set(sum);
            context.write(key, result); // Write the result: key = "ERROR", value = sum
        }
    }

    // Main driver class
    public static void main(String[] args) throws Exception {
        // Create a new Hadoop configuration and job
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "log error count");
        job.setJarByClass(logprocessor.class); // Correct class name here

        // Set the Mapper and Reducer classes
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);

        // Set the output types for the job
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // Set input and output paths from command-line arguments
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // Run the job and exit with appropriate status code
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
