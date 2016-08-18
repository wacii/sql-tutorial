(ns sql-tutorial.views
  (:require-macros [devcards.core :refer [defcard defcard-rg]])
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as reagent]))

;;
; show problem description component
(defn render-problem-description [{:keys [title description]}]
  [:div
   [:p title]
   [:p description]])

(defn problem-description []
  (let [lesson-summary (subscribe [:current-lesson])]
    (fn [] [render-problem-description @lesson-summary])))

(defcard-rg problem-description
  (render-problem-description
   {:title "asdf", :description "pie", :completed true}))

;;
; sql results as table
(defn sql-record [record]
  [:tr
    (for [value record]
      ^{:key value} [:td value])])

(defn sql-results [results]
  (let [keys (-> results (first) (keys))
        rows (map vals results)]
    [:table
      [:thead
        [:tr
          (for [key keys]
            ^{:key key} [:th key])]]
      [:tbody
        (for [row rows]
          ^{:key row} [sql-record row])]]))

(defcard-rg sql-results
  (sql-results [{:id 1, :name "Sam"} {:id 2, :name "Jane"}]))

;;
; show command and results
(defn render-previous-query
  "Display query and its results, or a no results message."
  [{:keys [query result error]}]
  (if (empty? query)
    [:p "Enter a command"]
    [:div
      [:pre [:code query]]
      (if (empty? error)
        (if (empty? result)
          [:p "No results!"]
          [sql-results result])
        [:p error])]))

(defn previous-query []
  (let [sub (subscribe [:previous-query])]
    (fn [] [render-previous-query @sub])))

; TODO: maintain current query on global state to mirror blocks ???
; TODO: up/down arrow through command history
;;
; input command
(defn search-field [on-submit]
  (let [value (reagent/atom "")
        submit (fn [event]
                 (.preventDefault event)
                 (on-submit @value)
                 (reset! value "")
                 nil)
        update (fn [event]
                 (reset! value (-> event .-target .-value)))]

    (fn []
      [:form {:on-submit submit}
        [:input {:type "text", :value @value, :on-change update}]])))

(defcard search-field
  (reagent/as-element [search-field #(js/alert %)]))

;;
; select lesson
(defn render-lesson-select [on-change [value lessons]]
  (let [update (fn [event]
                 (on-change (-> event .-target .-value int))
                 nil)]
    [:select {:value value, :on-change update}
      (for [lesson lessons
            :let [id (:id lesson)]]
        ^{:key id} [:option {:value id} (:title lesson)])]))

(defn lesson-select [change-lesson]
  (let [sub (subscribe [:lessons])]
    (fn [] [render-lesson-select change-lesson @sub])))

(defcard lesson-select
  (reagent/as-element [render-lesson-select
                        (do)
                        [2 [{:id 1 :title "SELECT"}
                            {:id 2 :title "UPDATE"}
                            {:id 3 :title "INSERT INTO"}]]]))

; TODO next problem button, linked to completed and corrected
;;
; lesson completion
(defn render-lesson-finished [{:keys [completed correct]}]
  [:div
    [:p (if completed "Completed!" "Not Completed!")]
    [:p (if correct "Correct!" "Incorrect!")]])

(defn lesson-finished []
  (let [sub (subscribe [:completed])]
    (render-lesson-finished @sub)))

;;
; visual programming
(def blocks-map
  {:select ["AND" "FROM" "OR" "SELECT" "UNION" "WHERE"]
   :joins ["LEFT" "INNER" "JOIN" "ON" "OUTER" "RIGHT"]
   :symbols ["*" "=" "," ";" "<" ">" "(" ")"]
   :insert ["INSERT" "INTO" "MERGE" "VALUES"]})

(defn block [word submit]
  [:button {:on-click submit} word])

(defn block-group [words]
  [:ul.inline
    (for [word words]
      ^{:key word} [:li [block word #(dispatch [:push word])]])])

; TODO render the table blocks differently with a different color or something
;   maybe provide children to block-group and have those rendered first
(defn render-schema-blocks [schema]
  [:div
    (for [table schema]
      ^{:key (first table)} [block-group (flatten table)])])
(defn schema-blocks []
  (let [sub (subscribe [:schema])]
    (render-schema-blocks @sub)))

(defn render-block-container [blocks]
  [:div
    [:ul.inline
      ^{:key "pop"} [:li [block "delete" #(dispatch [:pop])]]
      ^{:key "clear"} [:li [block "clear" #(dispatch [:clear])]]
      ^{:key "run"} [:li [block "run" #(dispatch [:run])]]]
    [block-group blocks]
    (for [group blocks-map]
      ^{:key (-> group (first) (str))} [block-group (second group)])
    [schema-blocks]])
(defn block-container []
  (let [sub (subscribe [:blocks])]
    (render-block-container @sub)))

(defn render-current-query [current-query]
  [:p (clojure.string/join " " current-query)])
(defn current-query []
  (let [sub (subscribe [:current-query])]
    (render-current-query @sub)))

; schema component, update on execute

; query component, update on execute

;;
; app container
(defn problem-layout []
  [:div
    [lesson-select #(dispatch [:change-lesson %])]
    [problem-description]
    [lesson-finished]
    [previous-query]
    [search-field #(dispatch [:execute %])]
    [block-container]
    [current-query]])
