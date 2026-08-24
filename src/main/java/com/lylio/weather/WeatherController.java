package com.lylio.weather;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/weather")
    public String getWeather(
            @RequestParam(required = false) String city,
            Model model) {

        try {

            WeatherResponse weather =
                    weatherService.getWeather(city);

            model.addAttribute("weather", weather);
            model.addAttribute("city", city);

        } catch (WeatherApiException e) {

            model.addAttribute("error", e.getMessage());
            model.addAttribute("city", city);
        }

        return "index";
    }
}
