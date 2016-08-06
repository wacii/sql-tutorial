(ns sql-tutorial.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]))

(register-sub
  :current-lesson
  (fn [db _]
    (reaction (select-keys @db [:title :description :completed]))))

(register-sub
  :current-query
  (fn [db _]
    (reaction (select-keys @db [:query :result]))))
