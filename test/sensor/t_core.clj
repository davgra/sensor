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
          :payload "1.4.1"})

(fact "process ID_REQUEST"
      (process-incomming-data (decode-message "255;255;3;0;3;\n"))
      => {:command :send
          :send "255;255;3;0;4;1\n"})


;(run-all-test






















































