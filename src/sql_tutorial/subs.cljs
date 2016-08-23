(ns sql-tutorial.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]))

(register-sub
  :current-lesson
  (fn [db _]
    (reaction
      (select-keys (:current-lesson @db) [:title :description]))))

(register-sub
  :previous-query
  (fn [db _]
    (reaction
      (select-keys @db [:query :result :error]))))

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
      (let [lesson-id (inc (:current-lesson-id @db))
            lesson-count (count (:lessons-info @db))
            next-lesson (if (>= lesson-id lesson-count) nil lesson-id)]
        (merge
          (select-keys @db [:completed :correct])
          {:next-lesson next-lesson})))))

(register-sub
  :current-query
  (fn [db _]
    (reaction (:current-query @db))))

(register-sub
  :schema
  (fn [db _]
    (reaction (:schema @db))))

(register-sub
  :blocks
  (fn [db _]
    (reaction (:blocks @db))))

(register-sub
  :keyboard-input?
  (fn [db _]
    (reaction (:keyboard-input? @db))))
