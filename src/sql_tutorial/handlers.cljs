(ns sql-tutorial.handlers
  (:require [re-frame.core :refer [register-handler path]]
            [sql-tutorial.sql :as sql]
            [sql-tutorial.db :refer [init-state get-lesson]])
  (:require-macros [devcards.core :refer [defcard tests]]
                   [cljs.test :refer [is testing]]))

(defn get-actual [result test]
  (if (= (:actual test) :current)
    result
    (-> (:actual test) (sql/execute) (second))))
; TODO ^ more of this success/error results nonsense

(defn filter-actual [result test]
  (if (contains? test :keys)
    (for [record result] (select-keys record (:keys test)))
    result))

(defn passing? [result test]
  (-> result
    (get-actual test)
    (filter-actual test)
    (= (:expected test))))

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
