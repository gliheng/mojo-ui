(ns mojo-ui.events
  (:require [re-frame.core :refer [reg-event-db]]))

(reg-event-db
 ::change-tab-index
 (fn [db [_ key id]]
   (assoc-in db [:ui (or key :default) :ui-tab-index] id)))


(reg-event-db
 ::change-accordion-index
 (fn [db [_ key id]]
   (update-in db [:ui (or key :default) :ui-accordion-index]
              (fn [item]
                (let [item (or item #{0})]
                  ((if (contains? item id)
                     disj
                     conj) item id))))))
