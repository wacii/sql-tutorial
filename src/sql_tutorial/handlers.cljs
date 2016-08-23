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

; HANDLERS
(defn execute-statement [state statement]
  (let [result (sql/execute statement)
        lesson (:current-lesson state)
        correct (tests-pass? result (:tests lesson))]
    (assoc state
      :query statement
      :current-query []
      :result result
      :schema (sql/schema)
      :completed (or (:completed state) correct)
      :correct correct
      :error "")))
(defn process-error [state statement error]
  (assoc state
    :query statement
    :result {}
    :correct false
    :error (.-message error)))

(register-handler
  :execute
  (fn [db [_ statement]]
    (try
      (execute-statement db statement)
      (catch :default error
        (process-error db statement error)))))

(defn change-lesson [state id]
  (let [lesson (get-lesson id)]
    (sql/reset-db (:db-setup lesson))
    (assoc state
      :current-query []
      :query ""
      :result {}
      :completed false
      :correct false
      :current-lesson lesson
      :current-lesson-id id
      :schema (sql/schema)
      :blocks (->> (:blocks lesson)
                (into (:mru-blocks state))
                (distinct)
                (take 10)))))
(register-handler :change-lesson ls (fn [state [_ id]] (change-lesson state id)))

(defn toggle-input-style [state _]
  (update state :keyboard-input? not))
(register-handler :toggle-input-style toggle-input-style)

(defn push-code-block [state [_ block]]
  (-> state
    (update :mru-blocks conj block)
    (update :current-query conj block)))
(register-handler :push push-code-block)

(defn pop-code-block [state _]
  (update state :current-query pop))
(register-handler :pop pop-code-block)

(defn clear-code-blocks [state _]
  (assoc state :current-query []))
(register-handler :clear clear-code-blocks)

(defn run-code-blocks [state _]
  (execute-statement state (clojure.string/join " " (:current-query state))))
(register-handler :run run-code-blocks)

(register-handler
  :initialize-db
  (fn [_ _]
    (let [lesson-id (ls-load)
          state (change-lesson init-state lesson-id)]
      (sql/reset-db (:db-setup (:current-lesson state)))
      state)))
