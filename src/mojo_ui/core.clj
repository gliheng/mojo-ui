(ns mojo-ui.core
  (:require [clojure.java.io :refer [resource reader input-stream copy]]
            [clojure.string :refer [join]])
  (:import [java.security MessageDigest]
           [java.io File]
           [java.util.zip ZipFile]
           [java.net URI]))

;; (import '[java.io File]
;;         '[java.util.zip ZipFile])
;; (require '[clojure.java.io :refer [copy]])

(def config (atom {:style-root "styles"
                   :style-exts [".scss" ".sass" ".css"]}))

(defn set-config!
  [conf]
  (swap! config
         (fn [old] (merge old conf))))

(defn save-entry
  "write entry to a new directory"
  [zip entry root]
  (let [name (.getName entry)
        tar (File. root name)]
    (if-not (.isDirectory entry)
      (let [parent (.getParentFile tar)]
        (.mkdirs parent)
        (.createNewFile tar)
        (copy (.getInputStream zip entry) tar)))))

(defn explode-jar
  "explode a jar to a temp directory return it's path.
  Delete tmp directory when jvm exits"
  [jar root]
  ;; (println "exploding jar" jar "to" root)
  (let [root-file (File. root)
        file (File. jar)
        zip (ZipFile. file)
        entries (loop [entries (.entries zip)
                       files []]
                  (if (.hasMoreElements entries)
                    (recur entries (conj files (.nextElement entries)))
                    files))]
    (.mkdir root-file)
    (doseq [entry entries]
      (save-entry zip entry root))))

;; exploded jars are put here temporarily
(def *temp-root* (File. (System/getProperty "java.io.tmpdir")
                        (str (java.util.UUID/randomUUID))))
(.mkdir *temp-root*)
(.deleteOnExit *temp-root*)

(def records (atom #{}))
(defn explode-jar-to-temp
  "if the jar has been exploded, it's a no-op"
  [jar]
  (let [jarname (.getName (File. jar))
        temp-root (.getCanonicalPath *temp-root*)
        temp-dir (.getCanonicalPath (File. temp-root jarname))]
    (when-not (@records jar)
      (swap! records conj jar)
      (explode-jar jar temp-dir))
    temp-dir))

(defn get-resource-file
  "return file path, explode a jar when necessary"
  [url]
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

(defmacro require-css-pkg
  [css-file pkg]
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
    `(js/console.warn "Cannot find style:" ~css-file)))

(defmacro require-css
  [css-file]
  `(require-css-pkg ~css-file ""))
