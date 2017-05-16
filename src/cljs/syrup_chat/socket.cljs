(ns syrup-chat.socket
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [cljsjs.phoenix]
              ))

(def state (atom nil))
(def channel (atom nil))

;
;   Socket
;
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

(defn format-message
  [m]
  {"id" (get m "id"),
  "nickname" (get m "nickname"),
  "picture" (get m "picture"),
  "body" (get m "body"),
  "created" (get m "created")})

(defn channel-handler
  [ch]

  (.on
    ch
    "shout"
    (fn [x]
      (let [m (js->clj x)]
        (let [new-message
          (format-message m)]
            (swap!
              state
              update-in
              ["chat_messages"]
              conj
              new-message)
          )))
    )
  )

(defn join-channel
  [ch]
  (let [join (.join ch)]
    (.receive join
      "ok"
      (fn [x]
        (reset! state (js->clj x))
        (.log js/console "Connected")))

    (.receive join
      "error"
      (fn [x]
        (.log js/console x))))
  )

(defn create
  []
  (let [ch (channel-connection)]
    (channel-handler ch)
    (reset!
      channel
      ch)
      (join-channel ch))
  @channel)

;
;   Storage
;
(defn get-messages
  []
  (get
    @state
    "chat_messages"))

(defn get-users
  []
  "Get the online users"
  ; (get
  ;   @state
  ;   "users")
  [])
