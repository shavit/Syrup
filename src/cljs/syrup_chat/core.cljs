(ns syrup-chat.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [syrup-chat.socket :as socket]
              [syrup-chat.ui.feed :as feed]))

;; -------------------------
;; State
(def ws-channel (atom nil))

;; -------------------------
;; Views

(defn home-page []
  [:div
    [:header {:class "active"}
      [:nav {:class "grid"}
        [:div {:class "four columns"} " "]
        [:div {:class "two columns"}
          [:a {:class "brand", :href "/"} "Syrup Chat"]]
        [:div {:class "four columns"} " "]]]
    (feed/view @ws-channel)
  ])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

; (secretary/defroute "/profile" []
;   (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (reset! ws-channel (socket/create))
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
