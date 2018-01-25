(ns app.main
  (:require [app.vector :as vector]
            [app.walker :as walker]
            [app.mover :as mover]))

(def width 1440) ; 1914
(def height 800) ; 1074

(def pink "#E10079")
(def blue "#5f57c4")
(def creme "#F2EBD9")
(def ice "#DDE4F2")
(def steel "#303F62")

(defonce state
  (atom {:pink-list []
         :blue-list []
         :ice-list []
         :creme-list []
         :steel-list []}))

(defn setup []
  (js/createCanvas width height)
  (swap! state assoc :pink-list (walker/seed 14 (- width 50) (- height 50)))
  (swap! state assoc :blue-list (walker/seed 14 (- width 50) (- height 50)))
  (swap! state assoc :ice-list (walker/seed 9 (- width 50) (- height 50)))
  (swap! state assoc :creme-list (walker/seed 9 (- width 50) (- height 50)))
  (swap! state assoc :steel-list (walker/seed 15 (- width 50) (- height 50)))
  )

(defn draw []
  (let [pink-list (:pink-list @state)
        blue-list (:blue-list @state)
        ice-list (:ice-list @state)
        creme-list (:creme-list @state)
        steel-list (:steel-list @state)]
    (swap! state assoc :pink-list (mapv walker/walker-step pink-list))
    (swap! state assoc :blue-list (mapv walker/walker-step blue-list))
    (swap! state assoc :ice-list (mapv walker/walker-gaussian ice-list))
    (swap! state assoc :creme-list (mapv walker/walker-gaussian creme-list))
    (swap! state assoc :steel-list (mapv walker/walker-montecarlo steel-list))
    (dorun
     (for [m pink-list]
       (walker/draw-ellip m pink)))
    (dorun
     (for [m blue-list]
       (walker/draw-ellip m blue)))
    (dorun
     (for [i ice-list]
       (walker/draw-ellip i ice)))
    (dorun
     (for [i creme-list]
       (walker/draw-ellip i creme)))
    (dorun
     (for [i steel-list]
       (walker/draw-dot i steel)))))

(defn setup-mouse []
  (js/createCanvas width height)
  (swap! state assoc :pink-list (mover/seed 6 width height))
  (swap! state assoc :blue-list (mover/seed 6 width height))
  (swap! state assoc :ice-list (mover/seed 3 width height))
  (swap! state assoc :creme-list (mover/seed 3 width height))
  (swap! state assoc :steel-list (mover/seed 3 width height)))

(defn draw-mouse []
  (let [pink-list (:pink-list @state)
        blue-list (:blue-list @state)
        ice-list (:ice-list @state)
        creme-list (:creme-list @state)
        steel-list (:steel-list @state)]

    (swap! state assoc :pink-list (mapv mover/accelerate-to-mouse pink-list))
    (swap! state assoc :blue-list (mapv mover/accelerate-to-mouse blue-list))
    (swap! state assoc :ice-list (mapv mover/accelerate-to-mouse ice-list))
    (swap! state assoc :creme-list (mapv mover/accelerate-to-mouse creme-list))
    (swap! state assoc :steel-list (mapv mover/accelerate-to-mouse  steel-list))
    (dorun
     (for [i pink-list]
       (let [location (:location i)]
         (mover/draw-big location pink))))
    (dorun
     (for [i blue-list]
       (let [location (:location i)]
         (mover/draw-big location blue))))
    (dorun
     (for [i ice-list]
       (let [location (:location i)]
         (mover/draw-smol location ice))))
    (dorun
     (for [i creme-list]
       (let [location (:location i)]
         (mover/draw-smol location creme))))
    (dorun
     (for [i steel-list]
       (let [location (:location i)]
         (mover/draw-dot location steel))))
    ))


(defonce env
  (atom {:wind (vector/create 0.0 0)
         :gravity (vector/create 0 0.18)}))

(defn setup-wind []
  (js/createCanvas width height)
  (swap! state assoc :pink-list (mover/seed-bounceball 1 width height)))

(defn draw-wind []
  (js/background 255 255 255 20)
  (let [pink-list (:pink-list @state)]
    (swap! state assoc :pink-list (mapv #(mover/apply-force % @env width height) pink-list))
    (js/noStroke)
    (dorun
     (for [i pink-list]
       (let [location (:location i)]
         (js/fill pink)
         (js/ellipse (:x location) (:y location) (* 1 20) (* 1 20)))))))

;; were used together with key press
(defn accelerate []
  (let [x (:x (:wind @env))
        new-x (+ x 0.01)]
    (js/console.log new-x)
    (swap! env assoc-in [:wind :x] new-x)))

(defn decelerate []
  (let [x (:x (:wind @env))
        new-x (- x 0.01)]
    (js/console.log new-x)
    (swap! env assoc-in [:wind :x] new-x)))

(defn keypressed []
  (let [left 37
        right 39
        up 38
        down 40]
    (condp = js/keyCode
      right (accelerate)
      left (decelerate)
      nil)))


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

(defn ^:export mouse []
  (js/console.log "Hey there")
  (doto js/window
    (aset "setup" setup-mouse)
    (aset "draw" draw-mouse)))

(defn ^:export wind []
  (js/console.log "wind, schoooo")
  (doto js/window
    (aset "setup" setup-wind)
    (aset "keyPressed" keypressed)
    (aset "draw" draw-wind)))
