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
(defn render-current-query
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
        [:p (.-message error)])]))

(defn current-query []
  (let [sub (subscribe [:current-query])]
    (fn [] [render-current-query @sub])))

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
(defn render-lesson-finished [{:keys [completed correct]}]
  [:div
    [:p (if completed "Completed!" "Not Completed!")]
    [:p (if correct "Yes" "Not yet")]])

(defn lesson-finished []
  (let [sub (subscribe [:completed])]
    (render-lesson-finished @sub)))

; schema component, update on execute

; query component, update on execute

; TODO visual programming bits

;;
; app container
(defn problem-layout []
  [:div
    [lesson-select #(dispatch [:change-lesson %])]
    [problem-description]
    [lesson-finished]
    [current-query]
    [search-field #(dispatch [:execute %])]])
