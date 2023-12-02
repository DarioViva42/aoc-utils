package tk.vivas.adventofcode;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * This Class contains useful methods to accompany you on your journey through advent of code.
 */
public class AocUtils {

    static {
        loadSecurityProperties();
    }

    private static final Logger LOGGER = LogManager.getLogger(AocUtils.class);

    /**
     * The {@code STACK_WALKER} is needed to find out from where the methods of this class are called from.
     * This is needed to find out in which package the calling class is located in.
     */
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    /**
     * The template for the url that is used to talk to the AoC server.
     */
    private static final String SERVER_PATH = "https://adventofcode.com/%d/day/%d/%s";

    /**
     * The template for the body of requests to the AoC server.
     */
    public static final String FORM = "level=%s&answer=%s";

    /**
     * Private constructor, no need to create instances of this class.
     */
    private AocUtils() {
    }

    /**
     * This method will send your answer.
     * For the level you will need to select either 1 for the first part and 2 for the more difficult second part.
     * <p>
     * When you want to send the String “helloWorld” for the first part, use the method like this:
     * {@snippet :
     *     sendPuzzleAnswer(1, "helloWorld");
     * }
     * @param level The level for which you try to send an answer.
     * @param answer The answer you have come up with and want to send to the AoC server.
     */
    public static void sendPuzzleAnswer(int level, Object answer) {
        LOGGER.info("sending {}…", answer);
        AdventDate date = AdventDate.fromClass(STACK_WALKER.getCallerClass());
        String form = FORM.formatted(level, answer);
        createPostRequest(date, form)
                .ifPresent(AocUtils::sendPostRequest);
    }

    /**
     * Creates a {@code POST} connection to send the puzzle answer to the server.
     * @param date The date that was automatically created from the package name.
     * @param form The body containing the part number and the answer.
     * @return The configured {@link HttpURLConnection} used to send the puzzle answer to the server.
     */
    private static Optional<HttpURLConnection> createPostRequest(AdventDate date, String form) {
        try {
            URL url = URI.create(SERVER_PATH.formatted(date.year(), date.day(), "answer")).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Cookie", "session=" + System.getProperty("aoc.session"));
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(form);
            out.close();
            return Optional.of(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * This method is used to send {@code POST} requests by using a {@link HttpURLConnection}
     * @param connection The connection used for the {@code POST} request.
     */
    private static void sendPostRequest(HttpURLConnection connection) {
        try (InputStream inputStream = connection.getInputStream()) {
            String serverAnswer = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(serverAnswer);
            Element feedbackElement = doc.getElementsByTag("p").get(0);
            String feedback = feedbackElement.text();
            LOGGER.info(feedback);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * This method will read the puzzle input. It will first search for a file
     * in you local ‘resource’ directory. If it does not find the file for the needed date
     * it will fetch the input from the AoC server instead.
     *
     * <p> You will need to create a file when testing, as these inputs
     * are not delivered by a distinct endpoint on the server, but only shown as part of the puzzle introduction.
     * As for the puzzle itself you don’t necessarily need to create a file for that.
     * Please note, that the ‘resources’ directory needs to be located directly inside the ‘test’ directory
     * when used in tests and directly inside the ‘main’ directory when used to solve the real puzzle.
     *
     * <p> The file needs to fallow a certain naming pattern.
     * The input data for day six of the year 2022 needs to be saved in the file “/resource/year2022/day06”.
     * Note that the file has no file ending.
     *
     * <p> To read the file “/year2022/day06” you will need to call from a class
     * that is in a package called ‘{@code (…).year2022.day06}’:
     * {@snippet :
     *      readPuzzleInput();
     * }
     * If there is no file called “day06” the input data is automatically tried to be read from the AoC server.
     *
     * <p> If you have different input data for both parts please consider using {@link #readPuzzleInput(int)}
     * @return The input for the puzzle.
     */
    public static String readPuzzleInput() {
        AdventDate date = AdventDate.fromClass(STACK_WALKER.getCallerClass());

        try (InputStream inputStream = getResource(date)) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * This method will read the puzzle input from a local file in the project’s ‘resources’ directory.
     *
     * <p> The file needs to fallow a certain naming pattern.
     * The part is denoted as an “a” for the first part and as a “b” for the second part.
     * The input data for part two, day seven of the year 2021 needs to be saved
     * in the file “/resources/year2022/day07b”. Note that the file has no file ending.
     *
     * <p> This method is mainly used when testing days, where there are two different inputs for both parts.
     * Please note, that the ‘resources’ directory needs to be located directly inside the ‘test’ directory
     * when used in tests and directly inside the ‘java’ directory when used to solve the real puzzle.
     *
     * <p> To read the file “/year2021/day07b” you will need to call from a class
     * that is in a package called ‘{@code (…).year2021.day07}’:
     * {@snippet :
     *      readPuzzleInput(2);
     * }
     * If you have the same input data for both parts please consider using {@link #readPuzzleInput()}
     * @param level The level that you want to read the input for.
     * @return The input for the puzzle.
     */
    public static String readPuzzleInput(int level) {
        AdventDate date = AdventDate.fromClass(STACK_WALKER.getCallerClass());

        try (InputStream inputStream = getResourceFromFile(date, level)) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    /**
     * This method reads the input data from a file when the corresponding file exists.
     * It will fall back to fetch the data from the AoC server.
     * @param date The date that was automatically created from the package name.
     * @return The {@code InputStream} that was read from the file / server.
     */
    private static InputStream getResource(AdventDate date) {
        return getResourceFromFile(date)
                .or(() -> fetchResourceFromServer(date))
                .orElseThrow();
    }

    /**
     * This method reads the input data from a file when there is one input for both parts.
     * @param date The date that was automatically created from the package name.
     * @return The {@code InputStream} that was read from the file.
     */
    private static Optional<InputStream> getResourceFromFile(AdventDate date) {
        String dayPadded = StringUtils.leftPad(String.valueOf(date.day()), 2, '0');
        String path = "/year%s/day%s".formatted(date.year(), dayPadded);
        InputStream inputStream = AocUtils.class.getResourceAsStream(path);
        return inputStream != null ? Optional.of(inputStream) : Optional.empty();
    }

    /**
     * This method reads the input data from a file when there are different inputs for both parts.
     * @param date The date that was automatically created from the package name.
     * @param part The part that needs to be read.
     * @return The {@code InputStream} that was read from the file.
     */
    private static InputStream getResourceFromFile(AdventDate date, int part) {
        String dayPadded = StringUtils.leftPad(String.valueOf(date.day()), 2, '0');
        String path = "/year%s/day%s%s".formatted(date.year(), dayPadded, (char) ('a' + part - 1));
        return AocUtils.class.getResourceAsStream(path);
    }


    /**
     * This method will fetch the puzzle input from the AoC server
     * using the date that was automatically created from the package name.
     * @param date The date that was automatically created from the package name.
     * @return An {@code InputStream} of the data that was read from the server.
     */
    private static Optional<InputStream> fetchResourceFromServer(AdventDate date) {
        try {
            URL url = URI.create(SERVER_PATH.formatted(date.year(), date.day(), "input")).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "session=" + System.getProperty("aoc.session"));
            return Optional.of(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * This will load the {@code security.properties} file.
     * It is expected that the property “{@code aoc.session}” is defined.
     */
    private static void loadSecurityProperties() {
        InputStream is = AocUtils.class.getResourceAsStream("/security.properties");
        try {
            System.getProperties().load(is);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    /**
     * This method will be useful when you want to display the times it takes for your algorithm to run.
     * It will print out how long the three parts of your puzzle solving took you
     * and in addition how long all three parts combined took. Times are shown in milliseconds.
     *
     * <p> Please note that reading the input from the file or the server by calling the {@link #readPuzzleInput()}
     * method is allowed to happen before the start instant.
     *
     * <p> Everything that is involved in the procedure where you parse the raw input string
     * and transform it into a data structure that is easier to work with is considered to be part of the parsing step.
     *
     * <p> When you have calculated the answer to both parts you are allowed to regard this the end instant.
     * Sending both answers to the AoC server should happen after the end instant.
     *
     * <p> Fallowing snippet demonstrates the usage of this method:
     * {@snippet :
     *    // read puzzle input
     *    Instant start = Instant.now();
     *    // parse input data
     *    Instant parseEnd = Instant.now();
     *    // solve part one
     *    Instant betweenParts = Instant.now();
     *    // solve part two
     *    Instant end = Instant.now();
     *    // send puzzle answers for both parts
     *    logDurations(start, parseEnd, betweenParts, end);
     * }
     * @param start This is the instant before you start to parse the input data.
     * @param parseEnd This is the instant when you have finished parsing the data.
     * @param betweenParts This is the instant when you have finished solving the first part.
     * @param end This is the instant when you have finished both parts.
     */
    public static void logDurations(Instant start, Instant parseEnd, Instant betweenParts, Instant end) {
        LOGGER.info("parsing: {}ms", Duration.between(start, parseEnd).toMillis());
        LOGGER.info("part 1: {}ms", Duration.between(parseEnd, betweenParts).toMillis());
        LOGGER.info("part 2: {}ms", Duration.between(betweenParts, end).toMillis());
        LOGGER.info("total: {}ms", Duration.between(start, end).toMillis());
    }
}
