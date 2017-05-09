(ns ^:figwheel-no-load syrup-chat.dev
  (:require [syrup-chat.core :as core]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(core/init!)
