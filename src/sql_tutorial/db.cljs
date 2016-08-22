(ns sql-tutorial.db)

(def lessons
  [{:title "Select a column"
    :description "Learn how to write sql queries to fetch records from a database. \nThe most basic select query takes the form `SELECT [column names] FROM [table name];`. An example against a database with table `users` and columns `name` and `email`: `SELECT email FROM users;` \nNow you! Select the names of all users from the database."
    :db-setup "DROP TABLE IF EXISTS users;
               CREATE TABLE users (name, email);
               INSERT INTO users (name, email)
               VALUES ('Sam', 'sam1234@example.com'),
                      ('Joe', 'jjguy@example.com');"
    :tests [{:expected [{"name" "Sam"} {"name" "Joe"}]
             :actual :current}]
    :blocks ["SELECT" "email" "FROM" "users" ";" "name"]}
   {:title "Select multiple columns"
    :description "Multiple columns may be specified in a comma-separated list. \nTry selecting both `name` and `email` from `users`."
    :db-setup "DROP TABLE IF EXISTS users;
               CREATE TABLE users (name, email);
               INSERT INTO users (name, email)
               VALUES ('Sam', 'sam1234@example.com'),
                      ('Joe', 'jjguy@example.com');"
    :tests [{:expected [{"name" "Sam", "email" "sam1234@example.com"}
                        {"name" "Joe", "email" "jjguy@example.com"}]
             :actual :current}]
    :blocks ["," "name" "email"]}
   {:title "Select all columns"
    :description "As a shortcut, an asterisk (`*`) may be used to specify all columns, instead of writing them all out. \nTry selecting all columns from `users`."
    :db-setup "DROP TABLE IF EXISTS users;
               CREATE TABLE users (name, email, id int);
               INSERT INTO users (name, email, id)
               VALUES ('Sam', 'sam1234@example.com', 1),
                      ('Joe', 'jjguy@example.com', 2);"
    :tests [{:expected [{"id" 1, "name" "Sam", "email" "sam1234@example.com"}
                        {"id" 2, "name" "Joe", "email" "jjguy@example.com"}]
             :actual :current}]
    :blocks ["*" "users"]}
   {:title "SELECT test"
    :description "A description!"
    :db-setup "DROP TABLE IF EXISTS users;
               CREATE TABLE users (name, email);
               INSERT INTO users (name, email)
               VALUES ('Sam', 'sam1234@example.com'),
                      ('Joe', 'jjguy@example.com');"
    :tests [{:expected [{"name" "Sam"}]
             :keys ["name"]
             :actual :current}]
    :blocks ["FROM" "name" "SELECT" "\"Sam\"" "users"]}
   {:title "INSERT test"
    :description "More stuff!"
    :db-setup "DROP TABLE IF EXISTS users;
               CREATE TABLE users (name, email);
               INSERT INTO users (name, email)
               VALUES ('Sam', 'sam1234@example.com'),
                      ('Joe', 'jjguy@example.com');"
    :tests [{:expected [{"name" "John"}]
             :actual "SELECT DISTINCT name FROM users where name = \"John\";"}]
    :blocks ["\"John\"" "name"]}])

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
   :current-query []
   :query ""
   :result {}
   :completed false
   :correct false
   :show-query-results {}
   :schema {}
   :lessons-info (lessons-info)
   :blocks []
   :mru-blocks `()
   :keyboard-input? false})
