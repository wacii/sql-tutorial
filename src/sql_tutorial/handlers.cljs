(ns sql-tutorial.handlers
  (:require [re-frame.core :refer [register-handler path dispatch]]
            [sql-tutorial.sql :as sql]
            [sql-tutorial.db :refer [init-state get-lesson]])
  (:require-macros [devcards.core :refer [defcard tests]]
                   [cljs.test :refer [is testing]]))

(defn get-actual [result test]
  (if (= (:actual test) :current)
    result
    (-> (:actual test) (sql/execute))))

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

; TODO finish implementing show-query-results and schema
; HANDLERS
(defn execute-statement [state [_ statement]]
  (let [result (sql/execute statement)
        lesson (:current-lesson state)
        correct (tests-pass? result (:tests lesson))]
    (assoc state
      :query statement
      :current-query []
      :result result
      :show-query-results (sql/execute (:show-query lesson))
      :schema (sql/schema)
      :completed (or (:completed state) correct)
      :correct correct
      :error "")))
(defn process-error [state [_ statement] error]
  (assoc state
    :query statement
    :result {}
    :correct false
    :error (.-message error)))

(register-handler
  :execute
  (fn [db v]
    (try
      (execute-statement db v)
      (catch :default e
        (process-error db v e)))))

; TODO setup schema, run lesson startup code
(defn change-lesson [id]
  (assoc init-state
    :current-lesson (get-lesson id)
    :current-lesson-id id))
(register-handler :change-lesson ls (fn [_ [_ id]] (change-lesson id)))

(defn push-code-block [state [_ block]]
  (update state :current-query conj block))
(register-handler :push push-code-block)

(defn pop-code-block [state _]
  (update state :current-query pop))
(register-handler :pop pop-code-block)

(defn clear-code-blocks [state _]
  (assoc state :current-query []))
(register-handler :clear clear-code-blocks)

; TODO cleanup execute-statement so that it can be used more easily by both
;   handlers calling into it
(defn run-code-blocks [state [v]]
  (execute-statement state [v (clojure.string/join " " (:current-query state))]))
(register-handler :run run-code-blocks)

(register-handler
  :initialize-db
  (fn [_ _]
    (let [lesson-id (ls-load)
          state (change-lesson lesson-id)]
      (sql/reset-db (:db-setup (:current-lesson state)))
      state)))
