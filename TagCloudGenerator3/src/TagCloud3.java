import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

/**
 * This method takes an input file or pathway for a file and reads in that input
 * line by line and separates each word by using the separator set of characters
 * and adds the words to a queue and map that also includes the corresponding
 * word count. It then adds them to an html table alphabetically and outputs
 * them to an output file or pathway to the output file provided by the user.
 *
 * @author Kyle Ondecker, Durge Kumar
 */
public final class TagCloud3 {

    /**
     * Default constructor--private to prevent instantiation.
     */
    private TagCloud3() {

    }

    /**
     *
     * Compare {@code String}s in lexicographic order ignoring upper or lower
     * case.
     *
     */
    private static class StringLT
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> o1,
                Map.Entry<String, Integer> o2) {
            return o1.getKey().compareToIgnoreCase(o2.getKey());
        }
    }

    /**
     *
     * Compare {@code String,Integer}s in order largest to smallest.
     *
     *
     */
    private static class IntegerLT
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> o1,
                Map.Entry<String, Integer> o2) {
            return o2.getValue().compareTo(o1.getValue());
        }
    }

    /**
     * Outputs table header in html format.
     *
     * @param file
     *            given {@code String} file name to label table
     * @param out
     *            given {@code SimpleWriter} to output to file
     * @param n
     *            given {@code int} to represent number of words from file
     *
     * @ensures the table header is output.
     */
    private static void outputHeader(String file, PrintWriter out, int n) {

        out.println("<html><head><b>Top " + n + " words from " + file
                + "</b></head><body>");
        out.println(
                "<link href=\"http://web.cse.ohio-state.edu/software/2231/web-sw2/assignments/projects/tag-cloud-generator/data/tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println(
                "<link href=\"tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println("<hr>");
        out.println("<div class=\"cdiv\">\r\n" + "<p class=\"cbox\">\r\n");
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param charSet
     *            the {@code Set} to be replaced
     * @replaces charSet
     * @ensures charSet = entries(str)
     */
    private static void generateElements(String str, Set<Character> charSet) {

        int length = str.length();

        for (int i = 0; i < length; i++) {
            char s = str.charAt(i);
            charSet.add(s);
        }
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,

            Set<Character> separators) {

        boolean character = separators.contains(text.charAt(position));

        int i = position;

        while (i < text.length()
                && character == separators.contains(text.charAt(i))) {
            i++;

        }

        return text.substring(position, i);
    }

    /**
     * Reads through input file and adds words to given {@code Queue}.
     *
     * @param separator
     *            given {@code Set} containing the separator characters
     * @param q
     *            the given {@code Queue} for words to be added
     * @param input
     *            the given {@code BufferedReader} to read in file
     * @replaces q
     * @ensures q = words
     */
    private static void readFileAndCreateQueue(Set<Character> separator,
            Queue<String> q, BufferedReader input) {

        try {
            String s = input.readLine();
            while (s != null) {
                int i = 0;
                while (i < s.length()) {

                    String word = nextWordOrSeparator(s, i, separator);
                    i = i + word.length();

                    if (!separator.contains(word.charAt(0))) {
                        q.add(word);
                    }

                }
                s = input.readLine();

            }
        } catch (IOException e) {
            System.err.println("Error reading from file");
        }

    }

    /**
     * Creates map containing the words and count.
     *
     *
     * @param map
     *            given {@code Map} that will store the words and counts.
     * @param q
     *            given {@code Queue} that contains the words including repeats.
     *
     * @updates q
     * @replaces map
     * @ensures map contains each word 1 time and its corresponding count and q
     *          contains each word only 1 time.
     */
    private static void createMap(Map<String, Integer> map, Queue<String> q) {

        assert q.size() > 1 : "Violation of Queue being greater than 1";

        int length = q.size();
        int count = 1;

        while (length > 0) {
            String word = q.remove();
            if (map.containsKey(word)) {
                count = map.get(word);
                count = count + 1;
                map.replace(word, count);

            } else {
                count = 1;
                map.put(word, count);
                q.add(word);
            }
            length--;
        }

    }

    /**
     * Outputs the tag cloud to an html file using linear scaling and maximum
     * and minimum fonts.
     *
     * @param list
     *            given {@code List} that will contains the sorted keys and
     *            corresponding values.
     * @param out
     *            given {@code PrintWriter} that prints to the output file.
     * @param n
     *            given {@code int} that contains the amount of entries that
     *            will be output
     * @param max
     *            given {@code int} that represents the max amount of
     *            occurrences for a single word in the file
     * @param min
     *            given {@code int} that represents the min amount of occurences
     *            for a single word in the file
     * @clears s
     */
    private static void outputCloud(List<Map.Entry<String, Integer>> list,
            PrintWriter out, int n, int max, int min) {
        int i = 0;

        final int fontMin = 11;
        final int diff = 37;
        int font = 11;

        while (i < n) {
            Map.Entry<String, Integer> m = list.get(i);
            if (min != max) {
                font = diff * (m.getValue() - min) / (max - min) + fontMin;
            }
            out.print("<span style=\"cursor:default\" class=\"f" + font
                    + "\" title=\"count: \"" + m.getValue() + "\">" + m.getKey()
                    + "</span>");
            out.print("    ");
            i++;
        }

        out.println("</p></body></html>");

    }

    /**
     * The main file calls various methods provided above to read in a file and
     * output the top number of words defined by the user and their printed out
     * in alphabetical order and font size corresponds to the amount of
     * occurrences of each word.
     *
     * @param args
     *            the command line arguments; unused here
     *
     */

    public static void main(String[] args) {

        BufferedReader input;
        PrintWriter output;
        Scanner sc = new Scanner(System.in);

        System.out.println("Please enter input file name.");
        String inputFile = sc.nextLine();
        System.out.println("Please enter the output file name.");
        String outputFile = sc.nextLine();
        System.out.println(
                "Please enter the number of words(positive integer) you would like to inlcude in the cloud generator.");
        int num = sc.nextInt();
        sc.close();
        try {
            input = new BufferedReader(new FileReader(inputFile));
            output = new PrintWriter(
                    new BufferedWriter(new FileWriter(outputFile)));

        } catch (IOException e) {
            System.err.println("Error opening files.");
            return;
        }

        Queue<String> q = new LinkedList<>();
        Map<String, Integer> map = new HashMap<>();

        Comparator<Map.Entry<String, Integer>> stringOrder = new StringLT();
        Comparator<Map.Entry<String, Integer>> orderInts = new IntegerLT();

        Set<Character> separator = new HashSet<>();
        String separatorStr = " \t\n\r,-.!?[]';:/()";

        generateElements(separatorStr, separator);
        readFileAndCreateQueue(separator, q, input);
        createMap(map, q);

        List<Map.Entry<String, Integer>> listInts = new ArrayList<>();

        for (Map.Entry<String, Integer> element : map.entrySet()) {
            Map.Entry<String, Integer> m2 = new AbstractMap.SimpleImmutableEntry(
                    element.getKey(), element.getValue());
            listInts.add(m2);

        }

        List<Map.Entry<String, Integer>> listStrings;

        listInts.sort(orderInts);
        if (listInts.size() < num) {
            num = listInts.size();
        }
        listStrings = listInts.subList(0, num);
        listStrings.sort(stringOrder);

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (Map.Entry<String, Integer> elements : listInts) {
            min = Math.min(min, elements.getValue());
        }
        for (Map.Entry<String, Integer> elements : listInts) {
            max = Math.max(max, elements.getValue());
        }

        outputHeader(inputFile, output, num);

        outputCloud(listStrings, output, num, max, min);

        try {
            input.close();
            output.close();
        } catch (IOException e) {
            System.err.println("Error closing file");
        }
    }

}
