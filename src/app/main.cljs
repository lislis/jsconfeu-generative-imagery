(ns app.main)

(def width 1080) ; 1914
(def height 720) ; 1074

(def pink "#E10079")
(def blue "#5F57C4")
(def creme "#F2EBD9")
(def ice "#DDE4F2")
(def steel "#303F62")

(defonce state
  (atom {:movers []}))

(defn setup []
  (js/createCanvas width height))

(defn draw [])


;; start stop pattern as described in
;; https://github.com/thheller/shadow-cljs/wiki/ClojureScript-for-the-browser
(defn start []
  (doto js/window
    (aset "setup" setup)
    (aset "draw" draw))
  (js/console.log "START"))

(defn stop []
  (js/clear)
  (js/console.log "STOP"))

(defn ^:export init []
  (js/console.log "INIT")
  (start))
