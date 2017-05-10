(ns syrup-chat.ui.feed
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              ))


(def guest-avatar (str
  "https://github.com/shavit/Syrup/blob/master/resources/public/img/avatar-"
  (rand-int 9)
  ".jpg?raw=true"))
(def guest-user {:id nil, :name "Guest (you)", :avatar guest-avatar})

(def username (atom nil))
(def guest-username (atom nil))
(def user (atom guest-user))
(def users (atom [guest-user]))
(def chat-messages (atom []))
(def chat-message (atom nil))

(defn date-format
  [params]

  (nth (clojure.string/split
    (js/Date (get params :created))
    "GMT") 0))

(defn i-key
  [params]
  (str (count @username) (count (get params :body)) (count @chat-messages)))

(defn submit-message
  [params]

  (.preventDefault params)

  (if (nil? @username)
    nil
    (swap!
      chat-messages
        conj {:id (i-key params),
          :from @username,
          :body @chat-message,
          :created (.now js/Date)}))

  (reset! chat-message ""))

(defn set-chat-message
  [params]

  (reset!
    chat-message
      (-> params .-target .-value)))

(defn render-message
  [params]

  [:div
    [:span
      [:img {:src guest-avatar}]]
    [:span
      [:strong @username]
      [:small (str " " (date-format params))]]
    [:div
      (get params :body)]])

(defn render-messages
  []

  [:div
    (doall (for [m @chat-messages] ^{:key (get m :id)}
      [render-message m]))])

; #(+ % 1) expands into (fn [a] (+ a 1))
(defn message-box
  [params]
  [:div
    [:form {:on-submit submit-message}
      [:input {:type "text",
        :value params, :on-change set-chat-message, :max-length 140}]]
    ]
  )

(defn render-user
  [params]
  [:div
    [:span
      [:img {:src guest-avatar}]]
    [:strong (get params :name)]
    ])

(defn render-user-list
  []
  [:div {:class "descriptive details"}
    [:ul
      (doall (for [u @users] ^{:key u}
        [:li [render-user u]]))]])

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
        :placeholder "Choose a username", :on-change set-guest-username,
        :max-length 12}]]
    ]
  )

(defn view
  [params]

  [:div {:class "grid"}
    [:div {:class "four columns"}
      [:div
        [render-user-list]]]
    [:div {:class "two columns"}
      (render-messages)
      (if
        (nil? @username)
        [login-box @guest-username]
        [message-box @chat-message])]
  ])
