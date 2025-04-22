import java.util.HashMap;
 import java.util.Map;
 import java.util.Scanner;
 public class WordCount {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Get input string
        System.out.println("Enter a sentence:");
        String input = scanner.nextLine();
        // Convert to lowercase and split into words
        String[] words = input.toLowerCase().split("\\W+"); // splits by non-word characters
        // Create a map to store word counts
        Map<String, Integer> wordCount = new HashMap<>();
        // Count each word
        for (String word : words) {
            if (!word.isEmpty()) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }
// Display the results
System.out.println("\nWord Count:");
for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}
scanner.close();
}
}