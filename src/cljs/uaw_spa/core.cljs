(ns uaw-spa.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [uaw-spa.events]
            [uaw-spa.subs]
            [uaw-spa.views :as views]
            [uaw-spa.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/top-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialise-db])
  (dev-setup)
  (mount-root))
