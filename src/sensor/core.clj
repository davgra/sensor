(ns sensor.core
  (:require [serial-port :as serial]
            [sensor.protocol :as prot]
            [clojure.pprint :as pp])
  ;(:use serial-port)
  )

(def arduino-filter #"/dev/tty[UA]")

(System/setProperty "gnu.io.rxtx.SerialPorts" "/dev/ttyACM0")
;; (System/setProperty "gnu.io.rxtx.SerialPorts" "/dev/ttyACM1")

(def pids (map #(.getName %) (serial/port-ids)))
(filter #(re-find arduino-filter %) pids)

(println pids)

(count pids)
;(serial/port-at 0)
;(serial/port-at 1)
(defn handle-line-old [line]
  ;(println (prot/decode-message line))
  (pp/pprint (prot/decode-message line))
  )

(def database (atom {}))

(defn send-command [command port]
  (doseq [c (map int command)]
    (serial/write-int port c)))

(defn handle-line [line port]
  (let [incomming-data (prot/decode-message line)
        outgoing-data (prot/process-incomming-data incomming-data)
        ]
    (case (:command outgoing-data)
      :send (do
              (println "sending command")
              (doseq [c (map int (:send outgoing-data))]
                (serial/write-int port c)))
      :set (do
             (pp/pprint (:set outgoing-data)))
      nil nil
      (println (str "Unknown command: " (:command outgoing-data)))
      )
    (when (:log outgoing-data)
      (println (:log outgoing-data)))
    (when (:print outgoing-data)
      (pp/pprint incomming-data)
      (pp/pprint outgoing-data)
      )
    ;(pp/pprint outgoing-data)
    outgoing-data
    ))


;(handle-line "12;6;1;0;0;36.5\n")

(def in-string (atom ""))

(defn handle-byte [byte port]
;;   (print (char byte))
;;   (flush)
  (if (= (char byte) \newline)
    (do
      (handle-line @in-string port)
      (reset! in-string ""))
    (swap! in-string #(str % (char byte)))
    )
  )

(defn handle-byte-port [port]
  (fn [byte]
    (handle-byte byte port)))


(defn- hande-user-input [line port cnt]
    (let [
          commands [
                    "105;10;1;0;3;58\n"
                    "105;11;1;0;2;1\n"
                    "105;11;1;0;2;0\n"
                    "105;10;1;0;3;118\n"
                    "105;11;1;0;2;1\n"
                    "105;11;1;0;2;0\n"

;;                     "1;1;1;0;2;1\n"
;;                     "1;1;1;0;2;0\n"
                     ]
          i (mod cnt (count commands))
          ]
        (println "sending command")
        (pp/pprint (prot/decode-message (nth commands i)))
        (doseq [c (map int (nth commands i))]
          (serial/write-int port c))
      )
  )


(defn -main [& args]
  (let [ports (map #(.getName %) (serial/list-ports))
        port (first (filter #(re-find arduino-filter %) pids))]
    (println port)
    (def port (serial/open port 115200)))
  (serial/on-byte port (handle-byte-port port))

  (loop [count 1]
;;     (Thread/sleep 1000)
    (flush)
    (let [line (read-line)]
;;       (println line)
      (hande-user-input line port count))
    ;;     (if (= (mod 10 count) 0)
    ;;       (do
    ;;         (println "sending command on")
    ;;         ;(doseq [c (map int "105;0;2;0;1;\n")]
    ;;         ;  (serial/write-int port c))
    ;;         ))
    ;;     (if (= (mod 10 (+ 5 count)) 0)
    ;;       (do
    ;;         (println "sending command off")
    ;;         ;(doseq [c (map int "105;0;2;0;1;\n")]
    ;;         ;  (serial/write-int port c))

    (recur (inc count))

    )
  )

