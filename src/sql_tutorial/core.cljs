(ns sql-tutorial.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [devtools.core :as devtools]
            [sql-tutorial.views :refer [problem-layout]]
            [sql-tutorial.handlers]
            [sql-tutorial.db]
            [sql-tutorial.subs]))

(defn sql-tutorial []
  [problem-layout])

;; TODO: call this in index.html
(defn ^:export init[]
  (devtools/install!)
  (re-frame/dispatch-sync [:initialize-db])
  (reagent/render-component [sql-tutorial]
                            (. js/document (getElementById "app"))))

;; TODO: understand this
(defn on-js-reload [])
;; optionally touch your app-state to force rerendering depending on
;; your application
;; (swap! app-state update-in [:__figwheel_counter] inc)
