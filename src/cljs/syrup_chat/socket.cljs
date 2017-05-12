(ns syrup-chat.socket
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [cljsjs.phoenix]
              ))

(def state (atom nil))
(def channel (atom nil))

(defn get-state
  []
  @state)

(defn get-channel
  []
  @channel)

(defn channel-connection
  []
  (let [socket (js/Phoenix.Socket. "ws://localhost:4001/socket")]
    (.connect socket)
    (.channel
      socket
      "chat:lobby")))

(defn join-channel
  [ch]
  (let [join (.join ch)]
    (.receive join
      "ok"
      (fn [x]
        (reset! state x)
        (.log js/console "Connected")))
    (.receive join
      "error"
      (fn [x]
        (.log js/console x))))
  )

(defn create
  []
  (let [ch (channel-connection)]
    (reset!
      channel
      ch)
      (join-channel ch))
  @channel)
