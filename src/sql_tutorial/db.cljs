(ns sql-tutorial.db)

(def lessons
  [{:id 0
    :title "Basic Queries"
    :description "A description!"
    :db-setup "DROP TABLE IF EXISTS users;
               CREATE TABLE users (name, email);
               INSERT INTO users (name, email)
               VALUES ('Sam', 'sam1234@example.com'),
                      ('Joe', 'jjguy@example.com');"
    :tests [{:expected [{:name "Sam"}]
             :keys [:name]
             :actual :current}
            {:expected [{:name "John"}]
             :actual "SELECT name FROM users;"}]
    :show-schema-for :all
    :show-query "SELECT * FROM users;"
    :completed false
    :query ""
    :result {}}
   {:id 1
    :title "Basic Queries II"
    :description "More stuff!"
    :db-setup "DROP TABLE IF EXISTS users;
               CREATE TABLE users (name, email);
               INSERT INTO users (name, email)
               VALUES ('Sam', 'sam1234@example.com'),
                      ('Joe', 'jjguy@example.com');"
    :tests [{:expected [{:name "Sam"}]
             :keys [:name]
             :actual :current}
            {:expected [{:name "John"}]
             :actual "SELECT name FROM users;"}]
    :show-schema-for :all
    :show-query "SELECT * FROM users;"
    :completed false
    :query ""
    :result {}}])

(defn get-lesson [position]
  (get lessons position))

(defn lessons-info []
  (map-indexed
    (fn [i lesson] {:id i, :title (:title lesson)})
    lessons))

; TODO schema to enforce proper data
(def init-state
  {:current-lesson {}
   :current-lesson-id 0
   :query ""
   :result {}
   :completed false
   :correct false
   :show-query-results {}
   :schema {}
   :lessons-info (lessons-info)})
