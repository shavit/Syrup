(ns syrup-chat.ui.feed
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              ))


(def username (atom nil))
(def guest-username (atom nil))
(def chat-messages (atom []))
(def chat-message (atom nil))

(defn submit-message
  [params]

  (.preventDefault params)

  (if (nil? @username)
    nil
    (swap!
      chat-messages
        conj @chat-message))

  (reset! chat-message ""))

(defn set-chat-message
  [params]

  (reset!
    chat-message
      (-> params .-target .-value)))

(defn render-message
  [params]
  [:div
    [:strong (str @username ": ")]
    [:span params]])

(defn render-messages
  []

  [:div
    (for [m @chat-messages] ^{:key m}
      (render-message m))])

; #(+ % 1) expands into (fn [a] (+ a 1))
(defn message-box
  [params]
  [:div
    [:form {:on-submit submit-message}
      [:input {:type "text",
        :value params, :on-change set-chat-message}]]
    ]
  )

(defn submit-username
  [params]

  (.preventDefault params)
  (reset!
    username
      @guest-username))

(defn set-guest-username
  [params]

  (reset!
    guest-username
      (-> params .-target .-value)))

(defn login-box
  [params]
  [:div
    [:form {:on-submit submit-username}
      [:input {:type "text", :value params,
        :placeholder "Choose a username", :on-change set-guest-username}]]
    ]
  )

(defn view
  [params]
  [:div {:class "grid"}
    (render-messages)
    (if
      (nil? @username)
      [login-box @guest-username]
      [message-box @chat-message])
  ])
