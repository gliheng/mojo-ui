(ns mojo-ui.dialog
  (:require [reagent.core :refer [render unmount-component-at-node]]))

(defonce root (atom))

(defn dialog
  ""
  [{:keys [title content actions on-close]
    :or {on-close (fn [] (unmount-component-at-node @root))}}]
  (let [footer (if (seq actions)
                 (into [:div.ui-footer] actions))]
    [:div.ui-dialog-wrap
     [:div.ui-dialog-outer
      [:div.ui-dialog
       [:div.ui-title
        [:h3 title]
        [:a.ui-close {:href "javascript:void(0)"
                      :on-click on-close}]]
       [:div.ui-content content]
       footer]]
     [:div.ui-bg {:on-click on-close}]]))

(defn open-dialog!
  "Append the dialog to the document."
  [config]
  (if (nil? @root)
    (let [r (js/document.createElement "div")]
      (set! (.-className r) "ui-root-container")
      (js/document.body.appendChild r)
      (reset! root r)))
  (render [dialog config] @root))
