(ns uaw-spa.db
  (:require [cljs.reader]
            [cljs.spec :as s]
            [re-frame.core :as re-frame])
  )

;; -- Spec --------------------------------------------------------------------
;;
;; This is a clojure.spec specification for the value in app-db. It is like a
;; Schema. See: http://clojure.org/guides/spec
;;
;; The value in app-db should always match this spec. Only event handlers
;; can change the value in app-db so, after each event handler
;; has run, we re-check app-db for correctness (compliance with the Schema).
;;
;; How is this done? Look in events.cljs and you'll notice that all handlers
;; have an "after" interceptor which does the spec re-check.
;;
;; None of this is strictly necessary. It could be omitted. But we find it
;; good practice.

(s/def ::resource-value int?)
(s/def ::resource string?)
(s/def ::nil boolean?)
(s/def ::column (s/keys :req-un [::resource ::resource-value ::nil]))
(s/def ::row (s/and                                       ;; should use the :kind kw to s/map-of (not supported yet)
                 (s/map-of ::resource ::resource-value)                     ;; in this map, each todo is keyed by its :id
                 #(instance? PersistentTreeMap %)           ;; is a sorted-map (not just a map)
                 ))
(s/def ::showing                                            ;; what todos are shown to the user?
  #{:all                                                    ;; all todos are shown
    :nonnil                                                 ;; only todos whose :nil is false
    :nil                                                   ;; only todos whose :nil is true
    })
(s/def ::db (s/keys :req-un [::row ::showing]))

;; -- Default app-db Value  ---------------------------------------------------
;;
;; When the application first starts, this will be the value put in app-db
;; Unless, of course, there are todos in the LocalStore (see further below)
;; Look in `core.cljs` for  "(dispatch-sync [:initialise-db])"
;;

(def default-value                                          ;; what gets put into app-db by default.
  {:realtimestats   (sorted-map)                                    ;; an empty list of todos. Use the (int) :id as the key
   :showing :all})                                          ;; show all realtimestats


;; -- Local Storage  ----------------------------------------------------------
;;
;; Part of the todomvc challenge is to store todos in LocalStorage, and
;; on app startup, reload the todos from when the program was last run.
;; But the challenge stipulates to NOT  load the setting for the "showing"
;; filter. Just the todos.
;;

(def ls-key "uaw-reframe")                          ;; localstore key
(defn todos->local-store
  "Puts todos into localStorage"
  [todos]
  (.setItem js/localStorage ls-key (str todos)))     ;; sorted-map writen as an EDN map


;; register a coeffect handler which will load a value from localstore
;; To see it used look in events.clj at the event handler for `:initialise-db`
(re-frame/reg-cofx
  :local-store-todos
  (fn [cofx _]
      "Read in todos from localstore, and process into a map we can merge into app-db."
      (assoc cofx :local-store-todos
             (into (sorted-map)
                   (some->> (.getItem js/localStorage ls-key)
                            (cljs.reader/read-string)       ;; stored as an EDN map.
                            )))))

(def default-db
  {:name "re-frame"})
