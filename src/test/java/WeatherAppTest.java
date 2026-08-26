package test.java;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherAppTest {

    @Test
    @DisplayName("convertWeatherCode should map ranges to readable conditions")
    void testConvertWeatherCodeMapping() throws Exception {
        Method m = WeatherApp.class.getDeclaredMethod("convertWeatherCode", long.class);
        m.setAccessible(true);
        assertEquals("Clear", m.invoke(null, 0L));
        assertEquals("Cloudy", m.invoke(null, 1L));
        assertEquals("Cloudy", m.invoke(null, 3L));
        assertEquals("Rain", m.invoke(null, 51L));
        assertEquals("Rain", m.invoke(null, 67L));
        assertEquals("Rain", m.invoke(null, 80L));
        assertEquals("Rain", m.invoke(null, 99L));
        assertEquals("Snow", m.invoke(null, 71L));
        assertEquals("Snow", m.invoke(null, 77L));
        assertEquals("", m.invoke(null, 100L));
    }

    @Test
    @DisplayName("findIndexOfCurrentTime returns index matching formatted current hour or 0 when none")
    void testFindIndexOfCurrentTime() throws Exception {
        Method getCurrentTime = WeatherApp.class.getDeclaredMethod("getCurrentTime");
        getCurrentTime.setAccessible(true);
        String now = (String) getCurrentTime.invoke(null);

        JSONArray times = new JSONArray();
        times.add("2000-01-01T00:00");
        times.add(now);
        times.add("2099-12-31T23:00");

        Method findIndex = WeatherApp.class.getDeclaredMethod("findIndexOfCurrentTime", org.json.simple.JSONArray.class);
        findIndex.setAccessible(true);
        int idx = (int) findIndex.invoke(null, times);
        assertEquals(1, idx);

        JSONArray timesNoMatch = new JSONArray();
        timesNoMatch.add("2000-01-01T00:00");
        int idxNo = (int) findIndex.invoke(null, timesNoMatch);
        assertEquals(0, idxNo);
    }

    @Test
    @DisplayName("getWeatherData deterministic assembly using internal helpers (simulated)")
    void testGetWeatherDataAssemblyWithSimulatedPayloads() throws Exception {
        // Prepare hourly arrays
        JSONArray time = new JSONArray();
        time.add("2000-01-01T00:00");
        time.add("2000-01-01T01:00");
        Method getNow = WeatherApp.class.getDeclaredMethod("getCurrentTime");
        getNow.setAccessible(true);
        String now = (String) getNow.invoke(null);
        time.add(now);

        JSONArray temps = new JSONArray();
        temps.add(1.0);
        temps.add(2.0);
        temps.add(12.5);

        JSONArray codes = new JSONArray();
        codes.add(3L);
        codes.add(71L);
        codes.add(0L);

        JSONArray humids = new JSONArray();
        humids.add(10L);
        humids.add(20L);
        humids.add(55L);

        JSONArray winds = new JSONArray();
        winds.add(3.0);
        winds.add(4.0);
        winds.add(7.2);

        Method findIndex = WeatherApp.class.getDeclaredMethod("findIndexOfCurrentTime", org.json.simple.JSONArray.class);
        findIndex.setAccessible(true);
        int index = (int) findIndex.invoke(null, time);
        assertEquals(2, index);

        double temperature = (double) temps.get(index);
        long code = (long) codes.get(index);
        long humidity = (long) humids.get(index);
        double wind = (double) winds.get(index);

        Method convert = WeatherApp.class.getDeclaredMethod("convertWeatherCode", long.class);
        convert.setAccessible(true);
        String condition = (String) convert.invoke(null, code);

        assertEquals(12.5, temperature);
        assertEquals("Clear", condition);
        assertEquals(55L, humidity);
        assertEquals(7.2, wind);
    }

    @Test
    @DisplayName("getCurrentTime format matches yyyy-MM-dd'T'HH':00'")
    void testCurrentTimeFormat() throws Exception {
        Method m = WeatherApp.class.getDeclaredMethod("getCurrentTime");
        m.setAccessible(true);
        String now = (String) m.invoke(null);
        assertEquals(16, now.length());
        assertEquals(':', now.charAt(13));
        assertEquals('0', now.charAt(14));
        assertEquals('0', now.charAt(15));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        assertDoesNotThrow(() -> LocalDateTime.parse(now, formatter));
    }

    @Test
    @DisplayName("convertWeatherCode returns empty string for unknown codes")
    void testUnknownWeatherCode() throws Exception {
        Method m = WeatherApp.class.getDeclaredMethod("convertWeatherCode", long.class);
        m.setAccessible(true);
        assertEquals("", m.invoke(null, -1L));
        assertEquals("", m.invoke(null, 1000L));
    }
}
