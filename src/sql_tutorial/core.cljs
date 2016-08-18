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

(defn ^:export init[]
  ; TODO: call this conditionally based on a debug flag
  (devtools/install!)
  (re-frame/dispatch-sync [:initialize-db])
  (reagent/render-component [sql-tutorial]
                            (. js/document (getElementById "app"))))
