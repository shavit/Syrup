(ns syrup-chat.ui.feed
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              ))


(def chat-messages (atom []))
(def chat-message (atom nil))

(defn submit-message
  [params]

  (.preventDefault params)

  (swap!
    chat-messages
      conj @chat-message)

  (reset! chat-message ""))

(defn set-chat-message
  [params]

  (reset!
    chat-message
      (-> params .-target .-value)))

(defn render-message
  [params]
  [:div
    [:strong "Me: "]
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

(defn view
  [params]
  [:div {:class "grid"}
    [:div "messages here"]
    (render-messages)
    [message-box @chat-message]
  ])
