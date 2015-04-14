(ns sensor.core
  (:require [serial-port :as serial]
            [sensor.protocol :as prot]
            [clojure.pprint :as pp])
  ;(:use serial-port)
  )

(def pids (map #(.getName %) (serial/port-ids)))
(filter #(re-find #"/dev/ttyU" %) pids)

(println pids)

(count pids)
;(serial/port-at 0)
;(serial/port-at 1)
(defn handle-line-old [line]
  ;(println (prot/decode-message line))
  (pp/pprint (prot/decode-message line))
  )

(defn handle-line [line port]
  (let [incomming-data (prot/decode-message line)
        outgoing-data (prot/process-incomming-data incomming-data)
        ]
    (pp/pprint incomming-data)
    (pp/pprint outgoing-data)
    (if (:command outgoing-data)
      (do
        (println "sending command")
        (doseq [c (map int (:command outgoing-data))]
          (serial/write-int port c))
        ))
    outgoing-data
    ))


;(handle-line "12;6;1;0;0;36.5\n")

(def in-string (atom ""))

(defn handle-byte [byte port]
  (print (char byte))
  (flush)
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

(defn -main [& args]
  (let [ports (map #(.getName %) (serial/list-ports))
        port (first (filter #(re-find #"/dev/ttyU" %) pids))]
    (println port)
    (def port (serial/open port 115200)))
  (serial/on-byte port (handle-byte-port port))

  (loop [count 1]
    (Thread/sleep 1000)
    (flush)
    (if (= (mod 10 count) 0)
      (do
        (println "sending command on")
        ;(doseq [c (map int "105;0;2;0;1;\n")]
        ;  (serial/write-int port c))
        ))
    (if (= (mod 10 (+ 5 count)) 0)
      (do
        (println "sending command off")
        ;(doseq [c (map int "105;0;2;0;1;\n")]
        ;  (serial/write-int port c))
        ))
    (recur (inc count))

    )
  )

