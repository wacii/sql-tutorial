(ns sql-tutorial.db)

; TODO remove lessons from app state, this will never change
;   instead provide a getter then maybe a summary
;   but keep the lessons themselves as a const/on page/in ls
; TODO remove id, instead switch on position in vector
; TODO do not duplicate a lesson in current lesson
;   instead just maintain related data
; TODO schema to enforce proper data
(def init-state
  {:lessons
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
      :result {}}]})
