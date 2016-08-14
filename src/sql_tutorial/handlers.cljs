(ns sql-tutorial.handlers
  (:require [re-frame.core :refer [register-handler path]]
            [sql-tutorial.sql :as sql]
            [sql-tutorial.db :refer [init-state get-lesson]])
  (:require-macros [devcards.core :refer [defcard tests]]
                   [cljs.test :refer [is testing]]))

; TODO no reason to update current lesson tests with actual results
;   do not add more state unless necessary, just pass the value along
(defn get-actual
  "Set actual value of a test as the results of some query, either
    1) :current, meaning the the results of the current query
    2) or a string, represent a query to be run"
  [result test]
  (if (= (:actual test) :current)
    (assoc test :actual result)
    (update test :actual (comp second sql/execute))))
; TODO ^ more of this success/error results nonsense

(defn filter-actual
  "Tests may provide keys to filter the actual results.
  Sometimes you don't care if the results match exactly."
  [test]
  (if (contains? test :keys)
    (select-keys (:actual test) (:keys test))
    test))

(defn test-passes? [test]
  (= (:actual test) (:expected test)))

(defn passing? [result test]
  (->> (get-actual result test)
       (filter-actual)
       (test-passes?)))

(defn tests-pass? [result tests]
  (every? (partial passing? result) tests))

; MIDDLEWARE
(defn ls-load []
  (-> (.getItem js/localStorage "lesson") (int)))

(defn ls [handler]
  (fn [state v]
    (let [new-state (handler state v)]
      (.setItem js/localStorage "lesson" (:current-lesson-id new-state))
      new-state)))

; TODO how to handle success vs error result here
; TODO finish implementing show-query-results and schema
; HANDLERS
(defn execute-statement [state [_ statement]]
  (let [result (sql/execute statement)
        lesson (:current-lesson state)
        correct (tests-pass? (second result) (get-in state [:current-lesson :tests]))]
    (assoc state
      :query statement
      :result result
      :show-query-results (sql/execute (:show-query lesson))
      :schema (sql/schema)
      :completed (or (:completed state) correct)
      :correct correct)))
(register-handler :execute execute-statement)

(defn change-lesson [id]
  (assoc init-state
    :current-lesson (get-lesson id)
    :current-lesson-id id))
(register-handler :change-lesson ls (fn [_ [_ id]] (change-lesson id)))

(register-handler
  :initialize-db
  (fn [_ _]
    (let [lesson-id (ls-load)
          state (change-lesson lesson-id)]
      (sql/reset-db (:db-setup (:current-lesson state)))
      state)))
