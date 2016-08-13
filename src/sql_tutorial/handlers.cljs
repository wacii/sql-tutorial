(ns sql-tutorial.handlers
  (:require [re-frame.core :refer [register-handler path]]
            [sql-tutorial.sql :as sql]
            [sql-tutorial.db :refer [init-state]])
  (:require-macros [devcards.core :refer [defcard tests]]
                   [cljs.test :refer [is testing]]))

;; TODO: navigate between lessons

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

; HANDLERS
(defn execute-statement [state [_ statement]]
  (let [result (sql/execute statement)
        lesson (:current-lesson state)]
    (update-in state [:current-lesson] assoc
      :query statement
      :result result
      :show-query-results (sql/execute (:show-query lesson))
      :schema (sql/schema)
      :completed (tests-pass? lesson))))
(register-handler :execute execute-statement)

(defn change-lesson [state [_ id]]
  (assoc state :current-lesson (get (:lessons state) id)))
(register-handler :change-lesson change-lesson)

; TODO select initial lesson given data from ls
(register-handler
  :initialize-db
  (fn [_ _]
    (let [lesson (first (:lessons init-state))
          state (assoc init-state :current-lesson lesson)]
      (sql/reset-db (:db-setup lesson))
      state)))
