(ns sql-tutorial.views
  (:require-macros [devcards.core :refer [defcard defcard-rg]])
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as reagent]))

;;
; markdown
(defn markdown [raw]
  [:div {:dangerouslySetInnerHTML {:__html (.render js/md (str raw))}}])

;;
; show problem description component
(defn render-problem-description [{:keys [title description]}]
  [:section
   [:h1 title]
   [markdown description]])

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
    [:p "No query"]
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
      [:form.prompt {:on-submit submit}
        [:input {:type "text", :value @value, :on-change update}]
        [:div.keyboard-toggle {:on-click #(dispatch [:toggle-input-style])}
          [:i.material-icons "keyboard"]]])))

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

;;
; lesson completion
(defn render-lesson-finished [{:keys [completed correct next-lesson]}]
  [:div.status
    [:div.complete (if completed "Completed!" "Not Completed!")]
    [:div.correct (if correct "Correct!" "Incorrect!")]
    (if (and completed next-lesson)
      [:button {:on-click #(dispatch [:change-lesson next-lesson])} "Next"])])

(defn lesson-finished []
  (let [sub (subscribe [:completed])]
    (render-lesson-finished @sub)))

;;
; visual programming
(defn block
  ([word]
   [block word #(dispatch [:push word])])
  ([word submit]
   [:a {:on-click submit} word]))

(defn block-group [words]
  [:div.keyboard
    [:div.div
      (for [word words]
        ^{:key word} [block word])]])

(defn current-query [query-blocks]
  [:div.prompt
    [:input {:disabled true
             :placeholder "|"
             :value (clojure.string/join " " query-blocks)}]
    [:div.keyboard-toggle {:on-click #(dispatch [:toggle-input-style])}
      [:i.material-icons "keyboard"]]])

(defn render-block-container
  [{:keys [query-blocks block-groups block-category]}]
  [:div
    [current-query query-blocks]
    [:div.nav-wrap
      [:nav
        (for [category (keys block-groups)]
          ^{:key category}
          [:a {:class (if (= category block-category) "active")
               :on-click #(dispatch [:change-block-category category])}
            category])]]
    [block-group (get block-groups block-category)]
    [:div.commands
      ^{:key "pop"} [:button.button-gray {:on-click #(dispatch [:pop])} "Delete"]
      ^{:key "clear"} [:button.button-gray {:on-click #(dispatch [:clear])} "Clear"]
      ^{:key "run"} [:button.button-orange {:on-click #(dispatch [:run])} "Run"]]])
(defn block-container []
  (let [sub (subscribe [:blocks])]
    (render-block-container @sub)))

(defn code-input []
  (let [keyboard-input? (subscribe [:keyboard-input?])]
    (fn []
      (if @keyboard-input?
        [search-field #(dispatch [:execute %])]
        [block-container]))))

;;
; app container
(defn problem-layout []
  [:main.container
    [:header
      [:div.container
        [:a (goog.string/unescapeEntities "&#9776;")]]]
    [lesson-select #(dispatch [:change-lesson %])]
    [problem-description]
    [previous-query]
    [lesson-finished]
    [code-input]])
