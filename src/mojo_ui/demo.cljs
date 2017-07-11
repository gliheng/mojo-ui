(ns mojo-ui.demo
  (:require [mojo-ui.core :refer [button tabs accordion view]]
            [mojo-ui.dialog :refer [dialog]]
            [reagent.core :refer [render atom]])
  (:require-macros [mojo-ui.core :refer [require-css]]))

(require-css "demo")

(defn button-demo
  ""
  []
  [:div#button-demo
   [button {:take-focus true} ":take-focus button"]
   [button "simple button"]])

(defn tab-demo
  [{:keys [key]}]
  [tabs {:key key}
   [view {:title "Tab 1"}
    [:div.tab-content "tab1 content"]]
   [view {:title "Tab 2"}
    [:div.tab-content "tab2 content"]]
   [view {:title "Tab 3"}
    [:div.tab-content "tab3 content"]]])

(defonce dialog-open (atom false))

(defn dialog-content
  ""
  []
  [:div "A dialog"])

(defn dialog-demo
  ""
  []
  (let [open dialog-open
        close-dialog (fn [] (reset! open false))
        open-dialog (fn [] (reset! open true))]
    (fn []
      (let [d (if @open
                [dialog {:title "Dialog"
                                 :open @open
                         :on-close close-dialog
                         :content [dialog-content]
                         :actions [[button {:take-focus true
                                            :on-click close-dialog} "OK"]
                                   [button {:take-focus true
                                            :auto-focus true
                                            :on-click close-dialog} "Cancel"]]}])]
        [:div
         [button {:take-focus true
                  :on-click open-dialog} "Open dialog"]
         d]))))

(defn accordion-demo
  ""
  [{:key [key]}]
  [accordion {:key key}
   [view {:title "Section 1"}
    [:div.tab-content "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum dolor odio, tristique non dui eu, tempus fringilla libero. Sed pretium ex odio, lobortis pharetra dolor eleifend nec. Nullam pellentesque semper."]]
   [view {:title "Section 2"}
    [:div.tab-content "Aenean quis mi est. Nullam blandit aliquet mi nec venenatis. Mauris ante tellus, semper at vehicula sed, ultrices sit amet urna. Suspendisse neque ligula, vulputate quis accumsan in, pellentesque ac."]]
   [view {:title "Section 3"}
    [:div.tab-content "Fusce feugiat erat ac gravida mattis. Phasellus laoreet urna eget metus tempus, nec venenatis metus lacinia. Curabitur eu accumsan risus, vitae vestibulum odio. Curabitur tincidunt velit id ex pulvinar convallis."]]])

(defn demo
  []
  [:div.demo
   [:div
    [:h1 "Tab Demo"]
    [tab-demo {:key "tab-demo"}]]
   [:div
    [:h1 "Accordion Demo"]
    [accordion-demo]]
   [:div
    [:h1 "Dialog Demo"]
    [dialog-demo]]
   [:div
    [:h1 "Button Demo"]
    [button-demo]]])

(defn ^:export run-demo
  []
  (render [demo]
          (js/document.getElementById "app")))

(defn on-js-reload []
  (run-demo))
