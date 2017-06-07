(ns mojo-ui.demo
  (:require [mojo-ui.core :refer [button tabs accordion item]]
            [reagent.core :refer [render]])
  (:require-macros [mojo-ui.core :refer [require-css]]))

(require-css "demo")

(defn button-demo
  ""
  []
  [:div#button-demo
   [button {:take-focus true} "Button with ripple and bulge effects."]
   [button "Button with only ripple."]])

(defn tab-demo
  [{:keys [key]}]
  [tabs {:key key}
   [item {:title "Tab 1"}
    [:div.tab-content "tab1 content"]]
   [item {:title "Tab 2"}
    [:div.tab-content "tab2 content"]]
   [item {:title "Tab 3"}
    [:div.tab-content "tab3 content"]]])

(defn dialog-demo
   ""
   []
   )

(defn accordion-demo
  ""
  [{:key [key]}]
  [accordion {:key key}
   [item {:title "Section 1"}
    [:div.tab-content "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum dolor odio, tristique non dui eu, tempus fringilla libero. Sed pretium ex odio, lobortis pharetra dolor eleifend nec. Nullam pellentesque semper."]]
   [item {:title "Section 2"}
    [:div.tab-content "Aenean quis mi est. Nullam blandit aliquet mi nec venenatis. Mauris ante tellus, semper at vehicula sed, ultrices sit amet urna. Suspendisse neque ligula, vulputate quis accumsan in, pellentesque ac."]]
   [item {:title "Section 3"}
    [:div.tab-content "Fusce feugiat erat ac gravida mattis. Phasellus laoreet urna eget metus tempus, nec venenatis metus lacinia. Curabitur eu accumsan risus, vitae vestibulum odio. Curabitur tincidunt velit id ex pulvinar convallis."]]])

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
    [:h1 "Accordion Demo"]
    [accordion-demo]]
   [:div
    [:h1 "Dialog Demo"]
    [dialog-demo]]])

(defn ^:export run-demo
  []
  (render [demo]
          (js/document.getElementById "app")))

(defn on-js-reload []
  (run-demo))
