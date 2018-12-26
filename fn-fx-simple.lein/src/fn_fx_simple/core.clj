(ns fn-fx-simple.core
  (:require [fn-fx.fx-dom :as dom]
            [fn-fx.diff :refer [component defui render should-update?]]
            [fn-fx.controls :as ui])
  (:gen-class :extends
              javafx.application.Application))

(defui Button
  (render [this args]
          (ui/stack-pane
           :children [(ui/button
                       :text (:text args)
                       :on-action {:event :button-press})])))

(defn force-exit
  []
  (reify javafx.event.EventHandler
    (handle [this event]
      (shutdown-agents)
      (javafx.application.Platform/exit))))

(defui Stage
  (render [this args]
          (ui/stage
           :title "Hello World !"
           :on-close-request (force-exit)
           :shown true
           :scene (ui/scene :root (button args)
                            :width 300
                            :height 250))))
 
(defn -start
  [& args]
  (let [data-state (atom {:text "Say Hello"})
        handler-fn  (fn [{:keys [event]}]
                      (println "UI Event " event)
                      (println "Hello World")
                      (swap! data-state assoc :text "Said Hello"))
        ui-state (agent (dom/app
                         (stage @data-state)
                         handler-fn))]
    (add-watch data-state
               :ui
               (fn [_ _ _ _]
                 (send ui-state (fn [old-ui]
                                  (dom/update-app old-ui
                                                  (stage @data-state))))))))

(defn start-javafx
  [& args]
  (javafx.application.Application/launch fn_fx_simple.core
                                         (into-array String args)))
