(ns cljs-mathbox-example.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.events :as events]
            [cljs.core.async :refer [put! <! >! timeout]]
            [clojure.string :as str]
            [cljs.pprint :refer [pprint]]
            [cljs-mathbox.mathbox :as mb]))

(enable-console-print!)

;; Create a mathbox instance which we can act on:
(let [m (mb/create-mathbox "shaders/snippets.glsl.html" {})]

  ;; Wait a second for mathbox to be loaded  and create an animation :)
  ;; If using React, having this code called from the React lifecycle
  ;; event :component-did-mount is much more elegant than a timeout.

  (go (<! (timeout 200))

      (-> m
          ;; set up the initial camera position
          (mb/camera {:orbit 7.5
                      :phi 1.05
                      :theta 0.3})

          ;; Create a curve
          (mb/curve {:id "my-curve"
                     :domain [-8, 7]
                     :color 0x559900
                     :expression #(.cos js/Math (* 25 %))})

          ;; Create a cube
          (mb/platonic {:type :cube
                        :id "acube"
                        :color 0x903020
                        :line true})

          ;; Animate the cube's color and rotation
          (mb/animate "#acube"
                      {:color 0x8855AA
                       :mathRotation [0 3 0]}
                      {:duration 6000}))

      ;; Getters and setters:
      (print "#acube props: " (mb/get-props m "#acube"))
      (mb/set-props! m "#acube" {:opacity 0.7})

      (<! (timeout 5000))

      ;; Now (after a delay) animate various properties of the cube & curve
      (-> m
          (mb/animate "#acube"
                      {:mathScale [2, 0.2, 0.2]}
                      {:duration 1000})

          (mb/animate "#my-curve"
                      {:color 0xAA4400
                       :mathRotation [2 0 1]
                       :expression #(.sin js/Math (* 40 %))}
                      {:duration 1000}))

      (<! (timeout 1500))

      ;; Clone that crazy curve
      (-> m
          (mb/mb-clone "#my-curve"
                       {:id "my-curve2"
                        :color 0xFF9955
                        :expression #(.cos js/Math (* 40 %))
                        :mathRotation [1 0 2]}
                       {:duration 1000}))

      ;; Show some info about the mathbox instance:
      (->> m
           .viewport
           js->clj
           ((fn [m] (m "__attributes")))
           pprint)))
