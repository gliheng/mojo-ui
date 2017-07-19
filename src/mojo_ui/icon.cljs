(ns mojo-ui.icon)

(defn font-icon
  ""
  [name]
  [:i {:class (str "fa fa-" name)
       :aria-hidden "true"}])


