(ns syrup-chat.prod
  (:require [syrup-chat.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
