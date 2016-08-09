(ns sql-tutorial.sql
  (:require [re-frame.core :as re-frame]))

(def db (js/SQL.Database.))

(defn reset-db
  ([]
   (.close db)
   (set! db (js/SQL.Database.)))
  ([statements]
   (reset-db)
   (.run db statements)))

(defn ^:private sql->map [results]
  (map #(zipmap (get results "columns") %) (get results "values")))

;; TODO: move back the keyword column and values
(defn ^:private sql-execute [statement]
  (->> (.exec db statement) (js->clj) (first)))

(defn execute [statement]
  (try
    [:success (sql->map (sql-execute statement))]
    (catch :default e
      [:error e])))

(defn ^:private query-column [statement]
  (first (:values (sql-execute statement))))

(defn ^:private add-column-names [table-info table-name]
  (let [statement (str "SELECT name FROM (PRAGMA table_info(" table-name "));")
        column-names (query-column statement)]
    (assoc table-info table-name column-names)))

(defn schema []
  (reduce add-column-names {} (query-column "SELECT name FROM sqlite_master;")))
