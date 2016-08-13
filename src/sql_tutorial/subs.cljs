(ns sql-tutorial.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]))

(register-sub
  :current-lesson
  (fn [db _]
    (reaction
      (select-keys (:current-lesson @db) [:title :description :completed]))))

(register-sub
  :current-query
  (fn [db _]
    (reaction
      (select-keys (:current-lesson @db) [:query :result]))))

(register-sub
  :lessons
  (fn [db _]
    (reaction
      {:current (get-in @db [:current-lesson :id])
       :lessons (:lessons @db)})))
