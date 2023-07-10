/*
    By default, let's consider the switch to be off
*/
let currentSwitchStatus = "Off";

function debugMessage(message) {
    print(message);
    
    if (MQTT.isConnected()) {
        MQTT.publish("power/pool", message, 0, true);
    } else {
        print("MQTT NOT Connected");
    }
}

/*
    Updates the switch status (global variable) based on the actual state
*/
function updateCurrentSwitchStatus() {
    Shelly.call(
        "Switch.GetStatus", {
            id: 0,
        },
        function(result, error_code, error_message) {
            if (error_code !== 0) {
                debugMessage("Cannot update switch status! Error Code: " + 
                             JSON.stringify(error_code) + ", Error Message: " + error_message);
                return;
            }
 
            if (result.output === true) {
                currentSwitchStatus = "On";
            } else {
                currentSwitchStatus = "Off";
            }
        }
    );
}

/*
    The regular check of the humidity level in the pool room
    
    The IP address of the humidity sensor is set as static in the router
    The goal is to keep the humidity between 60 and 69 percent
*/
function timerCode() {
    
    // update the current switch status to know what to do with the dehumidifer
    updateCurrentSwitchStatus();

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
            
            // get the humidity value first from the sensor
            let sensorData = JSON.parse(result.body);
            let humidity = sensorData.multiSensor.sensors[0].value / 100;
            let humidityStr = JSON.stringify(humidity);

            if ((humidity > 65.0) && (currentSwitchStatus === "Off")) {
                // We are over our threshold and the switch is off, we need to start dehumi.
                Shelly.call("Switch.set", {
                    'id': 0,
                    'on': true
                });
                debugMessage("Humidity: " + humidityStr + "%. Turning ON dehumidifier!");
            } else if ((humidity < 55.0) && (currentSwitchStatus === "On")) {
                // We are under our threshold and the switch is on, we need to stop dehumi.
                Shelly.call("Switch.set", {
                    'id': 0,
                    'on': false
                });
                debugMessage("Humidity: " + humidityStr + "%. Turning OFF dehumidifier!");
            } else {
                // Otherwise, nothing to do
                debugMessage("Humidity: " + humidityStr + "% and Switch is: " + 
                             currentSwitchStatus + ". Not doing anything!");
            }
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

// Run on startup
timerCode();