(ns mojo-ui.core
  (:require [clojure.java.io :refer [resource reader]]
            [clojure.string :refer [join]]
            [mojo-ui.jar-exploder :refer [explode-jar-to-temp]])
  (:import [java.security MessageDigest]
           [java.net URI]))

(def config (atom {:style-root "styles"
                   :style-exts [".scss" ".sass" ".css"]}))

(defn set-config!
  [conf]
  (swap! config
         (fn [old] (merge old conf))))

(defn get-resource-file
  "return file path, explode a jar when necessary"
  [url]
  ;; if url is a resource inside jar, explode that jar,
  ;; return the path in exploded jar
  (if (= (.getProtocol url) "jar")
    (let [path (.getPath url)
          idx (clojure.string/last-index-of path "!")
          jar (subs path 0 idx)
          path (subs path (inc idx))
          tmp (explode-jar-to-temp (.getPath (URI. jar)))]
      (str tmp path))
    (.getPath url)))

(defn compile-sass
  "compile sass file and get output as string"
  [url]
  (let [file (get-resource-file url)
        cmd (str "sassc " file)
        process (.. (Runtime/getRuntime) (exec cmd))
        stream (.getInputStream process)
        err (.getErrorStream process)]
    (.waitFor process)
    (with-open [rdr (reader stream)]
      (join "\n" (line-seq rdr)))))

(defn resolve-file
  [fn type pkg]
  (let [t (name type)
        conf @config
        exts (conf (keyword (str t "-exts")))
        root (conf (keyword (str t "-root")))
        path (if (empty? pkg) [root] [pkg root])
        url (->> exts
                 (map #(resource (clojure.string/join "/" (into path [(str fn %)]))))
                 (filter some?)
                 first)]
    url))

(defn int->hex
  [d]
  (subs (Integer/toString (+ d 0x100), 16) 1))

(defn bytes->string
  ""
  [bytes]
  (join "" (map int->hex bytes)))

(defn md5
  "get md5 for a string and return as string"
  [s]
  (let [d (MessageDigest/getInstance "md5")]
    (bytes->string (.digest d (.getBytes s)))))

(defmacro require-css
  ([css-file]
   `(require-css ~css-file ""))
  ([css-file pkg]
   (if-let [url (resolve-file
                 css-file
                 :style
                 (clojure.string/replace pkg "-" "_"))]
     (let [id (md5 (.getFile url))
           text (compile-sass url)]
       `(do
          (if-let [elem# (js/document.getElementById ~id)]
            (.remove elem#))
          (let [elem# (js/document.createElement "style")]
            (set! (.-id elem#) ~id)
            (set! (.-innerHTML elem#) ~text)
            (js/document.head.appendChild elem#))))
     `(js/console.warn "Cannot find style:" ~css-file))))
