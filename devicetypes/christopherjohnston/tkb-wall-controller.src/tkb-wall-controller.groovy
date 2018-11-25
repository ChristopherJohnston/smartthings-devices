metadata {
	// Automatically generated. Make future change here.
	definition (name: "TKB Wall Controller", namespace: "christopherjohnston", author: "ChristopherJohnston") {
		capability "Actuator"
		capability "Button"
		capability "Configuration"
		capability "Sensor"

		// 0 0 0x0101 0 0 0 9 0x72 0xEF 0x20 0x26 0x27 0x85 0x75 0x82 0x71
        //, outClusters: "0x26,0x2B"
        
		fingerprint deviceId: "0x0101", inClusters: "0x72,0xEF,0x20,0x26,0x27,0x85,0x75,0x82,0x71"
	}

	simulator {
		status "button 1 pushed":  "command: 2001, payload: 01"
		status "button 1 held":  "command: 2001, payload: 15"
		status "button 2 pushed":  "command: 2001, payload: 29"
		status "button 2 held":  "command: 2001, payload: 3D"
		status "button 3 pushed":  "command: 2001, payload: 51"
		status "button 3 held":  "command: 2001, payload: 65"
		status "button 4 pushed":  "command: 2001, payload: 79"
		status "button 4 held":  "command: 2001, payload: 8D"
		status "wakeup":  "command: 8407, payload: "
	}
	tiles {
		standardTile("button", "device.button", width: 2, height: 2) {
			state "default", label: "", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
		}
        standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat") {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}
		main "button"
		details(["button","configure"])
	}
}

def parse(String description) {
		// 1, single press: zw device: 15, command: 2B01, payload: 0B FF -> SceneActivationSet(dimmingDuration: 255, sceneId: 11)
        // 2, single press: zw device: 15, command: 2B01, payload: 15 FF -> SceneActivationSet(dimmingDuration: 255, sceneId: 21)
        // 3, single press: zw device: 15, command: 2B01, payload: 0C FF -> SceneActivationSet(dimmingDuration: 255, sceneId: 12)
        // 4, single press: zw device: 15, command: 2B01, payload: 16 FF -> SceneActivationSet(dimmingDuration: 255, sceneId: 22)
        
        
	def results = []
	if (description.startsWith("Err")) {
    	log.debug("err parse")
	    results = createEvent(descriptionText:description, displayed:true)
	} else {
		def cmd = zwave.parse(description)
		if(cmd) results += zwaveEvent(cmd)
		if(!results) results = [ descriptionText: cmd, displayed: false ]
	}
	log.debug("Parsed '$description' to $results")
	return results
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd) {
	def results = [createEvent(descriptionText: "$device.displayName woke up", isStateChange: false)]
	
    results += configurationCmds().collect{ response(it) }
	results << response(zwave.wakeUpV1.wakeUpNoMoreInformation().format())
	log.debug("zWaveEvent Called: $results")
	return results
}

def buttonEvent(button, held) {
	button = button as Integer
	if (held) {
		createEvent(name: "button", value: "held", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was held", isStateChange: true)
	} else {
		createEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
	}
}

def zwaveEvent(physicalgraph.zwave.commands.sceneactivationv1.SceneActivationSet cmd) {
	log.debug("Parsed $cmd")
    	/*
        
        Buttons:
        
        1		3
        2		4
        
        Values
        
        11 = scene 1, on				-> Button 1 push
        12 = scene 1, off				-> Button 2 push
        13 = scene 1, dim up start		-> Button 1 held start	
        14 = scene 1, dim down start	-> Button 2 held start
        15 = scene 1, dim up stop		-> Button 1 held stop
        16 = scene 1, dim down stop		-> Button 2 held stop
        21 = scene 2, on				-> Button 3 push
        22 = scene 2, off				-> Button 4 push
        23 = scene 2, dim up start
        24 = scene 2, dim down start
        25 = scene 2, dim up stop
        26 = scene 2, dim down stop*/
        
	/*Integer button = ((cmd.sceneId + 1) / 2) as Integer
	Boolean held = !(cmd.sceneId % 2)*/
    Integer button =0
    Boolean held = False
    
    switch (cmd.sceneId) {
    	case 11:
        	button = 1
            held = False
            break
        case 12:
        	button = 2
            held = False
            break
        case 21:
        	button = 3
            held = False
            break
        case 22:
        	button = 4
            held = False
            break
       	default:
        	button = 0
            held = False
            break
    }
	buttonEvent(button, held)
}

/*def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
	Integer button = (cmd.value / 40 + 1) as Integer
	Boolean held = (button * 40 - cmd.value) <= 20
	buttonEvent(button, held)
}*/

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	[ descriptionText: "$device.displayName: $cmd", linkText:device.displayName, displayed: false ]
}

/*def configurationCmds() {
	def cmds = []
	def hubId = zwaveHubNodeId
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 1, configurationValue: 1).format()
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 11, configurationValue: 5).format()
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 12, configurationValue: 5).format()
    cmds << zwave.configurationV1.configurationSet(parameterNumber: 20, configurationValue: 50).format()
    cmds << zwave.configurationV1.configurationSet(parameterNumber: 21, configurationValue: 1).format()
    cmds << zwave.configurationV1.configurationSet(parameterNumber: 22, configurationValue: 0).format()
    cmds << zwave.configurationV1.configurationSet(parameterNumber: 24, configurationValue: 2).format()
    cmds << zwave.configurationV1.configurationSet(parameterNumber: 30, configurationValue: 2).format()
	cmds
}*/

def configure() {
	log.debug("Sending configuration.")
	delayBetween([
        //zwave.configurationV1.configurationSet(parameterNumber: 1, scaledConfigurationValue: 1).format(),
        zwave.configurationV1.configurationSet(parameterNumber: 11, scaledConfigurationValue: 4).format(),
        zwave.configurationV1.configurationSet(parameterNumber: 12, scaledConfigurationValue: 4).format(),
        /*zwave.configurationV1.configurationSet(parameterNumber: 20, scaledConfigurationValue: 50).format(),
        zwave.configurationV1.configurationSet(parameterNumber: 21, scaledConfigurationValue: 1).format(),
        zwave.configurationV1.configurationSet(parameterNumber: 22, scaledConfigurationValue: 0).format(),
        zwave.configurationV1.configurationSet(parameterNumber: 24, scaledConfigurationValue: 2).format(),
        zwave.configurationV1.configurationSet(parameterNumber: 30, scaledConfigurationValue: 2).format(),*/
        zwave.associationV1.associationSet(groupingIdentifier:1, nodeId:zwaveHubNodeId).format(),
        zwave.associationV1.associationSet(groupingIdentifier:2, nodeId:zwaveHubNodeId).format(),
        // Make sure sleepy battery-powered sensors send their WakeUpNotifications to the hub every 4 hours:
        //zwave.wakeUpV1.wakeUpIntervalSet(seconds:4 * 3600, nodeid:zwaveHubNodeId).format(),
	])
}

/*def configure() {
	def cmds = configurationCmds()
	log.debug("Sending configuration: $cmds")
	return cmds
}*/