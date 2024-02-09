/*
    Configuration to be retrieved from the power/config MQTT topic and it's default
*/
let CONFIG = {

    /*
        How often do we check the humidity level?
    */
    timer_period_ms: 60000,
    
    /*
        The upper bound of allowed humidity in the pool room
    */
    max_humidity: 66.0,
    
    /*
        The lower bound of allowed humidity in the pool room
    */
    min_humidity: 57.0
};

/*
    Global variable to hold the parsed sensor data
*/
let currentSensorData = null;

/*
    Assume initial door status as open to ensure we are not dehumidifying
    unnecessarily. If there is an issue, the sensor itself will send a push
    notification when we go above certain threshold.
    
    Note that we do not have a direct access to the door sensor - it's
    mostly sleeping, reporting only changes. It's also battery driven so
    running out of battery should not result in wet walls :-)
*/
let lastKnownDoorStatus = "open";

let doorStatusMqttTopic = "shellies/shellydw2-D741F2/sensor/state";
let configMqttTopic = "power/config/dehumidifier";

function statusMessage(message) {
    logMessage(message, "info/humidity_control");
}

function debugMessage(message) {
    logMessage(message, "debug/humidity_control");
}

function traceMessage(message) {
    //logMessage(message, "trace/humidity_control");
}

function logMessage(message, subtopic) {
    print(message);

    if (MQTT.isConnected()) {
        MQTT.publish("power/log/" + subtopic, message, 2, true);
    } else {
        print("MQTT NOT Connected");
    }
}

/*
    Determines whether to start or stop the dehumidifier based on the given humidity
    and whether the dehumidifier is already running or not.
    The goal is to keep the humidity between 57 and 66 percent
*/
function controlDehumidifer(currentSwitchStatus, sensorData, doorStatus) {
    // get the humidity value first from the sensor
    let humidity = sensorData.multiSensor.sensors[0].value / 100;
    let humidityStr = JSON.stringify(humidity);

    if ((humidity > CONFIG.max_humidity) && (currentSwitchStatus === "Off")) {
        if (doorStatus === "close") {
            // We are over our threshold and the switch is off, we need to start dehumi.
            Shelly.call("Switch.set", {
                'id': 0,
                'on': true
            });
            statusMessage("Humidity: " + humidityStr + "%. Door: " + doorStatus + ". Turning ON dehumidifier!");
        } else {
            statusMessage("Humidity: " + humidityStr + "%. Door: " + doorStatus + ". Will NOT turn on dehumidifier!");
        }
    } else if ((humidity < CONFIG.min_humidity) && (currentSwitchStatus === "On")) {
        // We are under our threshold and the switch is on, we need to stop dehumi.
        Shelly.call("Switch.set", {
            'id': 0,
            'on': false
        });
        statusMessage("Humidity: " + humidityStr + "%. Turning OFF dehumidifier!");
    } else if ((currentSwitchStatus === "On") && (doorStatus === "open")) {
        // Dehumidifier is running but the door is open now, we need to stop!
        Shelly.call("Switch.set", {
            'id': 0,
            'on': false
        });
        statusMessage("Humidity: " + humidityStr + "%. Door: " + doorStatus + ". Turning OFF dehumidifier!");
    }
    else {
        // Otherwise, nothing to do
        statusMessage("Humidity: " + humidityStr + "% and Switch is: " + 
                     currentSwitchStatus + ". Not doing anything!");
    }
}

/*
    The regular check of the humidity level in the pool room
    
    The IP address of the humidity sensor is set as static in the router
*/
function timerCode() {
    
    // reset the sensor data from the previous iteration
    currentSensorData = null;

    traceMessage("Before getting the sensor state");

    // first, request the humidity sensor values
    Shelly.call(
        "HTTP.GET", {
            "url": "http://192.168.88.76/state",
        },
        function(result, error_code, error_message) {
            traceMessage("In sensor state callback - Begin");
            if (error_code !== 0) {
                statusMessage("Cannot access humidity sensor! Error Code: " + 
                             JSON.stringify(error_code) + ", Error Message: " + error_message);
                return;
            }
            if (result.code !== 200) {
                statusMessage("Humidity sensor is unavailable! HTTP Status Code: " + 
                             JSON.stringify(result.code));
                return;
            }
            traceMessage("In sensor state callback - After sanity checks");

            try {
                currentSensorData = JSON.parse(result.body);
            } catch (e) {
                debugMessage("Failed to load the sensor data! Error: " + JSON.stringify(e))
            }

            traceMessage("In sensor state callback - After pasring the body");

            // second, update the current switch status to know what to do with the dehumidifer
            Shelly.call(
                "Switch.GetStatus", {
                    id: 0,
                },
                function(switch_result, switch_error_code, switch_error_message) {
                    traceMessage("In switch status callback - Begin");
                    if (switch_error_code !== 0) {
                        statusMessage("Cannot update switch status! Error Code: " + 
                                     JSON.stringify(switch_error_code) + ", Error Message: " + switch_error_message);
                        return;
                    }

                    traceMessage("In switch status callback - After sanity checks");

                    let currentSwitchStatus = "Off";
                    if (switch_result.output === true) {
                        currentSwitchStatus = "On";
                    } else {
                        currentSwitchStatus = "Off";
                    }

                    traceMessage("In switch status callback - After determining the switch status");

                    controlDehumidifer(currentSwitchStatus, currentSensorData, lastKnownDoorStatus);
                }
            );
        }
    );
};

debugMessage("Setting up a timer to check humidity with period: " + JSON.stringify(CONFIG.timer_period_ms) + "ms");

let timerHandle = Timer.set(
    /* number of miliseconds */
    CONFIG.timer_period_ms,
    /* repeat? */
    true,
    /* callback */
    timerCode
);

debugMessage("Subsribing to MQTT topic: " + doorStatusMqttTopic);

MQTT.subscribe(doorStatusMqttTopic, 
    function(topic, message) {
        if (message)
        {
            debugMessage("Received Door Sensor status update: " + lastKnownDoorStatus + " -> " + message);
            lastKnownDoorStatus = message;
        }
    }
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
            debugMessage("Received Dehumidification Configuration: " + JSON.stringify(newConfig));

            if (newConfig.timer_period_ms !== CONFIG.timer_period_ms) {
                debugMessage("Setting up a timer to check humidity with period: " + JSON.stringify(newConfig.timer_period_ms) + "ms");
                Timer.clear(timerHandle);
                timerHandle = Timer.set(newConfig.timer_period_ms, true, timerCode);
            }

            CONFIG = newConfig;
        }
    }
);

// Run on startup
timerCode();