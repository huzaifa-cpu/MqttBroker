package com.client.enums;

public enum PacketType {
    CONNECT("connect"),
    CONNACK("connack"),
    PUBLISH("publish"),
    PUBACK("puback"),
    SUBSCRIBE("subscribe"),
    SUBACK("suback"),
    UNSUBSCRIBE("unsubscribe"),
    UNSUBACK("unsuback"),
    PUBREC("pubrec"),
    PUBREL("pubrel"),
    PUBCOMP("pubcomp"),
    PINGREQ("pingreq"),
    PINGRESP("pingresp"),
    DISCONNECT("disconnect"),
    AUTH("auth");

    private String value;

    PacketType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PacketType findByValue(String value){
        for (PacketType packetType: values()) {
            if(packetType.getValue().equals(value)){
                return packetType;
            }
        }
        return null;
    }
}
