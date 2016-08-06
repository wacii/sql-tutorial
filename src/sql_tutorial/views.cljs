(ns sql-tutorial.views
  (:require-macros [devcards.core :refer [defcard-rg deftest]])
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
   (for [value record] [:td value])])

(defn sql-results [results]
  ; FIXME how do you want to represent data?
  ; results is in data format returned by sql, not actually a map
  (let [keys (-> results (first) (keys))
        rows (map vals results)]
    [:table
      [:thead
        [:tr (for [key keys] [:th key])]]
      [:tbody (for [row rows] [sql-record row])]]))

; TODO what should the default results be?
;;
; show command and results
(defn render-current-query [{:keys [query result]}]
  [:div
    [:pre [:code query]]
    (if (= (first result) :error)
      [:p (.-message (second result))]
      [sql-results (second result)])])

(defn current-query []
  (let [query (subscribe [:current-query])]
    (fn [] [render-current-query @query])))

; TODO has to be a more streamlined way to do this
;   it seems a very basic sort of thing to do
;;
; input command
(defn command-input []
  (let [statement (reagent/atom "")
        submit-form (fn [event]
                      (.preventDefault event)
                      (dispatch [:execute @statement])
                      (reset! statement "")
                      nil)
        update-statement (fn [event]
                           (reset!
                             statement
                             (-> event .-target .-value))
                           nil)]
    (fn []
      [:form {:on-submit submit-form}
        [:input {:type "text"
                 :value @statement
                 :on-change update-statement}]])))

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
    [command-input]])
