(ns sql-tutorial.views
  (:require-macros [devcards.core :refer [defcard defcard-rg]])
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as reagent]))

; TODO: should you be able to move from correct to incorrect?
;;
; show problem description component
; and communicate problem state
(defn render-problem-description [{:keys [title description completed]}]
  [:div
   [:p title]
   [:p description]
   (if completed [:p "Success!"])])

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
  [{:keys [query result]}]
  (if (empty? query)
    [:p "Enter a command"]
    [:div
      [:pre [:code query]]
      (if (= (first result) :error)
        [:p (.-message (second result))]
        (if (empty? (second result))
          [:p "No results!"]
          [sql-results (second result)]))]))

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

; schema component, update on execute

; query component, update on execute

; TODO visual programming bits

; TODO client-side routing for bookmarking and wutnot
;   or maybe just remember progress in ls

; TODO move between lessons

;;
; app container
(defn problem-layout []
  [:div
    [problem-description]
    [current-query]
    [search-field #(dispatch [:execute %])]])
