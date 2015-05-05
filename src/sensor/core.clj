(ns sensor.core
  (:require [serial-port :as serial]
            [sensor.protocol :as prot]
            [clojure.pprint :as pp]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  )

(def arduino-filter #"/dev/tty[UA]")

;; (System/setProperty "gnu.io.rxtx.SerialPorts" "/dev/ttyACM0")
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

(defn irrigation [args setup]
  (let [port (:port setup)
        commands [
                  "105;10;1;0;3;58\n"
                  "105;11;1;0;2;1\n"
                  "105;11;1;0;2;0\n"
                  "105;10;1;0;3;118\n"
                  "105;11;1;0;2;1\n"
                  "105;11;1;0;2;0\n"

                  ;;                   "1;1;1;0;2;1\n"
                  ;;                   "1;1;1;0;2;0\n"
                  ]]
    (case  (:command args)
      "pump" (send-command "105;11;1;0;2;1\n" port)
      "stop" (send-command "105;11;1;0;2;0\n" port)
      "1" (send-command "105;10;1;0;3;58\n" port)
      "2" (send-command "105;10;1;0;3;118\n" port)
      )
    (str "console.log('recived: " (:command args) "')")))

(defn robot [args setup]
  (let [port (:port setup)
        speed (or (:speed args) 150)]
    (case  (:command args)
      "tanksteer" (let [left (:left args)
                        right (:right args)]
                    (send-command (str "1;1;1;0;3;" left "\n") port)
                    (send-command (str "1;2;1;0;3;" right "\n") port)
                    )
      "forward" (do
                  (send-command (str "1;1;1;0;3;" speed "\n") port)
                  (send-command (str "1;2;1;0;3;" speed "\n") port)
                  )
      "reverse" (do
                  (send-command "1;1;1;0;3;-150\n" port)
                  (send-command "1;2;1;0;3;-150\n" port)
                  )
      "left" (do
               (send-command "1;2;1;0;3;-150\n" port)
               (send-command "1;1;1;0;3;150\n" port)
               )
      "right" (do
                (send-command "1;2;1;0;3;150\n" port)
                (send-command "1;1;1;0;3;-150\n" port)
                )
      "stop" (do
               (send-command "1;1;1;0;3;0\n" port)
               (send-command "1;2;1;0;3;0\n" port))
      (println (str "unknown robot command: " (:command args))))

    (str "console.log('recived:" args "')")
    ))

(def setup (or setup {}))

(defroutes app-routes
  (GET "/" [] "Hello World!")
  (GET "/robot" [& more] (robot more setup))
  (GET "/irrigation" [& more] (irrigation more setup))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

(defn init []
  (println "init")
  (def setup
    (if (:port setup)
      setup
      (let [ports (map #(.getName %) (serial/list-ports))
            portpath (first (filter #(re-find arduino-filter %) pids))
            port (serial/open portpath 115200)
            ]
        (println portpath)
        (serial/on-byte port (handle-byte-port port))
        {:port port
         :portpath portpath}))))

(defn destroy []
  (println "destroy")
  (serial/close (:port setup)))
