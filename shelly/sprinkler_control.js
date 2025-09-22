let CONFIG = {
  
    /*
        Full URL to API of open-meteo to get primarily the precipitation data for the last 5 days
    */
    weatherEndpoint:
    "https://api.open-meteo.com/v1/forecast?latitude=50.4868021&longitude=13.439999&timezone=Europe%2FBerlin&daily=precipitation_sum,sunshine_duration,weather_code&current=temperature_2m,relative_humidity_2m,precipitation,weather_code,is_day,soil_moisture_1_to_3cm,soil_moisture_0_to_1cm&past_days=3&forecast_days=1",
  
    /*
        How often do we check the precipitation data?
        Default: Once an hour
    */
    timer_period_ms: 60000 * 60,
    
    /*
        Minimum 4 days running avarage of precipitation to disable sprinklers (including today)
        That is, this is the precipitation consumed by the lawn:
            - Anything below means we need to compensate with sprinklers
            - Anything above means that natural precipitation was enough
    */
    required_lawn_precipitation_avg: 0.6,
    
    /*
        Minimum amount of precipitation today to disable sprinklers
    */
    required_lawn_precipitation_today: 0.8
};

/*
    Global variable to hold the parsed weather data
*/
let currentWeatherData = null;

let configMqttTopic = "power/config/sprinkler";

function statusMessage(message) {
    logMessage(message, "info/sprinkler_control");
}

function debugMessage(message) {
    logMessage(message, "debug/sprinkler_control");
}

function traceMessage(message) {
    logMessage(message, "trace/sprinkler_control");
}

function logMessage(message, subtopic) {
    print(message);

    if (MQTT.isConnected()) {
        MQTT.publish("power/log/" + subtopic, message, 2, false);
    } else {
        print("MQTT NOT Connected");
    }
}

function publishWeatherData(precipitationTodayValue, precipitationAvgValue, temperatureCurrentValue, soilMoisture0to1cmValue, soilMoisture1to3cmValue, switchStatus) {
    
    let weatherData = {
        precipitation_today: precipitationTodayValue,
        precipitation_avg: precipitationAvgValue,
        temperature_now: temperatureCurrentValue,
        soil_moisture_0_to_1cm: soilMoisture0to1cmValue,
        soil_moisture_1_to_3cm: soilMoisture1to3cmValue,
        switch_on: switchStatus === "On"
    };

    if (MQTT.isConnected()) {
        MQTT.publish("power/sprinkler/weather/data", JSON.stringify(weatherData), 2, true);
    } else {
        print("MQTT NOT Connected");
    }
}

function actuateSwitch(activate) {
    Shelly.call(
        "Switch.Set",
        { id: 0, on: activate },
        function (response, error_code, error_message) {}
    );
}

function calculateAvgPrecipitation(dailyWeatherData) {
    let precipitationSum = 0.0;
    for (let daily_precipitation_sum of dailyWeatherData.precipitation_sum) {
        precipitationSum += daily_precipitation_sum;
    }
    return precipitationSum / dailyWeatherData.precipitation_sum.length;
}

/*
    Determines whether to enable sprinkler based on the given weather data
    The goal is to reduce the main water consumption based on the running avarage of natural precipitation
    The main sprinkler controller will still stick to the schedule but this switch will turn on/off
    the main sprinklier pump, ultimately giving the final green/red for sprinkling.
*/
function controlSprinkler(currentSwitchStatus, weatherData, doorStatus) {

    let precipitationToday = weatherData.daily.precipitation_sum[weatherData.daily.precipitation_sum.length - 1];
    let precipitationTodayStr = JSON.stringify(precipitationToday) + weatherData.current_units.precipitation;

    let precipitationAvg = calculateAvgPrecipitation(weatherData.daily);
    let precipitationAvgStr = precipitationAvg.toFixed(2) + weatherData.daily_units.precipitation_sum;

    let temperature = weatherData.current.temperature_2m;
    let temperatureStr = JSON.stringify(temperature) + weatherData.current_units.temperature_2m;
    
    let soilMoisture0to1cm = weatherData.current.soil_moisture_0_to_1cm;
    let soilMoisture0to1cmStr = JSON.stringify(soilMoisture0to1cm) + weatherData.current_units.soil_moisture_0_to_1cm;
    let soilMoisture1to3cm = weatherData.current.soil_moisture_1_to_3cm;
    let soilMoisture1to3cmStr = JSON.stringify(soilMoisture1to3cm) + weatherData.current_units.soil_moisture_1_to_3cm;

    let newSwitchStatus = currentSwitchStatus;
    
    if ((precipitationAvg >= CONFIG.required_lawn_precipitation_avg) || (precipitationToday >= CONFIG.required_lawn_precipitation_today)) {
        if (currentSwitchStatus === "On") {
            actuateSwitch(false);
            statusMessage("Avg. Precipitation: " + precipitationAvgStr + ", Cur. Temperature: " + temperatureStr + ", Today Precipitation: " + precipitationTodayStr + ", Soil Moisture 0-1cm: " + soilMoisture0to1cmStr + ", Soil Moisture 1-3cm: " + soilMoisture1to3cmStr + ". Disabling sprinkler!");
            newSwitchStatus = false;
        } else {
            statusMessage("Avg. Precipitation: " + precipitationAvgStr + ", Cur. Temperature: " + temperatureStr + ", Today Precipitation: " + precipitationTodayStr + ", Soil Moisture 0-1cm: " + soilMoisture0to1cmStr + ", Soil Moisture 1-3cm: " + soilMoisture1to3cmStr + ". Switch already Off. Not doing anything!");
        }
    } else {
        if (currentSwitchStatus === "Off") {
            actuateSwitch(true);
            statusMessage("Avg. Precipitation: " + precipitationAvgStr + ", Cur. Temperature: " + temperatureStr + ", Today Precipitation: " + precipitationTodayStr + ", Soil Moisture 0-1cm: " + soilMoisture0to1cmStr + ", Soil Moisture 1-3cm: " + soilMoisture1to3cmStr + ". Enabling sprinkler!");
            newSwitchStatus = true;
        } else {
            statusMessage("Avg. Precipitation: " + precipitationAvgStr + ", Cur. Temperature: " + temperatureStr + ", Today Precipitation: " + precipitationTodayStr + ", Soil Moisture 0-1cm: " + soilMoisture0to1cmStr + ", Soil Moisture 1-3cm: " + soilMoisture1to3cmStr + ". Switch already On. Not doing anything!");
        }
    }
    
    publishWeatherData(precipitationToday, precipitationAvg, temperature, soilMoisture0to1cm, soilMoisture1to3cm, newSwitchStatus);
}

/*
    The regular check of the weather data to determine whether there was enough natural precipitation for the lawn
*/
function timerCode() {
    
    // reset the weather data from the previous iteration
    currentWeatherData = null;
    
    // first, request the weather data
    Shelly.call(
        "HTTP.GET", {
            "url": CONFIG.weatherEndpoint
        },
        function (result, error_code, error_message) {
            if (error_code !== 0) {
                statusMessage("Cannot access weather data! Error Code: " + 
                             JSON.stringify(error_code) + ", Error Message: " + error_message);
                return;
            }
            if (result.code !== 200) {
                statusMessage("Weather data is unavailable! HTTP Status Code: " + 
                             JSON.stringify(result.code));
                return;
            }

            try {
                currentWeatherData = JSON.parse(result.body);
            } catch (e) {
                debugMessage("Failed to load the weather data! Error: " + JSON.stringify(e))
            }

            traceMessage("Raw Weather Data: " + result.body);
            
            // second, update the current switch status - for logging purposes only
            Shelly.call(
                "Switch.GetStatus", {
                    id: 0,
                },
                function(switch_result, switch_error_code, switch_error_message) {
                    if (switch_error_code !== 0) {
                        statusMessage("Cannot update switch status! Error Code: " + 
                                     JSON.stringify(switch_error_code) + ", Error Message: " + switch_error_message);
                        return;
                    }

                    let currentSwitchStatus = "Off";
                    if (switch_result.output === true) {
                        currentSwitchStatus = "On";
                    } else {
                        currentSwitchStatus = "Off";
                    }

                    controlSprinkler(currentSwitchStatus, currentWeatherData);
                }
            );
            
            let weatherData = JSON.parse(result.body);
            if (weatherData.current.temperature_2m <= CONFIG.tempBelowTurnOn) {
              activateSwitch(true);
            }
            if (weatherData.current.temperature_2m >= CONFIG.tempAboveTurnOff) {
              activateSwitch(false);
            }
            print(
              " Temperature - ",
              weatherData.current.temperature_2m,
              weatherData.current_units.temperature_2m
            );
        }
  );
};

debugMessage("Setting up a timer to check weather with period: " + JSON.stringify(CONFIG.timer_period_ms) + "ms");

let timerHandle = Timer.set(
    /* number of miliseconds */
    CONFIG.timer_period_ms,
    /* repeat? */
    true,
    /* callback */
    timerCode
);

debugMessage("Subsribing to MQTT topic: " + configMqttTopic);

MQTT.subscribe(configMqttTopic, 
    function(topic, message) {
        if (!message) {
            return;
        }

        let newConfig = null;
        try {
            newConfig = JSON.parse(message);
        } catch (e) {
            debugMessage("Failed to load the new configuration! Error: " + JSON.stringify(e))
        }

        if (newConfig !== null) {
            debugMessage("Received Sprinkler Configuration: " + JSON.stringify(newConfig));

            if (newConfig.timer_period_ms !== CONFIG.timer_period_ms) {
                debugMessage("Setting up a timer to check weather with period: " + JSON.stringify(newConfig.timer_period_ms) + "ms");
                Timer.clear(timerHandle);
                timerHandle = Timer.set(newConfig.timer_period_ms, true, timerCode);
            }

            CONFIG = newConfig;
        }
    }
);

// Run on startup
timerCode();
