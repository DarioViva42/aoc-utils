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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class AocUtils {

    static {
        loadSecurityProperties();
    }

    private static final Logger LOGGER = LogManager.getLogger(AocUtils.class);
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private static final String SERVER_PATH = "https://adventofcode.com/%d/day/%d/%s";
    public static final String FORM = "level=%s&answer=%s";

    private AocUtils() {
    }

    public static void sendPuzzleAnswer(int level, Object answer) {
        LOGGER.info("sending {}…", answer);
        AdventDate date = AdventDate.fromClass(STACK_WALKER.getCallerClass());
        String form = FORM.formatted(level, answer);
        createPostRequest(date, form)
                .ifPresent(AocUtils::sendPostRequest);
    }

    private static Optional<HttpURLConnection> createPostRequest(AdventDate date, String form) {
        try {
            URL url = new URL(SERVER_PATH.formatted(date.year(), date.day(), "answer"));
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

    public static String readPuzzleInput() {
        AdventDate date = AdventDate.fromClass(STACK_WALKER.getCallerClass());

        try (InputStream inputStream = getResource(date)) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String readPuzzleInput(int level) {
        AdventDate date = AdventDate.fromClass(STACK_WALKER.getCallerClass());

        try (InputStream inputStream = getResourceFromFile(date, level)) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static InputStream getResource(AdventDate date) {
        return getResourceFromFile(date)
                .or(() -> fetchResourceFromServer(date))
                .orElseThrow();
    }

    private static Optional<InputStream> getResourceFromFile(AdventDate date) {
        String dayPadded = StringUtils.leftPad(String.valueOf(date.day()), 2, '0');
        String path = "/day" + dayPadded;
        InputStream inputStream = AocUtils.class.getResourceAsStream(path);
        return inputStream != null ? Optional.of(inputStream) : Optional.empty();
    }

    private static InputStream getResourceFromFile(AdventDate date, int part) {
        String dayPadded = StringUtils.leftPad(String.valueOf(date.day()), 2, '0');
        String path = "/day" + dayPadded + (char) ('a' + part - 1);
        return AocUtils.class.getResourceAsStream(path);
    }

    private static Optional<InputStream> fetchResourceFromServer(AdventDate date) {
        try {
            URL url = new URL(SERVER_PATH.formatted(date.year(), date.day(), "input"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "session=" + System.getProperty("aoc.session"));
            return Optional.of(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private static void loadSecurityProperties() {
        InputStream is = AocUtils.class.getResourceAsStream("/security.properties");
        try {
            System.getProperties().load(is);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void logDurations(Instant start, Instant parseEnd, Instant betweenParts, Instant end) {
        LOGGER.info("parsing: {}ms", Duration.between(start, parseEnd).toMillis());
        LOGGER.info("part 1: {}ms", Duration.between(parseEnd, betweenParts).toMillis());
        LOGGER.info("part 2: {}ms", Duration.between(betweenParts, end).toMillis());
        LOGGER.info("total: {}ms", Duration.between(start, end).toMillis());
    }
}
