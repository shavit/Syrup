(ns syrup-chat.ui.feed
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [syrup-chat.socket :as socket]
              ))


(def guest-avatar (str
  "https://github.com/shavit/Syrup/blob/master/resources/public/img/avatar-"
  (+ 1 (rand-int 9))
  ".jpg?raw=true"))
(def guest-user {:id (rand-int 100), :name "Guest (you)", :avatar guest-avatar})

(def user-2 {:id 2, :name "User 2", :avatar (str
  "https://github.com/shavit/Syrup/blob/master/resources/public/img/avatar-"
  (+ 1 (rand-int 9))
  ".jpg?raw=true")})
(def user-3 {:id 3, :name "User 3", :avatar (str
  "https://github.com/shavit/Syrup/blob/master/resources/public/img/avatar-"
  (+ 1 (rand-int 9))
  ".jpg?raw=true")})
(def user-4 {:id 4, :name "User 4", :avatar (str
  "https://github.com/shavit/Syrup/blob/master/resources/public/img/avatar-"
  (+ 1 (rand-int 9))
  ".jpg?raw=true")})

(def username (atom nil))
(def guest-username (atom nil))
(def user (atom guest-user))
(def users (atom [guest-user, user-2, user-3, user-4]))
(def chat-messages (atom []))
(def chat-message (atom nil))

(defn date-format
  [params]

  (nth (clojure.string/split
    (js/Date
      (get params :created))
    "GMT") 0))

(defn i-key
  [params]
  (str (count @username) (count (get params :body)) (count @chat-messages)))

(defn submit-message
  [params]

  (.preventDefault params)
  (.push
    (socket/get-channel)
    "shout"
    (clj->js {:params
      {:user @user,
        :body @chat-message}}))

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

  [:div {:class "chat-message"}
    [:span {:class "avatar"}
      [:img {:src guest-avatar}]]
    [:span
      [:strong {:class "name"} @username]
      [:span {:class "datetime"} (str " " (date-format params))]]
    [:div
      (get params :body)]])

(defn render-messages
  []

  [:div {:class "messages-list"}
    (doall (for [m @chat-messages] ^{:key (get m :id)}
      [render-message m]))])

; #(+ % 1) expands into (fn [a] (+ a 1))
(defn message-box
  [params]
  [:div {:class "message-box"}
    [:form {:on-submit submit-message}
      [:input {:type "text",
        :value params, :on-change set-chat-message, :max-length 140}]]
    ]
  )

(defn render-user
  [params]
  [:div
    [:span {:class "avatar"}
      [:img {:src (get params :avatar)}]]
    [:strong (get params :name)]
    ])

(defn render-user-list
  []
  [:div {:class "descriptive details"}
    [:div {:class "user-list"}
      [:ul
        (doall (for [u @users]
          ^{:key (str (count (get u :name)) (get u :id) (get u :name))}
          [:li [render-user u]]))]]])

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

  [:div {:class "grid"}
    [:div {:class "four columns"}
      [:div
        [render-user-list]]]
    [:div {:class "eight columns"}
      [:div {:class "messages-feed"}
        (render-messages)
        (if
          (nil? @username)
          [login-box @guest-username]
          [message-box @chat-message])]]
  ])
