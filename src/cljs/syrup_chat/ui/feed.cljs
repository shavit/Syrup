(ns syrup-chat.ui.feed
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [syrup-chat.socket :as socket]
              ))


(def guest-avatar (str
  "https://github.com/shavit/Syrup/blob/master/resources/public/img/avatar-"
  (+ 1 (rand-int 8))
  ".jpg?raw=true"))
(def guest-user {:id (rand-int 100), :name "Guest (you)", :avatar guest-avatar})

(def username (atom nil))
(def guest-username (atom nil))
(def user (atom guest-user))
(def users (atom [@user]))
(def chat-message (atom nil))

(defn date-format
  [params]

  (nth (clojure.string/split
    (js/Date
      params)
    "GMT") 0))

(defn submit-message
  [params]

  (.preventDefault params)
  (.push
    (socket/get-channel)
    "shout"
    (clj->js {:params
      {:user @user,
        :body @chat-message}}))

  (reset! chat-message ""))

(defn set-chat-message
  [params]

  (reset!
    chat-message
      (-> params .-target .-value)))

(defn render-message
  [params]

  [:div {:class "chat-message"}
    [:span {:class "avatar"}
      [:img {:src (get params "picture")}]]
    [:span
      [:strong {:class "name"} (get params "nickname")]
      [:span {:class "datetime"} (str " " (date-format (get params "created")))]]
    [:div
      (get params "body")]])

(defn render-messages
  []

  [:div {:class "messages-list"}
    (doall (for [m (socket/get-messages)]
      ^{:key (str (get m "id") (count (socket/get-messages)))}
      [render-message m]))])

; #(+ % 1) expands into (fn [a] (+ a 1))
(defn message-box
  [params]
  [:div {:class "message-box"}
    [:form {:on-submit submit-message}
      [:input {:type "text",
        :value params, :on-change set-chat-message, :max-length 140}]
      [:div (str (count @chat-message) "/140")]]
    ]
  )

(defn render-user
  [params]
  [:div
    [:span {:class "avatar"}
      [:img {:src (get params "avatar")}]]
    [:strong (get params "name")]
    ])

(defn render-user-list
  []
  [:div {:class "descriptive details"}
    [:div {:class "user-list"}
      [:ul
        (doall (for [u (socket/get-users)]
          ^{:key (get u "id")}
          [:li [render-user u]]))]]])

(defn submit-username
  [params]

  (.preventDefault params)
  (reset! username @guest-username)
  (reset!
    user
      {:id (rand-int 100),
        :name @username,
        :avatar guest-avatar})
  (reset! users (conj @users @user))
  )

(defn set-guest-username
  [params]

  (reset!
    guest-username
      (-> params .-target .-value)))

(defn login-box
  [params]
  [:div {:class "message-box"}
    [:form {:on-submit submit-username}
      [:input {:type "text", :value params,
        :placeholder "Choose a username", :on-change set-guest-username,
        :max-length 12}]]
    ]
  )

(defn view
  [params]
  "View receiving a state with a channel"
  ; (load-messages)

  [:div {:class "grid"}
    [:div {:class "four columns"}
      [:div
        [:video {:autoPlay true, :controls true}]
        [:strong "Live Stream"]]
      (render-user-list)]
    [:div {:class "eight columns"}
      [:div {:class "messages-feed"}
        (render-messages)
        (if
          (nil? @username)
          [login-box @guest-username]
          [message-box @chat-message])]]
  ])
