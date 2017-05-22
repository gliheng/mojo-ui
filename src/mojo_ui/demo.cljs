(ns mojo-ui.demo
  (:require [mojo-ui.core :refer [button tabs tab]]
            [reagent.core :refer [render]])
  (:require-macros [mojo-ui.core :refer [require-css]]))

(require-css "demo")

(defn button-demo
  ""
  []
  [button "Button with ripple effect."])

(defn tab-demo
  [{:keys [key]}]
  [tabs {:key key}
   [tab {:title "Tab 1"}
    [:div.tab-content "tab1 content"]]
   [tab {:title "Tab 2"}
    [:div.tab-content "tab2 content"]]
   [tab {:title "Tab 3"}
    [:div.tab-content "tab3 content"]]])

(defn dialog-demo
   ""
   []
   )

(defn accordion-demo
  ""
  []
  )

(defn demo
  []
  [:div.demo
   [:div
    [:h1 "Tab Demo"]
    [tab-demo {:key "tab-demo"}]]
   [:div
    [:h1 "Button Demo"]
    [button-demo]]
   [:div
    [:h1 "Dialog Demo"]
    [dialog-demo]]
   [:div
    [:h1 "Accordion Demo"]
    [accordion-demo]]])

(defn ^:export run-demo
  []
  (render [demo]
          (js/document.getElementById "app")))

(defn on-js-reload []
  (run-demo))
