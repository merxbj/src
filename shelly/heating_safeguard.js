let CONFIG = {
  inputId: 2,
  switchId: 2
}

function statusMessage(message) {
    logMessage(message, "info/heating_safeguard");
}

function debugMessage(message) {
    logMessage(message, "debug/heating_safeguard");
}

function traceMessage(message) {
    //logMessage(message, "trace/heating_safeguard");
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
    Subscribe to status updates
    
    When heating input is turned off, check (after a short delay) that
    the heating output is also turned off.
    
    This is to prevent a situation when the pool controller is unavailable
    and cannot control this Shelly. In such case we would have no flow of
    pool water while the heat pump is still trying to heat it.
*/
Shelly.addStatusHandler(function(eventData) {
    if (eventData.component === "input:" + JSON.stringify(CONFIG.inputId)) {
        if (eventData.delta.state === false) {
            statusMessage("Heating input just changed to OFF! Will verify that heating output is OFF in 15 seconds.");
            setTimer(15000);
        }
    }
});

/*
    Checks that when heating input is OFF then the heating output is OFF as well.
    If not, switches the heating output OFF.
*/
function checkHeatingOutput() {
    
    // first, double-check that the heating input is, indeed, switched off
    Shelly.call(
        "Input.GetStatus", {
            id: CONFIG.inputId,
        },
        function(input_result, input_error_code, input_error_message) {
            if (input_error_code !== 0) {
                debugMessage("Cannot get input status! Will try again in 5s. Error Code: " + 
                             JSON.stringify(switch_error_code) + ", Error Message: " + switch_error_message);
                setTimer(5000);
                return;
            }
            
            if (input_result.state === true) {
                statusMessage("Heating input is ON again! No need to check the heating output.");
                return;
            }
            
            // second, check the heating output switch - it should be already OFF, if not, enforce it
            Shelly.call(
                "Switch.GetStatus", {
                    id: CONFIG.switchId,
                },
                function(switch_result, switch_error_code, switch_error_message) {
                    if (switch_error_code !== 0) {
                        statusMessage("Cannot get switch status! Will try again in 5s. Error Code: " + 
                                     JSON.stringify(switch_error_code) + ", Error Message: " + switch_error_message);
                        setTimer(5000);
                        return;
                    }

                    if (switch_result.output === false) {
                        statusMessage("Heating output is OFF. All is good. We are done.");
                    } else {
                        statusMessage("Heating output is still ON! Will switch if off now!");
                        
                        Shelly.call(
                            "Switch.Set", {
                                id: CONFIG.switchId,
                                on: false,
                            },
                            function(switch_set_result, switch_set_error_code, switch_set_error_message) {
                                if (switch_set_error_code !== 0) {
                                    statusMessage("Cannot set switch status! Will try again in 5s. Error Code: " + 
                                                 JSON.stringify(switch_set_error_code) + ", Error Message: " + switch_set_error_message);
                                    setTimer(5000);
                                    return;
                                }

                                statusMessage("Succesfully switched the heating OFF!");
                            }
                        );
                    }
                }
            );
        }
    );
};

function setTimer(delay) {
    Timer.set(
        /* number of miliseconds */
        delay,
        /* repeat? */
        false,
        /* callback */
        checkHeatingOutput
    );
}

/*
    Run the check immediatelly after a startup to make sure heating is in a correct state.
*/
checkHeatingOutput();