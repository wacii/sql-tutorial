(ns sql-tutorial.handlers
  (:require [re-frame.core :refer [register-handler path]]
            [sql-tutorial.sql :as sql]
            [sql-tutorial.db :refer [init-state get-lesson]])
  (:require-macros [devcards.core :refer [defcard tests]]
                   [cljs.test :refer [is testing]]))

(defn get-actual
  "Set actual value of a test as the results of some query, either
    1) :current, meaning the the results of the current query
    2) or a string, represent a query to be run"
  [state test]
  (if (= (:actual test) :current)
    (assoc test :actual (:current state))
    (update test :actual sql/execute)))

(defn filter-actual
  "Tests may provide keys to filter the actual results.
  Sometimes you don't care if the results match exactly."
  [test]
  (if (contains? test :keys)
    (update test :actual #(select-keys % (:keys test)))
    test))

(defn test-passes? [test]
  (= (:actual test) (:expected test)))

(defn passing? [state test]
  (->> (get-actual state test)
       (filter-actual)
       (test-passes?)))

(defn tests-pass? [state]
  (every? (partial passing? state) (:tests state)))

; MIDDLEWARE
(defn ls-save [state]
  (.log js/console state)
  (.setItem js/localStorage "state" (pr-str state)))

(defn ls-load []
  (->> "state" (.getItem js/localStorage) (str) (cljs.reader/read-string)))

(defn ls [handler]
  (fn [state v]
    (let [new-state (handler state v)]
      (ls-save new-state)
      new-state)))

; TODO finish implementing show-query-results and schema
; HANDLERS
(defn execute-statement [state [_ statement]]
  (let [result (sql/execute statement)
        lesson (:current-lesson state)
        correct (tests-pass? lesson)]
    (assoc state
      :query statement
      :result result
      :show-query-results (sql/execute (:show-query lesson))
      :schema (sql/schema)
      :completed (or (:completed state) correct)
      :correct correct)))
(register-handler :execute ls execute-statement)

(defn change-lesson [id]
  (assoc init-state
    :current-lesson (get-lesson id)
    :current-lesson-id id))
(register-handler :change-lesson ls (fn [_ [_ id]] (change-lesson id)))

(register-handler
  :initialize-db
  (fn [_ _]
    (let [prev (ls-load)
          state (if (empty? prev)
                  (change-lesson 0)
                  prev)]
      (sql/reset-db (:db-setup (:current-lesson state)))
      state)))
