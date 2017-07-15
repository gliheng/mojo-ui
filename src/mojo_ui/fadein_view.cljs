(ns mojo-ui.fadein-view
  (:require [reagent.core :as reagent]
            [goog.events :as events]))

(defn- get-height
  [this]
  (let [node (reagent/dom-node this)
        height (.. node
                   -firstElementChild
                   -clientHeight)]
    height))

(def fadein-view
  (reagent/create-class
   {:get-initial-state
    (fn [this] {:hidden true
                :height 0})

    :component-will-appear
    (fn [cbk]
      (this-as this
        (reagent/set-state this {:hidden false})
        (cbk)))

    :component-will-enter
    (fn [cbk]
      (this-as this
        (let [node (reagent/dom-node this)
              height (get-height this)]
          (reagent/set-state this {:height height})
          (events/listenOnce node "transitionend" cbk))))

    :component-did-enter
    (fn [] (this-as this
             (reagent/set-state this {:hidden false :height 0})))

    :component-will-leave
    (fn [cbk]
      (this-as this
        (let [node (reagent/dom-node this)
              height (get-height this)]
          (reagent/set-state this {:hidden :true :height height})
          (reagent/flush) ;; apply this height to dom before transition
          (reagent/after-render
           (fn []
             (reagent/set-state this {:height 0})
             (events/listenOnce node "transitionend" cbk))))))

    :render
    (fn [this]
      (let [state (reagent/state this)
            hidden? (:hidden state)
            height (:height state)
            style (if hidden?
                    #js {:height height :opacity (if (= height 0) 0 1)}
                    #js {})]
        [:div {:class (if hidden? "ui-state-hidden" "")
               :style style}
         (first (reagent/children this))]))}))

