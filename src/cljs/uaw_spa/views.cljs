(ns uaw-spa.views
  (:require [re-frame.core :as re-frame]))

(defn main-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:h1 "Hola " @name]
      [request-it-button])))


(defn top-panel    ;; this is new
  []
  (let [ready?  (re-frame/subscribe [:initialised?])]
    (if-not @ready?         ;; do we have good data?
      [:div "Initialising ..."]   ;; tell them we are working on it
      [main-panel])))      ;; all good, render this component


(defn request-it-button
  []
  [:div {:class "button-class"
         :on-click  #(dispatch [:request-it])}  ;; get data from the server !!
         "I want it, now!"])
