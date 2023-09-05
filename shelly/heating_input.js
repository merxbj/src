let CONFIG = {
  inputId: 2,
  MQTTPublishTopic: "power/pool/heating",
};

function debugMessage(message) {
    print(message);
    
    if (MQTT.isConnected()) {
        MQTT.publish("power/pool/debug", message, 2, false);
    } else {
        
        print("MQTT NOT Connected");
    }
}

Shelly.addStatusHandler(function(eventData) {
    if (eventData.component === "input:" + JSON.stringify(CONFIG.inputId)) {
        debugMessage("Announcing " + JSON.stringify(eventData) + " " + CONFIG.MQTTPublishTopic);
        MQTT.publish(CONFIG.MQTTPublishTopic, JSON.stringify(eventData), 2, true);
    }
});
