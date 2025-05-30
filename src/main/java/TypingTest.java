import java.io.File;
import java.io.InputStream;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class TypingTest {

    private static volatile String lastInput = "";
    private static volatile boolean inputReceived = false;
    private static Scanner scanner = new Scanner(System.in);

    public static class InputRunnable implements Runnable {
        @Override
        public void run() {
            Scanner localScanner = new Scanner(System.in);
            if (localScanner.hasNextLine()) {
                String input = localScanner.nextLine();
                lastInput = input.trim();
                inputReceived = true;
            }
        }
    }

    public static void testWord(String wordToTest) {
        try {
            System.out.println(wordToTest);
            lastInput = "";
            inputReceived = false;

            Thread inputThread = new Thread(new InputRunnable());
            inputThread.start();

            // Timeout based on word length (e.g., 1000ms per character, min 4000ms)
            int timeout = Math.max(4000, wordToTest.length() * 1000);
            inputThread.join(timeout);

            System.out.println();
            System.out.println("You typed: " + lastInput);

            if (lastInput.equals(wordToTest)) {
                System.out.println("Correct");
            } else {
                System.out.println("Incorrect");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void typingTest(List<String> inputList) throws InterruptedException {
        int correct = 0;
        int incorrect = 0;
        long totalTime = 0;

        for (int i = 0; i < inputList.size(); i++) {
            String wordToTest = inputList.get(i);

            long start = System.currentTimeMillis();
            testWord(wordToTest);
            long end = System.currentTimeMillis();

            totalTime += (end - start);
            if (lastInput.equals(wordToTest)) {
                correct++;
            } else {
                incorrect++;
            }

            Thread.sleep(500); // Short pause before next word
        }

        System.out.println("\n Test Summary :");
        System.out.println("Total words: " + inputList.size());
        System.out.println("Correct: " + correct);
        System.out.println("Incorrect: " + incorrect);
        System.out.println("Total time: " + totalTime / 1000.0 + " seconds");
        System.out.println("Average time per word: " + (totalTime / inputList.size()) / 1000.0 + " seconds");
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> words = new ArrayList<>();

        try {
            InputStream inputStream = TypingTest.class.getClassLoader().getResourceAsStream("Words.txt");
            if (inputStream == null) {
                System.out.println("Words.txt not found in resources.");
                return;
            }

            Scanner fileScanner = new Scanner(inputStream);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    words.add(line);
                }
            }
            fileScanner.close();
        } catch (Exception e) {
            System.out.println("Error reading file.");
            e.printStackTrace();
            return;
        }

        // Shuffle and pick a few random words (e.g., 10)
        List<String> testWords = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10 && !words.isEmpty(); i++) {
            int index = random.nextInt(words.size());
            testWords.add(words.get(index));
        }

        typingTest(testWords);

        System.out.println("Press enter to exit.");
        scanner.nextLine();
    }
}
