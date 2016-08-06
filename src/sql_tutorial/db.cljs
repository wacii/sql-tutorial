(ns sql-tutorial.db)

(def init-state
  {:title "Basic Queries"
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
   :result {}})
