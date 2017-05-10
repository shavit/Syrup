(ns syrup-chat.socket
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              ))

(def socket (atom nil))

; socket = new WebSocket("ws://localhost:4001/socket/websocket")
; msg = {topic: "", event: "", ref: "", payload: {body: "Hi"}}
; socket.send(JSON.stringify({topic: "chat", event: "phx_join", ref: "", payload: {body: "Hi"}}))

(defn create
  []
  (reset!
    socket
    (new js/WebSocket "ws://localhost:4001/socket/websocket"))

  (.addEventListener
    @socket
    "open"
    (fn [x]
      (.log js/console x)
      (.log js/console "opened")))

  (.addEventListener
    @socket
    "message"
    (fn [x]
      (.log js/console x)
      (.log js/console "received a message")
      ))

  @socket)
; (create-websocket)
