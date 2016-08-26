(ns sql-tutorial.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]
            [sql-tutorial.db :as db]))

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
  :blocks
  (fn [db _]
    (reaction
      (let [schema-blocks (-> @db :schema vec flatten)]
        {:query-blocks (:current-query @db)
         :block-category (:block-category @db)
         :block-groups (merge {"Recent" (:blocks @db)}
                              db/blocks-map
                              {"Database" schema-blocks})}))))

(register-sub
  :keyboard-input?
  (fn [db _]
    (reaction (:keyboard-input? @db))))
