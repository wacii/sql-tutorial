(ns sql-tutorial.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]))

(register-sub
  :current-lesson
  (fn [db _]
    (reaction
      (select-keys (:current-lesson @db) [:title :description]))))

(register-sub
  :current-query
  (fn [db _]
    (reaction
      (select-keys @db [:query :result]))))

(def ^:private select-vals (comp vals select-keys))

(register-sub
  :lessons
  (fn [db _]
    (reaction
      (select-vals @db [:current-lesson-id :lessons-info]))))

(register-sub
  :completed
  (fn [db _]
    (reaction
      (select-keys @db [:completed :correct]))))
