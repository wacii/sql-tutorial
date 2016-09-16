(ns sql-tutorial.db)

(def blocks-map
  {"Statements" ["SELECT" "FROM" "WHERE" "AND" "OR"
                 "INNER" "LEFT" "OUTER" "JOIN" "ON"
                 "INSERT" "INTO" "VALUES"]
   "Symbols" ["*" "=" "," ";" "<" ">" "(" ")"]})

(def users-setup
  "DROP TABLE IF EXISTS users;
   CREATE TABLE users (first_name, last_name, age int);
   INSERT INTO users (first_name, last_name, age)
   VALUES ('Sam', 'Warren', 17),
          ('John', 'Peters', 32),
          ('Sarah', 'Li', 24),
          ('John', 'Bowers', 41),
          ('Tara', 'Bishop', 36);")

(def lessons
  [{:title "SELECT I"
    :description "
Learn how to write sql queries to fetch records from a database.

The most basic select query takes the form `SELECT [column names] FROM [table name];`. An example against a database with table `users` and columns `name` and `email`: `SELECT email FROM users;`

Now you! Select the names of all users from the database."
    :db-setup "DROP TABLE IF EXISTS users;
               CREATE TABLE users (name, email);
               INSERT INTO users (name, email)
               VALUES ('Sam', 'sam1234@example.com'),
                      ('Joe', 'jjguy@example.com');"
    :tests [{:expected [{"name" "Sam"} {"name" "Joe"}]
             :actual :current}]
    :blocks ["SELECT" "email" "FROM" "users" ";" "name"]}

   {:title "SELECT II"
    :description "
Multiple columns may be specified in a comma-separated list.

Try selecting both `name` and `email` from `users`."
    :db-setup "DROP TABLE IF EXISTS users;
               CREATE TABLE users (name, email);
               INSERT INTO users (name, email)
               VALUES ('Sam', 'sam1234@example.com'),
                      ('Joe', 'jjguy@example.com');"
    :tests [{:expected [{"name" "Sam", "email" "sam1234@example.com"}
                        {"name" "Joe", "email" "jjguy@example.com"}]
             :actual :current}]
    :blocks ["," "name" "email"]}

   {:title "SELECT III"
    :description "
As a shortcut, an asterisk may be used to specify all columns, instead of writing them all out.

Try selecting all columns from `users`."
    :db-setup "DROP TABLE IF EXISTS users;
               CREATE TABLE users (name, email, id int);
               INSERT INTO users (name, email, id)
               VALUES ('Sam', 'sam1234@example.com', 1),
                      ('Joe', 'jjguy@example.com', 2);"
    :tests [{:expected [{"id" 1, "name" "Sam", "email" "sam1234@example.com"}
                        {"id" 2, "name" "Joe", "email" "jjguy@example.com"}]
             :actual :current}]
    :blocks ["*" "users"]}

   {:title "WHERE I"
    :description "
 You generally will want to select only a small portion of the available records: the current day's sales, products in the electronics department under twenty dollars, clients based in Toronto.

 This is done with the `WHERE` keyword. The query for finding people older than 25 might read `SELECT * FROM people WHERE age > 25;`.

 Try selecting all people with first name 'John'."
    :db-setup users-setup
    :tests [{:expected [{"first_name" "John", "last_name" "Peters", "age" 32}
                        {"first_name" "John", "last_name" "Bowers", "age" 41}]
             :actual :current}]
    :blocks ["first_name" "John" "people" "WHERE" "="]}

   {:title "WHERE II"
    :description "
Select records fulfilling multiple conditions with the `AND` keyword.

Try selecting users whose first name is 'John' and is younger than 40."
    :db-setup users-setup
    :tests [{:expected [{"first_name" "John", "last_name" "Peters", "age" 32}]
             :actual :current}]
    :blocks ["age" "first_name" "John" "40" "AND" "<" ">"]}

   {:title "OR"
    :description "
Select records fulfilling at least one of multiple conditions with the `OR` keyword.

Try selecting users younger than 20 or whose first name is 'John'"
    :db-setup users-setup
    :tests [{:expected [{"first_name" "Sam", "last_name" "Warren", "age" 17}
                        {"first_name" "John", "last_name" "Peters", "age" 32}
                        {"first_name" "John", "last_name" "Bowers", "age" 41}]
             :actual :current}]
    :blocks ["age" "first_name" "20" "John" "<" "OR"]}

   {:title "IN"
    :description "
```sql
SELECT * FROM users WHERE id = 3 OR id = 5 OR id = 6 OR id = 11;
```
The previous statement can be more succinctly expressed with the `IN` keyword.
```sql
SELECT * FROM users WHERE id IN (3, 5, 6, 11);}])
```
Try selecting all users with last name Bowers, Peters, or Li."
    :db-setup users-setup
    :tests [{:expected [{"first_name" "Sarah", "last_name" "Li", "age" 24}
                        {"first_name" "John", "last_name" "Peters", "age" 32}
                        {"first_name" "John", "last_name" "Bowers", "age" 41}]
             :actual :current}]
    :blocks ["IN" "Bowers" "Peters" "Li" "(" ")" "," "last_name"]}

   {:title "NOT IN"
    :description "
Any condition may be negated with the `NOT` keyword.

Try selecting users whose ages are not 17, 24, or 36."
    :db-setup users-setup
    :tests [{:expected [{"first_name" "John", "last_name" "Peters", "age" 32}
                        {"first_name" "John", "last_name" "Bowers", "age" 41}]
             :actual :current}]
    :blocks ["NOT" "age" "(17, 24, 36)"]}])

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
   :block-category "Recent"
   :keyboard-input? false})
