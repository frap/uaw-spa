(ns uaw-spa.events
  (:require [ajax.core :refer [GET]]
            [re-frame.core :as re-frame]
            [uaw-spa.db :as db]))

(re-frame/reg-event-db
 :initialise-db
 (fn  [db _]
   (assoc db :display-name "gas da Man")))

(re-frame/reg-event-db        ;; <-- register an event handler
  :request-it        ;; <-- the event id
  (fn                ;; <-- the handler function
    [db _]

    ;; kick off the GET, making sure to supply a callback for success and failure
    (GET
      "http://192.168.66.1"
      {:handler       #(dispatch [:process-response %1])   ;; <2> further dispatch !!
       :error-handler #(dispatch [:bad-response %1])})     ;; <2> further dispatch !!

     ;; update a flag in `app-db` ... presumably to cause a "Loading..." UI
     (assoc db :loading? true)))

(re-frame/reg-event-fx        ;; <-- note the `-fx` extension
  :request-it2        ;; <-- the event id
  (fn                ;; <-- the handler function
    [{db :db} _]     ;; <-- 1st argument is coeffect, from which we extract db

    ;; we return a map of (side) effects
    {:http-xhrio {:method          :get
                  :uri             "http://192.168.66.1/"
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:process-response]
                  :on-failure      [:bad-response]}
     :db  (assoc db :loading? true)}))

(re-frame/reg-event-db
  :process-response
  (fn
    [db [_ response]]           ;; destructure the response from the event vector
    (-> db
        (assoc :loading? false) ;; take away that "Loading ..." UI
        (assoc :data (js->clj response))))  ;; fairly lame processing
  )
