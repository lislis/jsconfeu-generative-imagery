(ns app.mover
  (:require [app.vector :as vector]))

;; mover functions that implement vectors
;; example wrapping edge accelerating ball
;;
;; (defonce state
;;   (atom {:mover (mover/create 400 200 0 0 -0.001 0.01 8)}))
;;
;; (defn draw []
;;   (let [updated-mover (mover/update (:mover @state) state width height)]
;;     (swap! state assoc :mover updated-mover) ; emulates update()
;;     (vector/draw (:location (:mover @state)))))


(defn draw-big [location color]
  (js/noStroke)
  (js/fill color)
  (js/ellipse (:x location) (:y location) (js/random 20 50) (js/random 20 50)))

(defn draw-smol [location color]
  (js/stroke "white")
  (js/strokeWeight 2)
  (js/fill color)
  (js/ellipse (:x location) (:y location) (js/random 10 20) (js/random 10 20)))

(defn draw-dot [location color]
  (js/noStroke)
  (js/fill color)
  (js/ellipse (:x location) (:y location) (js/random 3 5) (js/random 4 6)))

(defn create-vec [l v a topspeed]
  {:location l
   :velocity v
   :acceleration a
   :topspeed topspeed})

(defn create [x y vx vy ax ay topspeed]
  (create-vec
   (vector/create x y)
   (vector/create vx vy)
   (vector/create ax ay)
   topspeed))

(defn seed [num w h]
  (for [i (range num)]
    (let [x (js/random 50 (- w 50))
          y (js/random 50 (- h 50))
          topspeed (js/randomGaussian 3.5 1)
          m (create x y 0 0 0 0 topspeed)]
      (js/console.log x y)
      m)))

(defn create-mass [x y vx vy ax ay topspeed mass]
  (let [mover (create-vec
               (vector/create x y)
               (vector/create vx vy)
               (vector/create ax ay)
               topspeed)]
    (merge mover {:mass mass})))

(defn seed-bounceball [num w h]
  (for [i (range num)]
    (let [x (js/random 20 (- w 20))
          y 30
          topspeed 3
          mass (js/randomGaussian 4 1.5)
          m (create x y 0 0 0 0 topspeed)]
      m)))

(defn wrap-edges "takes location vector" [vec width height]
  (let [x (cond (> (:x vec) width) width
                (< (:x vec) 0) 0
                :else (:x vec))
        y (cond (> (:y vec) height) 0
                (< (:y vec) 0) height
                :else (:y vec))]
    (vector/create x y)))

(defn bounce-edges "takes mover for location and velocity" [mover width height]
  (let [l (:location mover)
        v (:velocity mover)
        a (:acceleration mover)
        t (:topspeed mover)
        vx (if (or (> (:x l) width) (< (:x l) 0))
             (* -1 (:x v))
             (:x v))
        vy (if (or (> (:y l) height) (< (:y l) 0))
             (* -1 (:y v))
             (:y v))
        lx (cond (> (:x l) width) width
                 (< (:x l) 0) 0
                 :else (:x l))
        ly (cond (> (:y l) height) height
                 (< (:y l) 0) 0
                 :else (:y l))]
    (create-vec (vector/create lx ly) (vector/create vx vy) a t)))

(defn updates [mover width height]
  (let [st mover
        a (:acceleration st)
        calc-v (vector/add (:velocity st) a)
        v (vector/limit calc-v (:topspeed st))
        calc-l (vector/add (:location st) v)
        l (wrap-edges calc-l width height)]
    (create-vec l v a (:topspeed mover))))

; I don't think that works
(defn updates-random-acceleration [mover width height]
  (let [st mover
        rand-a (vector/random2d)
        a (vector/mult rand-a 0.2)
        calc-v (vector/add (:velocity st) a)
        v (vector/limit calc-v (:topspeed st))
        calc-l (vector/add (:location st) v)
        l (wrap-edges calc-l width height)]
    (create-vec l v a (:topspeed mover))))


;; I don't think this works either
(defn updates-perlin-acceleration [mover width height xoff yoff]
  (let [st mover
        rand-a (vector/create (js/map (js/noise xoff) 0 1 -1 1) (js/map(js/noise yoff) 0 1 -1 1))
        a (vector/mult rand-a 0.03)
        calc-v (vector/add (:velocity st) a)
        v (vector/limit calc-v (:topspeed st))
        calc-l (vector/add (:location st) v)
        l (wrap-edges calc-l width height)]
    (create-vec l v a (:topspeed mover))))


(defn apply-acceleration "remembers acceleration" [mover acc]
  (let [v (vector/limit (vector/add (:velocity mover) acc) (:topspeed mover))
        l (vector/add (:location mover) v)
        t (:topspeed mover)]
    (create-vec l v acc t)))

(defn apply-acceleration-2 "forgets acceleration" [mover acc width height]
  (let [m (bounce-edges mover width height)
        t (:topspeed m)
        v (vector/limit (vector/add (:velocity m) acc) t)
        l (vector/add (:location m) v)]
    (create-vec l v (vector/create 0 0) t)))

(defn accelerate-to-mouse [mover]
  (let [m-v (vector/create js/mouseX js/mouseY)
        loc (:location mover)
        dir (vector/mult (vector/normalize (vector/sub m-v loc)) 0.5)
        acc dir]
    (apply-acceleration mover acc)))

(defn apply-force [mover force width height]
  (let [mass (:mass mover)
        ;vals (vals force)
        ;mapped (mapv #(vector/div % mass) vals)
        f (reduce vector/add (vals force))
        acc f]
    (apply-acceleration-2 mover acc width height)))
