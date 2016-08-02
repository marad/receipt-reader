(set-env!
 :source-paths #{"src/clj" "src/java"}
 :resource-paths #{"resources"}
 :dependencies '[[org.clojure/clojure "1.8.0"]
                 [mount "0.1.10"]
                 [seesaw "1.4.5"]
                 [quil "2.4.0"]])

(task-options!
 pom {:project 'list-reader
      :version "0.0.1-alpha"}
 aot {:namespace '#{lists.core}}
 jar {:main 'lists.core})

(deftask build []
  (comp (javac) (aot) (pom) (uber) (jar)))
