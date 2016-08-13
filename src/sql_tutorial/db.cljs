(ns sql-tutorial.db)

(def lessons
  [{:title "SELECT"
    :description "A description!"
    :db-setup "DROP TABLE IF EXISTS users;
               CREATE TABLE users (name, email);
               INSERT INTO users (name, email)
               VALUES ('Sam', 'sam1234@example.com'),
                      ('Joe', 'jjguy@example.com');"
    :tests [{:expected [{"name" "Sam"}]
             :keys ["name"]
             :actual :current}]
    :show-schema-for :all
    :show-query "SELECT * FROM users;"
    :completed false
    :query ""
    :result {}}
   {:title "INSERT"
    :description "More stuff!"
    :db-setup "DROP TABLE IF EXISTS users;
               CREATE TABLE users (name, email);
               INSERT INTO users (name, email)
               VALUES ('Sam', 'sam1234@example.com'),
                      ('Joe', 'jjguy@example.com');"
    :tests [{:expected [{"name" "John"}]
             :actual "SELECT DISTINCT name FROM users where name = \"John\";"}]
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
