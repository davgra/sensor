(ns sensor.core-test
  (:require [clojure.test :refer :all]
            [sensor.core :refer :all]
            [sensor.protocol :refer :all]))


(deftest test1
  (testing "Decode message"
    (is (= (decode-message
            "12;6;0;0;3;1.4\n"
            ) {:node-id 12
               :child-sensor-id 6
               :message-type :presentation
               :ack false
               :subtype :S_LIGHT
               :payload "1.4"}))))

(deftest test2
  (testing "Decode message"
    (is (= (decode-message
            "12;6;1;0;0;36.5\n"
            ) {:node-id 12
               :child-sensor-id 6
               :message-type :set
               :ack false
               :subtype :V_TEMP
               :payload "36.5"}))))

(deftest test3
  (testing "Decode message"
    (is (= (decode-message
            "13;7;1;0;2;1\n"
            ) {:node-id 13
               :child-sensor-id 7
               :message-type :set
               :ack false
               :subtype :V_LIGHT
               :payload "1"}))))

(deftest test4
  (testing "Decode message"
    (is (= (decode-message
            "1;255;0;0;17;1.4.1\n"
            ) {:node-id 1
               :child-sensor-id 255
               :message-type :presentation
               :ack false
               :subtype :S_ARDUINO_NODE
               :payload "1.4.1"}))))

(deftest test5
  (testing "process ID_REQUEST"
    (is (= (process-incomming-data (decode-message "255;255;3;0;3;\n"))
           {:command "255;255;3;0;4;3\n"}))))


;(run-all-tests)























































