# Whether the Weather

### Description
This repository contains two weather-app entry points: a Spring web application and a Swing desktop application. The web workflow accepts a city request, resolves its location, fetches current conditions, and renders a result or error. The desktop workflow launches a search GUI, fetches weather for the entered location, and updates its display. The sampled web service uses Open-Meteo geocoding and forecast endpoints; the legacy desktop client does too. The README’s API description differs from the sampled implementation. Unsampled template and styling behavior is not inferred beyond the controller’s view name.

<br/>

|              |                                                                                                                                      |
|--------------|--------------------------------------------------------------------------------------------------------------------------------------|
| Demo Link    | [https://whether-the-weather-production.up.railway.app/](https://whether-the-weather-production.up.railway.app/)                     |
| Tech Stack   | Java 18                                                                                                                              | 
| Cloud Deploy | https://whether-the-weather-production.up.railway.app/                                                                               |
| Top Language | ![Github Language](https://img.shields.io/github/languages/top/lylio/whether-the-weather?style=for-the-badge)                        |
| Last Commit  | ![Github Commit Activity](https://img.shields.io/github/last-commit/lylio/whether-the-weather/main?style=for-the-badge)              |

### Launch & Structure

URL:
https://whether-the-weather-production.up.railway.app/

This app was built with assistance from ChatGPT and deployed onto Railway.com: both excellent in the assistance of building and deploying Java/React applications.

<br >

[![Architecture diagram of lylio/whether-the-weather](https://gitdiagram.com/lylio/whether-the-weather/diagram.png)](https://gitdiagram.com/lylio/whether-the-weather?utm_source=readme&utm_medium=picture)

<img width="3874" height="6435" alt="diagram" src="https://github.com/user-attachments/assets/7b92cc88-0621-4e54-93b9-8520658f7fb9" />


#### Acknowledgements
This app was built using the inspiration of TapTap's tutorials: https://www.youtube.com/watch?v=8ZcEYv2ezWc & https://github.com/curadProgrammer/WeatherAppGUI-Java
