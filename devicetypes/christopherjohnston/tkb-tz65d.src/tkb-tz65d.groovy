/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
/*
Left Switch on:

5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:35 BST: debug Parse Result: [[descriptionText:Living Room Light switch: BasicReport(value: 99), isStateChange:false, displayed:false, linkText:Living Room Light switch]]
5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:35 BST: debug Command: BasicReport(value: 99)
5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:35 BST: debug Parse Comand: BasicReport(value: 99)
5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:35 BST: debug Parse: zw device: 1C, command: 2003, payload: 63
5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:32 BST: debug Parse Result: [[name:switch, value:on, isStateChange:false, displayed:false, linkText:Living Room Light switch, descriptionText:Living Room Light switch switch is on]]
5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:32 BST: debug BasicSet: BasicSet(value: 255)
5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:32 BST: debug Parse Comand: BasicSet(value: 255)
5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:32 BST: debug Parse: zw device: 1C, command: 2001, payload: FF

Left Switch Off:

5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:53 BST: debug Parse Result: [[descriptionText:Living Room Light switch: BasicReport(value: 0), isStateChange:false, displayed:false, linkText:Living Room Light switch]]
5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:53 BST: debug Command: BasicReport(value: 0)
5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:53 BST: debug Parse Comand: BasicReport(value: 0)
5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:53 BST: debug Parse: zw device: 1C, command: 2003, payload: 00
5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:50 BST: debug Parse Result: [[name:switch, value:off, isStateChange:true, displayed:true, linkText:Living Room Light switch, descriptionText:Living Room Light switch switch is off]]
5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:50 BST: debug BasicSet: BasicSet(value: 0)
5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:50 BST: debug Parse Comand: BasicSet(value: 0)
5bee2c1e-ef37-4805-b9f2-e9b6d36adf22  18:21:50 BST: debug Parse: zw device: 1C, command: 2001, payload: 00

Right Switch On:

Right Switch Off:
*/
metadata {
	definition (name: "TKB TZ65D", namespace: "christopherjohnston", author: "ChristopherJohnston") {
		capability "Actuator"
		capability "Switch"
		capability "Switch Level"
		capability "Refresh"
        capability "Button"
		capability "Configuration"
		capability "Zw Multichannel"
        
        // 0 0 0x1101 0 0 0 6 0x26 0x85 0x70 0x27 0x86 0x72
        
        // 0x73: Power level - manual says this is supported.
        
        // From https://graph.api.smartthings.com/ide/doc/zwave-utils.html:
        // 0x26: Switch Multilevel
        // 0x85: Association
        // 0x70: Configuration
        // 0x27: Switch All
        // 0x86: Version
        // 0x72: Manufacturer Specific
		fingerprint deviceId: "0x1101", inClusters: "0x26,0x85,0x70,0x27,0x86,0x72,0x60,0x8E,0x2D", manufacturer: "TKB", model: "TZ65D"
		//fingerprint deviceId: "0x"
		//fingerprint deviceId: "0x3101"  // for z-wave certification, can remove these when sub-meters/window-coverings are supported
		//fingerprint deviceId: "0x3101", inClusters: "0x86,0x32"
		//fingerprint deviceId: "0x09", inClusters: "0x86,0x72,0x26"
		//fingerprint deviceId: "0x0805", inClusters: "0x47,0x86,0x72"
	}

	simulator {
		status "on":  "command: 2003, payload: FF"
		status "off": "command: 2003, payload: 00"
	}
    
   	tiles{
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
            }
            tileAttribute ("device.switchLevel", key: "SECONDARY_CONTROL") {
        		attributeState "switchLevel", label:'Level: ${currentValue}', icon: "st.Appliances.appliances17"
    		}
            tileAttribute ("device.switchLevel", key: "SLIDER_CONTROL") {
                attributeState "switchLevel", action:"switch level.setLevel"
            }
        }
        standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat") {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        
        main "switch"
		details (["switch", "switchOn", "switchOff", "levelSliderControl", "refresh"])
    }
}

def parse(String description) {
	log.debug("Parse: $description")
	def result = []
	if (description.startsWith("Err")) {
	    result = createEvent(descriptionText:description, isStateChange:true)
	} else {
		def cmd = zwave.parse(description, [0x26: 3, 0x20: 1, 0x25: 1, 0x60: 3, 0x8E: 2])
        if (cmd) {
        	log.debug("Parse Comand: $cmd")
			result += zwaveEvent(cmd)
		}
	}
    log.debug("Parse Result: $result")
	return result
}

def updated() {
	response(zwave.wakeUpV1.wakeUpNoMoreInformation())
}

//
// 0x72: Manufacturer Specific
//

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	log.debug("ManufacturerSpecificReport: ${cmd.inspect()}")
}

//
// 0x70: Configuration
//
def zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd) {
	log.debug("ConfigurationReport: ${cmd.inspect()}")
}

//
// 0x86: Version
//
def zwaveEvent(physicalgraph.zwave.commands.versionv1.VersionReport cmd)
{
	log.debug("VersionReport - applicationSubVersion: ${cmd.applicationSubVersion}")
    log.debug("VersionReport - applicationVersion: ${cmd.applicationVersion}")
    log.debug("VersionReport - zWaveLibraryType: ${cmd.zWaveLibraryType}")
    log.debug("VersionReport - zWaveProtocolSubVersion: ${cmd.zWaveProtocolSubVersion}")
    log.debug("VersionReport - zWaveProtocolVersion: ${cmd.zWaveProtocolVersion}")
}

//
// 0x26: SwitchMultiLevel
//
def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv2.SwitchMultilevelSet cmd) {
	log.debug("Received SwitchMultilevelSet: $cmd")
    return setSwitchLevel(cmd.value)
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelSupportedReport cmd) {
	log.debug("SwitchMultilevelSupportedReport - primarySwitchType: ${cmd.primarySwitchType}")
    log.debug("SwitchMultilevelSupportedReport - secondarySwitchType: ${cmd.secondarySwitchType}")
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelReport cmd) {
	log.debug("Received SwitchMultilevelReport: $cmd")
    return setSwitchLevel(cmd.value)
}

// Many sensors send BasicSet commands to associated devices.
// This is so you can associate them with a switch-type device
// and they can directly turn it on/off when the sensor is triggered.
def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd)
{
	log.debug("Received BasicSet: $cmd")
	return setSwitchLevel(cmd.value)
    //log.debug("NodeId: ${zwaveHubNodeId}")
    //log.debug("group: ${cmd.groupingIdentifier}")
    //log.debug("group: ${groupingIdentifier}")
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd)
{
	log.debug("Received BasicReport: $cmd")
	return setSwitchLevel(cmd.value)
}

private setSwitchLevel(value)
{
	def result
    log.debug("setSwitchLevel: Setting switchLevel to $value.")
	if (value == 0) {
		result = createEvent(name: "switch", value: "off")
	} else if (value == 255) {
		result = createEvent(name: "switch", value: "on")
	} else {
		result = [createEvent(name: "switch", value: "on"), createEvent(name: "switchLevel", value: value) ]
	}
    return result
}

//
// Multi-Channel
//

/*def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCapabilityReport cmd)
{
	log.debug("MultiChannelCapabilityReport: $cmd")
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
    log.debug "zwaveEvent-MultiChannelCmdEncap {$cmd.inspect()}"
    def encapsulatedCommand = cmd.encapsulatedCommand([0x25: 1, 0x20: 1])
    if (encapsulatedCommand) {
        if (state.enabledEndpoints.find { it == cmd.sourceEndPoint }) {
            def formatCmd = ([cmd.commandClass, cmd.command] + cmd.parameter).collect{ String.format("%02X", it) }.join()
            createEvent(name: "epEvent", value: "$cmd.sourceEndPoint:$formatCmd", isStateChange: true, displayed: false, descriptionText: "(fwd to ep $cmd.sourceEndPoint)")
        } else {
            zwaveEvent(encapsulatedCommand, cmd.sourceEndPoint as Integer)
        }
    }
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelassociationv2.MultiChannelAssociationGroupingsReport cmd)
{
	log.debug("MultiChannelAssociationGroupingsReport: $cmd")
}*/

//
// 0x85: Association
//

def zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationGroupingsReport cmd)
{
	log.debug("AssociationGroupingsReport: $cmd")
}

def zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationReport cmd)
{
	log.debug("AssociationReport: $cmd")
}


def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.debug("Unhandled Command: $cmd")
    // This will capture any commands not handled by other instances of zwaveEvent
    // and is recommended for development so you can see every command the device sends
    return createEvent(descriptionText: "${device.displayName}: ${cmd}")
}

//
// Commands Start
//

def on() {
	log.debug("Turning on")
	commands([zwave.switchMultilevelV3.switchMultilevelSet(value: 0xFF),
    		  zwave.switchMultilevelV3.switchMultilevelGet()], 2000)
}

def off() {
	log.debug("Turning off")
	commands([zwave.switchMultilevelV3.switchMultilevelSet(value: 0x00),
    		  zwave.switchMultilevelV3.switchMultilevelGet()], 2000)
}

def setLevel(value) {
	if (value == 100)
    {
    	value = 99
    }
	log.debug("Setting level to: $value")
	commands([zwave.switchMultilevelV3.switchMultilevelSet(value: value as Integer),
    		zwave.switchMultilevelV3.switchMultilevelGet()], 3000)
}

private command(physicalgraph.zwave.Command cmd) {
	if (state.sec) {
		zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
	} else {
		cmd.format()
	}
}

private commands(commands, delay=200) {
	delayBetween(commands.collect{ command(it) }, delay)
}

// Commands End

def refresh() {
	delayBetween([
    	//zwave.basicV1.basicGet().format(),
        //zwave.switchBinaryV1.switchBinaryGet().format(),
		zwave.manufacturerSpecificV1.manufacturerSpecificGet().format(),
        zwave.configurationV1.configurationGet().format(),
        zwave.associationV2.associationGroupingsGet().format(),
        //zwave.versionV1.versionGet().format(),
        //zwave.switchMultilevelV3.switchMultilevelSupportedGet().format(),
        //zwave.switchMultilevelV3.switchMultilevelGet().format(),
        zwave.associationV2.associationGet(groupingIdentifier: 1).format(),
        zwave.associationV2.associationGet(groupingIdentifier: 2).format(),
	], 1200)
}

def configure() {
	log.debug("Sending configuration")
    enableEpEvents("1,2")
    
    delayBetween([
    
    // Left paddle = group 1
    zwave.associationV1.associationSet(groupingIdentifier:1, nodeId:zwaveHubNodeId).format(),
    //zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier:1, nodeId:[zwaveHubNodeId]).format(),
    
    // Right paddle = group 2
    //zwave.associationV1.associationRemove(groupingIdentifier:2, nodeId:zwaveHubNodeId).format(),
    zwave.associationV1.associationSet(groupingIdentifier:2, nodeId:zwaveHubNodeId).format(),
    //zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier:2, nodeId:[zwaveHubNodeId]).format(),
    
    // Each configuration parameter can be set to its defualt setting by setting the default bit
    // in the configuration set command.
    
    // Ignore start level when transmitting dim commands
    // Switch can send dim commands to enabled dimmers, which has a start level embedded in it.
    // A dimmer receiving this command will start dimming from the start level.
    // The command also has a bit that indicates whether the dimmer should ignore the start level.
    // If set to 1, the dimmer will ignore the start level and instead start dimming from its current level.
    // if set to 0, the dimmer will ignore the start level
    //
    // Param #:1
    // Length: 1 Byte
    // Valid Values: 0 or 1
    // Default Value: 1
    zwave.configurationV1.configurationSet(parameterNumber: 1, scaledConfigurationValue: 1).format(),
    
    // Suspend Group 4
    // Disable transmitting commands to devices that are in group 4 without disassociating them from the group.
    //
    // Param #: 2
    // Length: 1 Byte
    // Valid Values: 0 or 1
    // Default Value: 0
    zwave.configurationV1.configurationSet(parameterNumber: 2, scaledConfigurationValue: 0).format(),
 
    // Night Light
    // The LED will by default turn on when the load attached is turned off.
    // To make the LED turn on when the load attached is turned on instead, set param 3 to a value of 1
    //
    // Param #: 3
    // Length: 1 byte
    // Valid Values: 0 or 1
    // Default value: 0
    zwave.configurationV1.configurationSet(parameterNumber: 3, scaledConfigurationValue: 0).format(),
    
    // Poll Group 2 interval (minutes)
    //
    // Param #: 20
    // Length: 1 byte
    // Valid Values: 0-255
    // Default value: 2
    zwave.configurationV1.configurationSet(parameterNumber: 20, scaledConfigurationValue: 2).format(),
    
    // Poll Group 2
    // If value is 0, switch will not poll group 2
    // If value is 1, switch will poll group 2 at the interval set in param 20
    //
    // Param #: 22
    // Length: 1 byte
    // Valid Values: 0 or 1
    // Default Value: 1
    zwave.configurationV1.configurationSet(parameterNumber: 22, scaledConfigurationValue: 1).format(),
    
    // Enable shade control group 2
    // The switch can operate shade control devices via its group 2 if this parameter is set to 1
    //
    // Param #: 14
    // Length: 1 byte
    // Valid Values: 0 or 1
    // Default Value: 0
    zwave.configurationV1.configurationSet(parameterNumber: 14, scaledConfigurationValue: 0).format(),
    
    // Enable shade control group 3
    // The switch can operate shade control devices via its group 3 if this parameter is set to 1
    //
    // Param #: 15
    // Length: 1 byte
    // Valid Values: 0 or 1
    // Default Value: 0
    zwave.configurationV1.configurationSet(parameterNumber: 15, scaledConfigurationValue: 0).format(),
    
    // LED transmission indication
    // The switch will flicker its LED when transmitting to any of its 4 groups.
    // 0: dont flicker at all
    // 1: flicker entire time of transmission
    // 2: flicker for 1 second at beginning of transmission (default)
    //
    // Param #: 19
    // Length: 1 byte
    // Valid Values: 0, 1, 2
    // Default Value: 2
    zwave.configurationV1.configurationSet(parameterNumber: 19, scaledConfigurationValue: 2).format(),
    ])
}

/*
def enableEpEvents(enabledEndpoints) {
    log.debug "enabledEndpoints: $enabledEndpoints"
    state.enabledEndpoints = enabledEndpoints.split(",").findAll()*.toInteger()
    null
}

private encap(cmd, endpoint) {
    log.debug "encap $endpoint {$cmd}"
    zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:endpoint, destinationEndPoint:endpoint).encapsulate(cmd)
}*/