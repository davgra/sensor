(ns sensor.t-core
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [sensor.core :refer :all]
            [sensor.protocol :refer :all]))


(fact "Decode message"
      (decode-message "12;6;0;0;3;1.4\n")
      => {:node-id 12
          :child-sensor-id 6
          :message-type :presentation
          :ack false
          :subtype :S_LIGHT
          :payload "1.4"}

      (decode-message "12;6;1;0;0;36.5\n")
      => {:node-id 12
          :child-sensor-id 6
          :message-type :set
          :ack false
          :subtype :V_TEMP
          :payload "36.5"}

      (decode-message "13;7;1;0;2;1\n")
      => {:node-id 13
          :child-sensor-id 7
          :message-type :set
          :ack false
          :subtype :V_LIGHT
          :payload "1"}

      (decode-message "1;255;0;0;17;1.4.1\n")
      => {:node-id 1
          :child-sensor-id 255
          :message-type :presentation
          :ack false
          :subtype :S_ARDUINO_NODE
          :payload "1.4.1"}

      (decode-message "255;255;3;0;3;\n")
      => {:node-id 255
          :child-sensor-id 255
          :message-type :internal
          :ack false
          :subtype :I_ID_REQUEST
          :payload ""}

      (decode-message "0;0;3;0;9;read: 105-105-0 s=255,c=0,t=17,pt=0,l=5:1.4.1\n")
      => {:node-id 0
          :child-sensor-id 0
          :message-type :internal
          :ack false
          :subtype :I_LOG_MESSAGE
          :payload "read: 105-105-0 s=255,c=0,t=17,pt=0,l=5:1.4.1"}

      )

(fact "process :V_TEMP"
      (decode-message "105;1;1;0;0;23.0\n")
      => {:node-id 105
          :child-sensor-id 1
          :message-type :set
          :ack false
          :subtype :V_TEMP
          :payload "23.0"}
      (process-incomming-data (decode-message "105;1;1;0;0;23.0\n"))
      => {:log "105-1 :V_TEMP 23.0"
          :command :set
          :set {:sensor "105;1"
                :value {:V_TEMP "23.0"}}}
      )

(fact "process :V_HUM"
      (decode-message "105;0;1;0;1;35.0\n")
      => {:node-id 105
          :child-sensor-id 0
          :message-type :set
          :ack false
          :subtype :V_HUM
          :payload "35.0"}
      (process-incomming-data (decode-message "105;0;1;0;1;35.0\n"))
      => {:log "105-0 :V_HUM 35.0"
          :command :set
          :set {:sensor "105;0"
                :value {:V_HUM "35.0"}}}
      )

(fact "process :S_TEMP"
      (decode-message "105;1;0;0;6;1.4.1\n")
      => {:node-id 105
          :child-sensor-id 1
          :message-type :presentation
          :ack false
          :subtype :S_TEMP
          :payload "1.4.1"}
      (process-incomming-data (decode-message "105;1;0;0;6;1.4.1\n"))
      => {:log "105-1 :S_TEMP 1.4.1"}
      )

(fact "process :S_HUM"
      (decode-message "105;0;0;0;7;1.4.1\n")
      => {:node-id 105
          :child-sensor-id 0
          :message-type :presentation
          :ack false
          :subtype :S_HUM
          :payload "1.4.1"}
      (process-incomming-data (decode-message "105;0;0;0;7;1.4.1\n"))
      => {:log "105-0 :S_HUM 1.4.1"}
      )

(fact "process :I_SKETCH_NAME"
      (decode-message "105;255;3;0;11;Humidity\n")
      => {:node-id 105
          :child-sensor-id 255
          :message-type :internal
          :ack false
          :subtype :I_SKETCH_NAME
          :payload "Humidity"}
      (process-incomming-data (decode-message "105;255;3;0;11;Humidity\n"))
      => {:log "105 :I_SKETCH_NAME Humidity"}
      )

(fact "process :I_SKETCH_VERSION"
      (decode-message "105;255;3;0;12;1.0\n")
      => {:node-id 105
          :child-sensor-id 255
          :message-type :internal
          :ack false
          :subtype :I_SKETCH_VERSION
          :payload "1.0"}
      (process-incomming-data (decode-message "105;255;3;0;12;1.0\n"))
      => {:log "105 :I_SKETCH_VERSION 1.0"}
      )

(fact "process :I_CONFIG"
      (decode-message "105;255;3;0;6;0\n")
      => {
          :node-id 105
          :child-sensor-id 255
          :message-type :internal
          :ack false
          :subtype :I_CONFIG
          :payload "0"
          }
      (process-incomming-data (decode-message "105;255;3;0;6;0\n"))
      => {:log "105 :I_CONFIG 0"}
      )

(fact "process :S_ARDUINO_NODE"
      (decode-message "105;255;0;0;17;1.4.1\n")
      => {
          :node-id 105
          :child-sensor-id 255
          :ack false
          :message-type :presentation
          :subtype :S_ARDUINO_NODE
          :payload "1.4.1"
          }
      (process-incomming-data (decode-message "105;255;0;0;17;1.4.1\n"))
      => {:log "105 :S_ARDUINO_NODE 1.4.1"}
      )

(fact " process :I_LOG_MESSAGE"
      (decode-message "0;0;3;0;9;read: 105-105-0 s=255,c=0,t=17,pt=0,l=5:1.4.1\n")
      => {:node-id 0
          :child-sensor-id 0
          :message-type :internal
          :ack false
          :subtype :I_LOG_MESSAGE
          :payload "read: 105-105-0 s=255,c=0,t=17,pt=0,l=5:1.4.1"}

      (process-incomming-data (decode-message "0;0;3;0;9;read: 105-105-0 s=255,c=0,t=17,pt=0,l=5:1.4.1\n"))
      => {:log "read: 105-105-0 s=255,c=0,t=17,pt=0,l=5:1.4.1"})

(fact "process GATEWAY_READY"
      (decode-message "0;0;3;0;14;Gateway startup complete.\n")
      => {:node-id 0
          :child-sensor-id 0
          :message-type :internal
          :ack false
          :subtype :I_GATEWAY_READY
          :payload "Gateway startup complete."}

      (process-incomming-data (decode-message "0;0;3;0;14;Gateway startup complete.\n"))
      => {:log "Gateway startup complete."})

(fact "process ID_REQUEST"
      (decode-message "255;255;3;0;3;\n")
      => {:node-id 255
          :child-sensor-id 255
          :ack false
          :message-type :internal
          :subtype :I_ID_REQUEST
          :payload ""}

      (process-incomming-data (decode-message "255;255;3;0;3;\n"))
      => {:command :send
          :send "255;255;3;0;4;2\n"})


;(run-all-test






















































