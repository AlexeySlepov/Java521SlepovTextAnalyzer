import java.io.*;
import java.util.*;


// TODO:
//  Создать фаил на N количество слов.
//  Запустить потоки для
//  1) Посчитать количество слов
//  2) Посчитать количество предложений
//  3) Посчитать количество букв
//  4) Посчитать количество чисел
//  5) Посчитать количество глассных

public class TextAnalyzer {
    private static final String FILE_NAME = "sample_text.txt";
    private static String fileContent;
    private static final Object lock = new Object();

    private static int wordCount = 0;
    private static int sentenceCount = 0;
    private static int letterCount = 0;
    private static int digitCount = 0;
    private static int vowelCount = 0;

    public static void main(String[] args) {

        generateTextFile(500);

        fileContent = readFileContent();
        if (fileContent == null) {
            System.out.println("Ошибка чтения файла!");
            return;
        }


        Thread wordCounter = new WordCounterThread();
        Thread sentenceCounter = new SentenceCounterThread();
        Thread letterCounter = new LetterCounterThread();
        Thread digitCounter = new DigitCounterThread();
        Thread vowelCounter = new VowelCounterThread();

        wordCounter.start();
        sentenceCounter.start();
        letterCounter.start();
        digitCounter.start();
        vowelCounter.start();

        try {
            wordCounter.join();
            sentenceCounter.join();
            letterCounter.join();
            digitCounter.join();
            vowelCounter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nРезультаты анализа файла '" + FILE_NAME + "':");
        System.out.println("1. Количество слов: " + wordCount);
        System.out.println("2. Количество предложений: " + sentenceCount);
        System.out.println("3. Количество букв: " + letterCount);
        System.out.println("4. Количество цифр: " + digitCount);
        System.out.println("5. Количество гласных: " + vowelCount);
    }


    private static void generateTextFile(int wordCount) {
        try (PrintWriter writer = new PrintWriter(FILE_NAME)) {
            Random random = new Random();
            String[] words = {"Java", "поток", "2023", "анализ", "текст", "пример", "данные",
                    "файл", "Hello", "world", "многопоточность", "число", "123", "45"};
            String[] punctuation = {".", "!", "?"};

            for (int i = 0; i < wordCount; i++) {
                writer.print(words[random.nextInt(words.length)] + " ");

                if (i % 10 == 9) {
                    writer.print(punctuation[random.nextInt(punctuation.length)] + " ");
                }

                if (i % 50 == 49) {
                    writer.println();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String readFileContent() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class WordCounterThread extends Thread {
        @Override
        public void run() {
            String[] words = fileContent.split("\\s+");
            synchronized (lock) {
                wordCount = words.length;
            }
            System.out.println("Поток WordCounter завершил работу");
        }
    }

    static class SentenceCounterThread extends Thread {
        @Override
        public void run() {
            String[] sentences = fileContent.split("[.!?]+");
            synchronized (lock) {
                sentenceCount = sentences.length;
            }
            System.out.println("Поток SentenceCounter завершил работу");
        }
    }

    static class LetterCounterThread extends Thread {
        @Override
        public void run() {
            int count = 0;
            for (char c : fileContent.toCharArray()) {
                if (Character.isLetter(c)) {
                    count++;
                }
            }
            synchronized (lock) {
                letterCount = count;
            }
            System.out.println("Поток LetterCounter завершил работу");
        }
    }

    static class DigitCounterThread extends Thread {
        @Override
        public void run() {
            int count = 0;
            for (char c : fileContent.toCharArray()) {
                if (Character.isDigit(c)) {
                    count++;
                }
            }
            synchronized (lock) {
                digitCount = count;
            }
            System.out.println("Поток DigitCounter завершил работу");
        }
    }

    static class VowelCounterThread extends Thread {
        private final String VOWELS = "AEIOUАЕЁИОУЫЭЮЯaeiouаеёиоуыэюя";

        @Override
        public void run() {
            int count = 0;
            for (char c : fileContent.toCharArray()) {
                if (VOWELS.indexOf(c) != -1) {
                    count++;
                }
            }
            synchronized (lock) {
                vowelCount = count;
            }
            System.out.println("Поток VowelCounter завершил работу");
        }
    }
}