/*
    Global variable to hold the parsed sensor data
*/
let currentSensorData = null;

/*
    Assume initial door status as closed to ensure we are dehumidifying
    unless we are explicitly told that the door is open.
    
    Note that we do not have a direct access to the door sensor - it's
    mostly sleeping, reporting only changes. It's also battery driven so
    running out of battery should not result in wet walls :-)
*/
let lastKnownDoorStatus = "close";

function debugMessage(message) {
    print(message);
    
    if (MQTT.isConnected()) {
        MQTT.publish("power/pool", message, 0, true);
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

    if ((humidity > 66.0) && (currentSwitchStatus === "Off")) {
        if (doorStatus === "close") {
            // We are over our threshold and the switch is off, we need to start dehumi.
            Shelly.call("Switch.set", {
                'id': 0,
                'on': true
            });
            debugMessage("Humidity: " + humidityStr + "%. Door: " + doorStatus + ". Turning ON dehumidifier!");
        } else {
            debugMessage("Humidity: " + humidityStr + "%. Door: " + doorStatus + ". Will NOT turn on dehumidifier!");
        }
    } else if ((humidity < 57.0) && (currentSwitchStatus === "On")) {
        // We are under our threshold and the switch is on, we need to stop dehumi.
        Shelly.call("Switch.set", {
            'id': 0,
            'on': false
        });
        debugMessage("Humidity: " + humidityStr + "%. Turning OFF dehumidifier!");
    } else if ((currentSwitchStatus === "On") && (doorStatus === "open")) {
        // Dehumidifier is running but the door is open now, we need to stop!
        Shelly.call("Switch.set", {
            'id': 0,
            'on': false
        });
        debugMessage("Humidity: " + humidityStr + "%. Door: " + doorStatus + ". Turning OFF dehumidifier!");
    }
    else {
        // Otherwise, nothing to do
        debugMessage("Humidity: " + humidityStr + "% and Switch is: " + 
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
    
    // first, request the humidity sensor values
    Shelly.call(
        "HTTP.GET", {
            "url": "http://192.168.88.76/state",
        },
        function(result, error_code, error_message) {
            if (error_code !== 0) {
                debugMessage("Cannot access humidity sensor! Error Code: " + 
                             JSON.stringify(error_code) + ", Error Message: " + error_message);
                return;
            }
            if (result.code !== 200) {
                debugMessage("Humidity sensor is unavailable! HTTP Status Code: " + 
                             JSON.stringify(result.code));
                return;
            }
            
            currentSensorData = JSON.parse(result.body);
            
            // second, update the current switch status to know what to do with the dehumidifer
            Shelly.call(
                "Switch.GetStatus", {
                    id: 0,
                },
                function(switch_result, switch_error_code, switch_error_message) {
                    if (switch_error_code !== 0) {
                        debugMessage("Cannot update switch status! Error Code: " + 
                                     JSON.stringify(switch_error_code) + ", Error Message: " + switch_error_message);
                        return;
                    }
                    
                    let currentSwitchStatus = "Off";
                    if (switch_result.output === true) {
                        currentSwitchStatus = "On";
                    } else {
                        currentSwitchStatus = "Off";
                    }
                    
                    controlDehumidifer(currentSwitchStatus, currentSensorData, lastKnownDoorStatus);
                }
            );
        }
    );
};

Timer.set(
    /* number of miliseconds */
    60000,
    /* repeat? */
    true,
    /* callback */
    timerCode
);

MQTT.subscribe("shellies/shellydw2-73C13B/sensor/state", 
    function(topic, message) {
        debugMessage("Received Door Sensor status update: " + lastKnownDoorStatus + 
                     " -> " + message);
        lastKnownDoorStatus = message;
    }
 );

// Run on startup
timerCode();