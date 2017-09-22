(ns mojo-ui.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 ::tab-index
 (fn [db [_ key]]
   (get-in db [:ui (or key :default) :ui-tab-index] 0)))

(reg-sub
 ::accordion-index
 (fn [db [_ key]]
   (get-in db [:ui (or key :default) :ui-accordion-index] #{0})))
